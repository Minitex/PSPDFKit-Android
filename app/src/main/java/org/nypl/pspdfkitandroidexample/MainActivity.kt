package org.nypl.pspdfkitandroidexample

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

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
    }

    override fun onStart() {
        super.onStart()
    }

    private fun populateBooks() {
        var book1 = Book("Financial Accounting", kotlin.collections.emptySet(), 19, Uri.parse("file:///android_asset/FinancialAccounting.pdf"))
        booksList.add(book1)
        var book2 = Book("Alice in Wonderland", kotlin.collections.emptySet(), 1, Uri.parse("file:///android_asset/aliceInWonderland.pdf"))
        booksList.add(book2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        print("We have received data from the child activity!!!")
    }
}
