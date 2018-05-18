package org.nypl.simplifiedpspdfkit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.pspdfkit.PSPDFKit;
import com.pspdfkit.bookmarks.Bookmark;
import com.pspdfkit.bookmarks.BookmarkProvider;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.exceptions.PSPDFKitInitializationFailedException;
import com.pspdfkit.listeners.DocumentListener;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import org.jetbrains.annotations.NotNull;
import org.nypl.pdfrendererprovider.PDFAnnotation;
import org.nypl.pdfrendererprovider.PDFBookmark;
import org.nypl.pdfrendererprovider.PDFConstants;
import org.nypl.pdfrendererprovider.PDFRendererListener;
import org.nypl.pdfrendererprovider.PDFRendererProviderInterface;
import org.nypl.pdfrendererprovider.broadcaster.PDFBroadcaster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Matt on 3/2/2018.
 */

public class SimplifiedPDFActivity extends PdfActivity implements DocumentListener {

    public SimplifiedPDFActivity() {
    }

    private static int[] bookmarksToCreate;
    private static int documentId;
    private Menu menu;
    private PdfDocument document;
    private BookmarkProvider bookmarkProvider;

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
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onBackPressed() {
        //create an intent with any data you want the host to get, and add that to "setResult()" method
//        Intent returnIntent = new Intent();
//        String uri = document.getUid();
//        int pageRead = getPageIndex();
//        returnIntent.putExtra(PDFConstants.Companion.getPDF_PAGE_READ_EXTRA(), pageRead);
//        returnIntent.putExtra(PDFConstants.Companion.getPDF_ID_EXTRA(), this.documentId);
//        setResult(RESULT_OK, returnIntent);

        super.onBackPressed();
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        int pageRead = getPageIndex();
        returnIntent.putExtra(PDFConstants.Companion.getPDF_PAGE_READ_EXTRA(), pageRead);
        returnIntent.putExtra(PDFConstants.Companion.getPDF_ID_EXTRA(), this.documentId);
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDocumentLoaded(@NonNull PdfDocument document) {
        super.onDocumentLoaded(document);

        this.document = getDocument();
        this.bookmarkProvider = document.getBookmarkProvider();

        if (bookmarksToCreate != null && bookmarksToCreate.length > 0) {
            List<Bookmark> currentBookmarks = this.bookmarkProvider.getBookmarks();

            for (int i = 0; i < bookmarksToCreate.length; i++) {
                int bookmarkPage = bookmarksToCreate[i];
                if (!containsBookmarkForPage(currentBookmarks, bookmarkPage)) {
                    if (bookmarkPage > 0) {
                        this.bookmarkProvider.addBookmark(new Bookmark(bookmarkPage));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onPageChanged(@NonNull PdfDocument document, int pageIndex) {
        super.onPageChanged(document, pageIndex);
        setBookmarkIcon(pageIndex);
        sendPageChangedMessage(pageIndex);
    }

    private void sendPageChangedMessage(int pageIndex){
        Intent intent = new Intent(PDFBroadcaster.Companion.getBROADCAST_EVENT_NAME());
        intent.putExtra(PDFConstants.Companion.getPDF_PAGE_READ_EXTRA(), pageIndex);
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

    private Set<PDFBookmark> bookmarksToPDFBookmark(List<Bookmark> bookmarks) {
//        var convertedBookmarks : MutableSet<PDFBookmark> = mutableSetOf()
//        for (appBookmark in bookmarks){
//            convertedBookmarks.add(PDFBookmark(appBookmark.pageNumber))
//        }
//
//        return convertedBookmarks.toSet()
        Set<PDFBookmark> convertedBookmarks = new HashSet<PDFBookmark>();
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

        setBookmarkIcon(bookmarkPage);
    }

    private int[] bookmarksToIntArray(List<Bookmark> bookmarks) {
        // https://stackoverflow.com/a/965289/2107568
        int[] bookmarkArray = new int[bookmarks.size()];
        int i = 0;
        for (Bookmark bookmark : bookmarks) {
            bookmarkArray[i++] = bookmark.getPageIndex();
        }

        return bookmarkArray;
    }

    private int[] pdfBookmarkToIntArray(Set<PDFBookmark> set) {
        int[] ret = new int[set.size()];
        int i = 0;
        for (PDFBookmark bookmark : set) {
            ret[i] = bookmark.getPageNumber();
            i++;
        }

        return ret;
    }

}
