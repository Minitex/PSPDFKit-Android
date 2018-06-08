package org.nypl.pspdfkitandroidexample

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.book_list_item_row.view.*
import org.nypl.pdfrendererprovider.PDFAnnotation
import org.nypl.pdfrendererprovider.PDFBookmark
import org.nypl.pdfrendererprovider.PDFConstants
import org.nypl.pdfrendererprovider.PDFRendererProviderInterface
import kotlin.reflect.full.createInstance

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

    class BookHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        var rendererProvider: PDFRendererProvider = PDFRendererProvider()

        private var view: View = v
        private var book: Book? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(p0: View) {
            val context = itemView.context
            if (book != null) {
                Toast.makeText(context, itemView.bookTitle.text, Toast.LENGTH_SHORT).show()
                startPdfActivity(context, book!!.bookId, book!!.resourceUri, book!!.lastPageRead, book!!.bookmarks, book!!.annotations)
            } else {
                Toast.makeText(context, "No book set", Toast.LENGTH_SHORT).show()
            }
        }

        fun bindBook(book: Book) {
            this.book = book
            updateView()
        }

        private fun updateView() {
            view.bookTitle.text = book?.title
            view.lastPageRead.text = "Last Page Read: " + book?.lastPageRead.toString()
            view.bookmarkCount.text = "Bookmarks Saved: " + book?.bookmarks?.size.toString()
        }

        private fun startPdfActivity(context: Context, bookId: Int, assetFile: Uri, lastRead: Int, bookmarks: Set<AppBookmark>, annotations: Set<AppAnnotation>) {

            val classString = "org.nypl.simplifiedpspdfkit.PSPDFKitProvider"
            val kclass = Class.forName(classString).kotlin
            val renderer = kclass.createInstance() as PDFRendererProviderInterface

            val intent = renderer.buildPDFRendererIntent(
                    assetFile = assetFile,
                    bookId = bookId,
                    lastRead = lastRead,
                    bookmarks = convertToRendererBookmarks(bookmarks),
                    annotations = convertToRendererAnnotations(annotations),
                    context = context
            )



            (context as MainActivity).startActivity(intent)
        }

        private fun convertToRendererBookmarks(bookmarks: Set<AppBookmark>?): ArrayList<PDFBookmark>? {
            if (bookmarks == null || bookmarks.isEmpty()){
                return null
            }

            val convertedBookmarks = arrayListOf<PDFBookmark>()
            for (appBookmark in bookmarks) {
                convertedBookmarks.add(PDFBookmark(appBookmark.pageNumber))
            }

            return convertedBookmarks
        }

        private fun convertToRendererAnnotations(annotations: Set<AppAnnotation>?): ArrayList<PDFAnnotation>? {
            if (annotations == null || annotations.isEmpty()){
                return null
            }

            val convertedAnnotations = arrayListOf<PDFAnnotation>()
            for (appAnnotation in annotations) {
                convertedAnnotations.add(PDFAnnotation(appAnnotation.pageNumber))
            }

            return convertedAnnotations
        }
    }
}