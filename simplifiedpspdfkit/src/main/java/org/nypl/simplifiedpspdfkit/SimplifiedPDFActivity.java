package org.nypl.simplifiedpspdfkit;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.AnnotationProvider;
import com.pspdfkit.annotations.AnnotationType;
import com.pspdfkit.annotations.TextMarkupAnnotation;
import com.pspdfkit.bookmarks.Bookmark;
import com.pspdfkit.bookmarks.BookmarkProvider;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.listeners.DocumentListener;
import com.pspdfkit.ui.PdfActivity;

import org.nypl.pdfrendererprovider.PDFAnnotation;
import org.nypl.pdfrendererprovider.PDFBookmark;
import org.nypl.pdfrendererprovider.PDFConstants;
import org.nypl.pdfrendererprovider.broadcaster.PDFBroadcaster;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by Matt on 3/2/2018.
 */

public class SimplifiedPDFActivity extends PdfActivity implements DocumentListener {

    public SimplifiedPDFActivity() {
    }

    private static final String TAG = SimplifiedPDFActivity.class.getName();
    private static final Gson GSON = new Gson();

    private static int[] bookmarksToCreate;
    private static int[] annotationsToCreate;
    private static int documentId;
    private Menu menu;
    private BookmarkProvider bookmarkProvider;
    private AnnotationProvider annotationProvider;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getMenuInflater().inflate(org.nypl.simplifiedpspdfkit.R.menu.bookmark_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            int documentId = intent.getIntExtra(PDFConstants.Companion.getPDF_ID_EXTRA(), -1);
            if (documentId >= 0) {
                this.documentId = documentId;
            }

            ArrayList<PDFBookmark> bookmarksExtra = intent.getParcelableArrayListExtra(PDFConstants.Companion.getPDF_BOOKMARKS_EXTRA());
            if (bookmarksExtra != null && bookmarksExtra.size() > 0) {
                bookmarksToCreate = pdfBookmarkToIntArray(bookmarksExtra);
            }

            ArrayList<PDFAnnotation> annotationsExtra = intent.getParcelableArrayListExtra(PDFConstants.Companion.getPDF_ANNOTATIONS_EXTRA());
            if (annotationsExtra != null && annotationsExtra.size() > 0) {
                annotationsToCreate = pdfAnnotationToPSPDFAnnotation(annotationsExtra);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDocumentLoaded(@NonNull PdfDocument document) {
        super.onDocumentLoaded(document);

        this.bookmarkProvider = document.getBookmarkProvider();
        this.annotationProvider = document.getAnnotationProvider();


        if (bookmarksToCreate != null && bookmarksToCreate.length > 0) {
            List<Bookmark> currentBookmarks = this.bookmarkProvider.getBookmarks();

            for (int i = 0; i < bookmarksToCreate.length; i++) {
                int bookmarkPage = bookmarksToCreate[i];
                if (!containsBookmarkForPage(currentBookmarks, bookmarkPage)) {
                    if (bookmarkPage > 0) {
                        this.bookmarkProvider.addBookmark(new Bookmark(bookmarkPage));
                    }
                }
            }
        }
        document.getAnnotationProvider().addOnAnnotationUpdatedListener(new AnnotationProvider.OnAnnotationUpdatedListener() {
            @Override
            public void onAnnotationCreated(@NonNull Annotation annotation) {
                Log.i(TAG, "The annotation was created.");
                sendAnnotationsChangedMessage();
            }

            @Override
            public void onAnnotationUpdated(@NonNull Annotation annotation) {
                Log.i(TAG, "The annotation was updated.");
                sendAnnotationsChangedMessage();
            }

            @Override
            public void onAnnotationRemoved(@NonNull Annotation annotation) {
                Log.i(TAG, "The annotation was removed.");
                sendAnnotationsChangedMessage();
            }
        });

        if (annotationsToCreate != null && annotationsToCreate.length > 0) {
            // this.annotationProvider.addAnnotationToPage(new AssetAnnotation());
        }
    }

    @Override
    public void onPageChanged(@NonNull PdfDocument document, int pageIndex) {
        super.onPageChanged(document, pageIndex);
        setBookmarkIcon(pageIndex);
        sendPageChangedMessage(pageIndex);
    }

    private void sendPageChangedMessage(int pageIndex) {
        Intent intent = new Intent(PDFBroadcaster.Companion.getPAGE_CHANGED_BROADCAST_EVENT_NAME());
        intent.putExtra(PDFConstants.Companion.getPDF_PAGE_READ_EXTRA(), pageIndex);
        intent.putExtra(PDFConstants.Companion.getPDF_ID_EXTRA(), this.documentId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBookmarksChangedMessage() {
        ArrayList<PDFBookmark> bookmarks = bookmarksToPDFBookmark(bookmarkProvider.getBookmarks());
        Intent intent = new Intent(PDFBroadcaster.Companion.getBOOKMARKS_CHANGED_BROADCAST_EVENT_NAME());
        intent.putExtra(PDFConstants.Companion.getPDF_BOOKMARKS_EXTRA(), bookmarks);
        intent.putExtra(PDFConstants.Companion.getPDF_ID_EXTRA(), this.documentId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendAnnotationsChangedMessage() {
//        ArrayList<PDFAnnotation> annotations =
//                annotationsToPDFAnnotation(annotationProvider.getAllAnnotationsOfType(EnumSet.allOf(AnnotationType.class)).toList().blockingGet());

        // Observable conversion example: https://pspdfkit.com/guides/android/current/annotations/introduction-to-annotations/
        final Observable<Annotation> annotationObservable = annotationProvider.getAllAnnotationsOfType(EnumSet.of(AnnotationType.HIGHLIGHT, AnnotationType.UNDERLINE));

        // This will asynchronously read all annotations, cast them and return them as a List.
        annotationObservable
                .cast(TextMarkupAnnotation.class)
                .toList() // Collect all annotations into a List.
                .observeOn(AndroidSchedulers.mainThread()) // Receive all annotations on the main thread.
                .subscribe(new Consumer<List<TextMarkupAnnotation>>() {
                    @Override
                    public void accept(List<TextMarkupAnnotation> noteAnnotations) {
                        // This is called on the main thread.
                        broadcastAnnotationChanges(noteAnnotations);
                    }
                });
    }

    private void broadcastAnnotationChanges(List<TextMarkupAnnotation> annotations) {
        Intent intent = new Intent(PDFBroadcaster.Companion.getANNOTATIONS_CHANGED_BROADCAST_EVENT_NAME());
        intent.putExtra(PDFConstants.Companion.getPDF_ANNOTATIONS_EXTRA(), annotationsToPDFAnnotation(annotations));
        intent.putExtra(PDFConstants.Companion.getPDF_ID_EXTRA(), this.documentId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean handled = false;

        if (item.getItemId() == org.nypl.simplifiedpspdfkit.R.id.bookmark_item) {
            handled = true;
            toggleBookmark(getPageIndex());
        }

        return handled || super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        int pageRead = getPageIndex();
        returnIntent.putExtra(PDFConstants.Companion.getPDF_ID_EXTRA(), this.documentId);
        returnIntent.putExtra(PDFConstants.Companion.getPDF_PAGE_READ_EXTRA(), pageRead);
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }

    private ArrayList<PDFAnnotation> annotationsToPDFAnnotation(List<TextMarkupAnnotation> annotations) {
        ArrayList<PDFAnnotation> convertedAnnotations = new ArrayList<>();
        for (TextMarkupAnnotation annotation : annotations) {
            RectF boundingBox = annotation.getBoundingBox();
            Log.w(TAG, boundingBox.toString());
            Log.w(TAG, boundingBox.toShortString());
            List<RectF> rects = annotation.getRects();

            ArrayList<String> convertedRects = new ArrayList<>(rects.size());
            for (RectF rect : rects) {
                convertedRects.add(GSON.toJson(rect));
            }

            convertedAnnotations.add(
                    new PDFAnnotation(
                            annotation.getPageIndex(),
                            annotation.getType().toString(),
                            GSON.toJson(boundingBox),
                            convertedRects,
                            String.valueOf(annotation.getColor()),
                            String.valueOf(annotation.getAlpha())
                    )
            );
        }

        return convertedAnnotations;
    }

    private ArrayList<PDFBookmark> bookmarksToPDFBookmark(List<Bookmark> bookmarks) {
        ArrayList<PDFBookmark> convertedBookmarks = new ArrayList<>();
        for (Bookmark bookmark : bookmarks) {
            convertedBookmarks.add(new PDFBookmark(bookmark.getPageIndex()));
        }

        return convertedBookmarks;
    }


    private boolean containsBookmarkForPage(List<Bookmark> bookmarks, int page) {
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getPageIndex() == page) {
                return true;
            }
        }

        return false;
    }

    private Bookmark getBookmarkForPage(List<Bookmark> bookmarks, int page) {
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getPageIndex() == page) {
                return bookmark;
            }
        }

        return null;
    }

    private int[] pdfAnnotationToPSPDFAnnotation(ArrayList<PDFAnnotation> annotationsExtra) {
        int[] ret = new int[annotationsExtra.size()];
        int i = 0;
        for (PDFAnnotation annotation : annotationsExtra) {
            ret[i] = annotation.getPageNumber();
            i++;
        }

        return ret;
    }

    private int[] pdfBookmarkToIntArray(ArrayList<PDFBookmark> bookmarksExtra) {
        int[] ret = new int[bookmarksExtra.size()];
        int i = 0;
        for (PDFBookmark bookmark : bookmarksExtra) {
            ret[i] = bookmark.getPageNumber();
            i++;
        }

        return ret;
    }

    private void setBookmarkIcon(int pageIndex) {
        // If this hasn't been initialized we don't want to trigger this.
        if (menu == null) {
            return;
        }

        List<Bookmark> currentBookmarks = this.bookmarkProvider.getBookmarks();
        MenuItem menuItem = menu.findItem(org.nypl.simplifiedpspdfkit.R.id.bookmark_item);
        if (containsBookmarkForPage(currentBookmarks, pageIndex)) {
            menuItem.setIcon(org.nypl.simplifiedpspdfkit.R.drawable.ic_bookmark);
        } else {
            menuItem.setIcon(org.nypl.simplifiedpspdfkit.R.drawable.ic_bookmark_outline);
        }
    }

    private void toggleBookmark(int bookmarkPage) {
        List<Bookmark> currentBookmarks = this.bookmarkProvider.getBookmarks();
        if (!containsBookmarkForPage(currentBookmarks, bookmarkPage)) {
            if (bookmarkPage > 0) {
                this.bookmarkProvider.addBookmark(new Bookmark(bookmarkPage));
            }
        } else {
            Bookmark bookmarkToRemove = getBookmarkForPage(currentBookmarks, bookmarkPage);
            if (bookmarkToRemove == null) {
                return;
            } else {
                this.bookmarkProvider.removeBookmark(bookmarkToRemove);
            }
        }

        sendBookmarksChangedMessage();
        setBookmarkIcon(bookmarkPage);
    }
}
