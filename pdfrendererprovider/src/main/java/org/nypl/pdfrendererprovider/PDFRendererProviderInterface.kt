package org.nypl.pdfrendererprovider

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Both the host app and simplifiedpspdfkit will import this module,
 * to get access to interfaces and associated classes.
 */

interface PDFRendererProviderInterface {

    //simplifiedpspdfkit will need to override these properties' get/set
    var currentPage: PDFPage
    var currentBookmarks: List<PDFPage>
    var notes: List<PDFAnnotation>

    //simplifiedpspdfkit will need to override these functions
    fun buildIntent(assetFile: Uri, lastRead: Int, bookmarks: Set<PDFPage>, context: Context, listener: PDFRendererListener) : Intent

}

//Host will need to implement this listener
interface PDFRendererListener {

    fun onBookmarkChanged()
    fun onPageChanged()
}

class PDFPage(val pageNumber: Int) {

    init {
        //("The page set for this bookmark is ${pageNumber}")
    }
}

class PDFAnnotation(val page: PDFPage, val boundingRect: List<Int>, val text: String) {

    init {
        //print("I'm just making up parameters right now...")
    }
}
