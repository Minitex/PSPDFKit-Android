package org.nypl.pspdfkitandroidexample

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.book_list_item_row.view.*
import org.nypl.pdfrendererprovider.PDFRenderer
import org.nypl.simplifiedpspdfkit.OnBookmarksChangedListener
import org.nypl.simplifiedpspdfkit.OnPageChangedListener

/**
 * Created by nieho003 on 2/23/2018.
 */
class BookListAdapter(private val books: ArrayList<Book>) : RecyclerView.Adapter<BookListAdapter.BookHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListAdapter.BookHolder {
        val inflatedView = parent.inflate(R.layout.book_list_item_row, false)
        return BookHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BookListAdapter.BookHolder, position: Int) {
        val book = books[position]
        holder.bindBook(book)
    }

    override fun getItemCount() = books.size

    class BookHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener, OnBookmarksChangedListener, OnPageChangedListener {

        var rendererProvider : PDFRendererProvider = PDFRendererProvider()


        override fun onPageChangedEvent(pageIndex: Int) {
            book?.lastPageRead = pageIndex
            updateView()
        }

        override fun onBookmarkEvent(newBookmarks: IntArray) {
            book?.bookmarks = rendererProvider.intArrayToPdfBookmarkSet(newBookmarks)
            updateView()
        }

        private var view: View = v
        private var book: Book? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(p0: View) {
            val context = itemView.context
            if (book != null) {
                Toast.makeText(context, itemView.bookTitle.text, Toast.LENGTH_SHORT).show()
                startPdfActivity(context, book!!.resourceUri, book!!.lastPageRead, book!!.bookmarks)
            } else {
                Toast.makeText(context, "No book set", Toast.LENGTH_SHORT).show()
            }
        }

        fun bindBook(book: Book) {
            this.book = book
            updateView()
        }

        private fun updateView(){
            view.bookTitle.text = book?.title
            view.lastPageRead.text = "Last Page Read: " + book?.lastPageRead.toString()
            view.bookmarkCount.text = "Bookmarks Saved: " + book?.bookmarks?.size.toString()
        }

        private fun startPdfActivity(context: Context, assetFile: Uri, lastRead: Int, bookmarks: Set<PDFBookmark>) {
            //val intent = rendererProvider.buildIntent(assetFile, lastRead, bookmarks, ApiKeys.PSPDFKitLicenseKey, context, this,  this)
            //val intent = SimplifiedPDFActivity.BuildIntent(assetFile, lastRead, bookmarks, ApiKeys.PSPDFKitLicenseKey, context, null, this)
            val intent = PDFRenderer().renderer.buildIntent(assetFile, lastRead, bookmarks, ApiKeys.PSPDFKitLicenseKey, context)
            context.startActivity(intent)
        }
    }
}