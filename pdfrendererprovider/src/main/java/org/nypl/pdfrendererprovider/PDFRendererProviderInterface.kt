package org.nypl.pdfrendererprovider

import android.content.Context
import android.content.Intent
import android.net.Uri

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
                               context: Context): Intent
}

interface PDFRendererListener {
    fun onBookmarkChanged(newBookmarks: Set<PDFBookmark>)
    fun onPageChanged(pageIndex: Int)
}

class PDFConstants {
    companion object {
        val intentKey = "org.nypl.pdfRenderer.intentKey"
    }
}

data class PDFBookmark(val pageNumber: Int)

data class PDFAnnotation(val page: Int, val boundingRect: List<Int>, val text: String)
