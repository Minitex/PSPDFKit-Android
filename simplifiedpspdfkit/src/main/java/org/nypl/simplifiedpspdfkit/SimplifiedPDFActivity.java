package org.nypl.simplifiedpspdfkit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.pspdfkit.PSPDFKit;
import com.pspdfkit.bookmarks.Bookmark;
import com.pspdfkit.bookmarks.BookmarkProvider;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.exceptions.PSPDFKitInitializationFailedException;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.util.List;

/**
 * Created by Matt on 3/2/2018.
 */

public final class SimplifiedPDFActivity extends PdfActivity {

    public SimplifiedPDFActivity() {
    }

    private static int[] bookmarksToCreate;
    private Menu menu;
    private PdfDocument document;
    private BookmarkProvider bookmarkProvider;
    private static OnBookmarksChangedListener onBookmarksChangedListener;
    private static OnPageChangedListener onPageChangedListener;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bookmark_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public static Intent BuildIntent(Uri assetFile, int openToPage, int[] bookmarks, String pspdfkitLicenseKey, Context context, OnBookmarksChangedListener bookmarkListener, OnPageChangedListener pageChangedListener) {
        // Set license key
        try {
            PSPDFKit.initialize(context, pspdfkitLicenseKey);
        } catch (PSPDFKitInitializationFailedException e) {
            Log.e(LOG_TAG, "Current device is not compatible with PSPDFKit!");
        }

        // Set listeners
        onBookmarksChangedListener = bookmarkListener;
        onPageChangedListener = pageChangedListener;

        bookmarksToCreate = bookmarks;

        // Set configuration
        PdfActivityConfiguration config = new PdfActivityConfiguration
                .Builder(context)
                .disableDocumentEditor()
                .disableAnnotationEditing()
                .disableAnnotationList()
                .disableShare()
                .disablePrinting()
                .disableFormEditing()
                .page(openToPage - 1)
                .build();


        return PdfActivityIntentBuilder.fromUri(context, assetFile)
                .configuration(config)
                .activityClass(SimplifiedPDFActivity.class)
                .build();
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
                    }
                }
            }
        }
    }

    @Override
    public void onPageChanged(@NonNull PdfDocument document, int pageIndex) {
        super.onPageChanged(document, pageIndex);
        setBookmarkIcon(pageIndex);
        // pageIndex here is 0 based and used works for setting bookmark, but not last page read
        onPageChangedListener.onEvent(pageIndex + 1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean handled = false;

        if (item.getItemId() == R.id.bookmark_item) {
            handled = true;
            toggleBookmark(getPageIndex());
            onBookmarksChangedListener.onEvent(bookmarksToIntArray(bookmarkProvider.getBookmarks()));
        }

        return handled || super.onOptionsItemSelected(item);
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
        MenuItem menuItem = menu.findItem(R.id.bookmark_item);
        if (containsBookmarkForPage(currentBookmarks, pageIndex)) {
            menuItem.setIcon(R.drawable.ic_bookmark);
        } else {
            menuItem.setIcon(R.drawable.ic_bookmark_outline);
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

    private int[] bookmarksToIntArray(List<Bookmark> bookmarks){
        // https://stackoverflow.com/a/965289/2107568
        int[] bookmarkArray = new int[bookmarks.size()];
        int i = 0;
        for (Bookmark bookmark : bookmarks){
            bookmarkArray[i++] = bookmark.getPageIndex();
        }

        return bookmarkArray;
    }
}
