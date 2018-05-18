package org.nypl.pspdfkitandroidexample

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
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

        // Broadcast listeners
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(pageChangedMessageReceiver, IntentFilter(PDFBroadcaster.PAGE_CHANGED_BROADCAST_EVENT_NAME))

        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(bookmarksChangedMessageReceiver, IntentFilter(PDFBroadcaster.BOOKMARKS_CHANGED_BROADCAST_EVENT_NAME))
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
                            book.lastPageRead = pageExtra
                            break
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }
        }
    }

    private val bookmarksChangedMessageReceiver  = object : BroadcastReceiver() {
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

    private fun convertBookmarksToAppBookmark(bookmarksExtra: java.util.ArrayList<PDFBookmark>?): Set<AppBookmark> {
        var appBookmarks : MutableSet<AppBookmark> = hashSetOf()
        if (bookmarksExtra != null) {
            for (bookmark in bookmarksExtra.iterator())
            {
                appBookmarks.add(AppBookmark(bookmark.pageNumber))
            }
        }

        return appBookmarks
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
        var book1Bookmarks : MutableSet<AppBookmark> = hashSetOf(AppBookmark(1), AppBookmark(3), AppBookmark(35))
        var book1 = Book(1, "Financial Accounting", book1Bookmarks, 19, Uri.parse("file:///android_asset/FinancialAccounting.pdf"))
        booksList.add(book1)
        var book2 = Book(2, "Alice in Wonderland", kotlin.collections.emptySet(), 1, Uri.parse("file:///android_asset/aliceInWonderland.pdf"))
        booksList.add(book2)
    }
}
