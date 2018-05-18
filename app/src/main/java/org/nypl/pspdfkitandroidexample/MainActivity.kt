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
import org.nypl.pdfrendererprovider.PDFConstants
import org.nypl.pdfrendererprovider.broadcaster.PDFBroadcaster
import org.nypl.pspdfkitandroidexample.R.id.rv_book_list

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

        // Broadcast listener
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(messageReceiver, IntentFilter(PDFBroadcaster.BROADCAST_EVENT_NAME))
    }

    // https://stackoverflow.com/a/45399437
    val messageReceiver = object : BroadcastReceiver() {
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
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
        super.onDestroy()
    }

    private fun populateBooks() {
        var book1Bookmarks : MutableSet<AppBookmark> = hashSetOf(AppBookmark(1), AppBookmark(3), AppBookmark(35))
        var book1 = Book(1, "Financial Accounting", book1Bookmarks, 19, Uri.parse("file:///android_asset/FinancialAccounting.pdf"))
        booksList.add(book1)
        var book2 = Book(2, "Alice in Wonderland", kotlin.collections.emptySet(), 1, Uri.parse("file:///android_asset/aliceInWonderland.pdf"))
        booksList.add(book2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                var bookId = data.getIntExtra(PDFConstants.PDF_ID_EXTRA, -1)
                var bookUri = data.getStringExtra(PDFConstants.PDF_URI_EXTRA)
                var lastPage = data.getIntExtra(PDFConstants.PDF_PAGE_READ_EXTRA, -1)

                if (bookId >= 0) {
                    for (book in booksList) {
                        if (book.bookId == bookId) {
                            book.lastPageRead = lastPage
                            break
                        }
                    }
                }
            }
        }

        adapter.notifyDataSetChanged()

        print("We have received data from the child activity!!!")
    }
}
