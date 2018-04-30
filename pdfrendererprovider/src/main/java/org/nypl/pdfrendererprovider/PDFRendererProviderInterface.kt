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
    var currentPage: Int
    var currentBookmarks: Set<PDFBookmark>
    var notes: List<PDFAnnotation>

    //simplifiedpspdfkit will need to override these functions
    fun buildIntent(assetFile: Uri, lastRead: Int, bookmarks: Set<PDFBookmark>, context: Context, listener: PDFRendererListener) : Intent

}

//Host will need to implement this listener
interface PDFRendererListener {
    fun onBookmarkChanged(newBookmarks: Set<PDFBookmark>)
    fun onPageChanged(pageIndex: Int)
}

//class PDFPage(val pageNumber: Int) {
//
//    init {
//        //("The page set for this bookmark is ${pageNumber}")
//    }
//}

class PDFBookmark(val pageNumber: Int){
    init{

    }
}

class PDFAnnotation(val page: Int, val boundingRect: List<Int>, val text: String) {

    init {
        //print("I'm just making up parameters right now...")
    }
}
