package org.nypl.simplifiedpspdfkit

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by nieho003 on 3/8/2018.
 */


// We'll just pretend for now that this file is our "PDFRendererProvider" module


interface PDFRendererProvider {

    //The two listeners that would normally get passed in the intent builder will now just update these properties
    var currentPage: Int
    var currentBookmarks: List<PDFRendererBookmark>

    //I can imagine the most agnostic way an activity would be launched would look something like this.
    //The listeners can be separate interface functions and don't need to be in this intent constructor.
    fun buildPDFRendererIntent(assetFile: Uri,
                               openToPage: PDFRendererPage,
                               bookmarks: List<PDFRendererBookmark>,
                               annotations: List<PDFRendererAnnotation>,
                               context: Context): Intent
}


interface PDFRendererPage {
    var currentPageNumber: Int
}

interface PDFRendererBookmark {
    fun location(): PDFRendererPage
    fun textSnippet(): String?
    //IF we assume a bookmark is a subclass of annotation, this would be one way to handle that,
    //but this might be a dangerous assumption (would have to think about this)
    var annotation: PDFRendererAnnotation
}

interface PDFRendererAnnotation {
    //I'm making things that I think might be PSPDFKit-specific: optional return types
    fun pageLocation(): PDFRendererPage
    fun boundingBox(): List<Int>?
    fun jsonBody(): String?
}

//this is the glue that connects the two pieces together
class PDFRenderer {

    val renderer: PDFRendererProvider = SimplifiedPDFActivity()

}
