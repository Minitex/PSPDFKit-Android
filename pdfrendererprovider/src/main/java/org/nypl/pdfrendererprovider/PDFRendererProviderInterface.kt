package org.nypl.pdfrendererprovider

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * Both the host app and simplifiedpspdfkit will import this module,
 * to get access to interfaces and associated classes.
 */

interface PDFRendererProviderInterface {
    var currentPage: Int
    var currentBookmarks: Set<PDFBookmark>
    var notes: List<PDFAnnotation>

    fun buildPDFRendererIntent(assetFile: Uri,
                               lastRead: Int,
                               bookmarks: Set<PDFBookmark>,
                               context: Context,
                               listener: Class<PDFRendererListener>,
                               initialState: Parcelable): Intent
}

interface PDFRendererListener {
    fun setInitialState(state: Parcelable)
    fun onBookmarkChanged(newBookmarks: Set<PDFBookmark>)
    fun onPageChanged(pageIndex: Int)
}

class PDFConstants {
    companion object {
        val initialStateKey = "org.nypl.pdfRenderer.initialStateKey"
        val listenerKey = "org.nypl.pdfRenderer.listenerKey"
    }
}

data class PDFBookmark(val pageNumber: Int)

data class PDFAnnotation(val page: Int, val boundingRect: List<Int>, val text: String)
