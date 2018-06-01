package org.nypl.pdfrendererprovider

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Both the host app and simplifiedpspdfkit will import this module,
 * to get access to interfaces and associated classes.
 */

interface PDFRendererProviderInterface {
    var currentPage: Int?
    var currentBookmarks: Set<PDFBookmark>?
    var notes: List<PDFAnnotation>?

    fun buildPDFRendererIntent(assetFile: Uri,
                               lastRead: Int,
                               context: Context): Intent
}

interface PDFRendererListener {
    fun onBookmarkChanged(newBookmarks: Set<PDFBookmark>)
    fun onPageChanged(pageIndex: Int)
}

class PDFConstants {
    companion object {
        val initialStateKey = "org.nypl.pdfRenderer.initialStateKey"
        val listenerKey = "org.nypl.pdfRenderer.listenerKey"
        val PDF_ID_EXTRA = "org.nypl.pdfRendere.uri_extra"
        val PDF_URI_EXTRA = "org.nypl.pdfRenderer.uri_extra"
        val PDF_PAGE_READ_EXTRA = "org.nypl.pdfRenderer.page_read_extra"
        val PDF_BOOKMARKS_EXTRA = "org.nypl.pdfRenderer.bookmarks_extra"
        val PDF_ANNOTATIONS_EXTRA = "org.nyple.pdfRenderer.annotations_extra"
    }
}

@Parcelize
data class PDFBookmark(val pageNumber: Int): Parcelable

@Parcelize
data class PDFAnnotation(val pageNumber: Int, val boundingRect: List<Int>, val text: String) : Parcelable
