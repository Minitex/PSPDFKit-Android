package org.nypl.pspdfkitandroidexample

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.book_list_item_row.view.*

/**
 * Created by nieho003 on 2/23/2018.
 */
class BookListAdapter (private val books : ArrayList<Book>) : RecyclerView.Adapter<BookListAdapter.BookHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListAdapter.BookHolder {
        val inflatedView = parent.inflate(R.layout.book_list_item_row, false)
        return BookHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BookListAdapter.BookHolder, position: Int) {
        val book = books[position]
        holder.bindBook(book)
    }

    override fun getItemCount() = books.size

    class BookHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var book: Book? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(p0: View) {
            val context = itemView.context
            Toast.makeText(context, itemView.bookTitle.text, Toast.LENGTH_SHORT).show();
        }

        fun bindBook(book: Book) {
            view.bookTitle.text = book.title
            view.lastPageRead.text = "Last Page Read: " + book.lastPageRead.toString()
            view.bookmarkCount.text = "Bookmarks Saved: " + book.bookmarks.size.toString()
        }
    }
}