package org.nypl.pspdfkitandroidexample

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.nypl.pdfrendererprovider.PDFAnnotation
import org.nypl.pdfrendererprovider.PDFBookmark
import org.nypl.pdfrendererprovider.PDFConstants
import org.nypl.pdfrendererprovider.broadcaster.PDFBroadcaster

class MainActivity : AppCompatActivity() {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: BookListAdapter

    private var booksList: ArrayList<Book> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutManager = LinearLayoutManager(this)
        rv_book_list.layoutManager = layoutManager

        populateBooks()

        adapter = BookListAdapter(booksList)
        rv_book_list.adapter = adapter

        with(LocalBroadcastManager.getInstance(this)) {
            registerReceiver(pageChangedMessageReceiver, IntentFilter(PDFBroadcaster.PAGE_CHANGED_BROADCAST_EVENT_NAME))
            registerReceiver(bookmarksChangedMessageReceiver, IntentFilter(PDFBroadcaster.BOOKMARKS_CHANGED_BROADCAST_EVENT_NAME))
            registerReceiver(annotationsChangedMessageReceiver, IntentFilter(PDFBroadcaster.ANNOTATIONS_CHANGED_BROADCAST_EVENT_NAME))
        }
    }

    // https://stackoverflow.com/a/45399437
    private val pageChangedMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                var idExtra = intent.getIntExtra(PDFConstants.PDF_ID_EXTRA, -1)
                var pageExtra = intent.getIntExtra(PDFConstants.PDF_PAGE_READ_EXTRA, -1)

                if (idExtra >= 0) {
                    for (book in booksList) {
                        if (book.bookId == idExtra) {
                            // Passes back the page index, which is zero-based
                            book.lastPageRead = pageExtra + 1
                            break
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }
        }
    }

    private val bookmarksChangedMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                var idExtra = intent.getIntExtra(PDFConstants.PDF_ID_EXTRA, -1)
                var bookmarksExtra = intent.getParcelableArrayListExtra<PDFBookmark>(PDFConstants.PDF_BOOKMARKS_EXTRA)

                if (idExtra >= 0) {
                    for (book in booksList) {
                        if (book.bookId == idExtra) {
                            book.bookmarks = convertBookmarksToAppBookmark(bookmarksExtra)
                            break
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }
        }
    }

    private val annotationsChangedMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                var idExtra = intent.getIntExtra(PDFConstants.PDF_ID_EXTRA, -1)
                var annotationsExtra = intent.getParcelableArrayListExtra<PDFAnnotation>(PDFConstants.PDF_ANNOTATIONS_EXTRA)

                if (idExtra >= 0) {
                    for (book in booksList) {
                        if (book.bookId == idExtra) {
                            book.annotations = convertAnnotationsToAppAnnotations(annotationsExtra)
                            break
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun convertBookmarksToAppBookmark(bookmarksExtra: java.util.ArrayList<PDFBookmark>?): Set<AppBookmark> {
        var appBookmarks: MutableSet<AppBookmark> = hashSetOf()
        if (bookmarksExtra != null) {
            for (bookmark in bookmarksExtra.iterator()) {
                appBookmarks.add(AppBookmark(bookmark.pageNumber))
            }
        }

        return appBookmarks
    }

    private fun convertAnnotationsToAppAnnotations(annotationsExtra: java.util.ArrayList<PDFAnnotation>?): Set<AppAnnotation> {
        var appAnnotations: MutableSet<AppAnnotation> = hashSetOf()
        if (annotationsExtra != null) {
            for (annotation in annotationsExtra.iterator()) {
                appAnnotations.add(
                        AppAnnotation(
                                annotation.pageNumber,
                                annotation.annotationType,
                                annotation.boundingRect,
                                annotation.rects,
                                annotation.color,
                                annotation.opacity))
            }
        }

        return appAnnotations
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pageChangedMessageReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bookmarksChangedMessageReceiver)
        super.onDestroy()
    }

    private fun populateBooks() {
        var book1Bookmarks: MutableSet<AppBookmark> = hashSetOf(AppBookmark(1), AppBookmark(3), AppBookmark(35))
        var book1 = Book(1, "Financial Accounting", book1Bookmarks, null, 19, Uri.parse("file:///android_asset/FinancialAccounting.pdf"))
        booksList.add(book1)

        var book2 = Book(
                bookId = 2,
                title = "Alice in Wonderland",
                bookmarks = null,
                annotations = null,
                lastPageRead = 1,
                resourceUri = Uri.parse("file:///android_asset/aliceInWonderland.pdf"))
        booksList.add(book2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                var bookId = data.getIntExtra(PDFConstants.PDF_ID_EXTRA, -1)
                var lastPage = data.getIntExtra(PDFConstants.PDF_PAGE_READ_EXTRA, -1)

                if (bookId >= 0) {
                    for (book in booksList) {
                        if (book.bookId == bookId) {
                            // Passes back the page index, which is zero-based
                            book.lastPageRead = lastPage + 1
                            break
                        }
                    }
                }
            }
        }

        adapter.notifyDataSetChanged()
    }
}
