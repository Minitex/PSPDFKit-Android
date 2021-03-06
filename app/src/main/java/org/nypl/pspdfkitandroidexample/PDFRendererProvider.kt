package org.nypl.pspdfkitandroidexample

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.nypl.pdfrendererprovider.PDFBookmark
import org.nypl.simplifiedpspdfkit.OnBookmarksChangedListener
import org.nypl.simplifiedpspdfkit.OnPageChangedListener
import org.nypl.pdfrendererprovider.PDFRendererProviderInterface
import org.nypl.simplifiedpspdfkit.SimplifiedPDFActivity

class PDFRendererProvider {
    lateinit var renderer : PDFRendererProviderInterface

//    fun buildPDFRendererIntent(assetFile: Uri, lastRead: Int, bookmarks: Set<PDFBookmark>, pspdfKitLicenseKey: String, context: Context, pageChangedListener: OnPageChangedListener, bookmarksChangedListener: OnBookmarksChangedListener): Intent? {
//        renderer = SimplifiedPDFActivity()
//        var bookmarksArray = pdfBookmarkSetToIntArray(bookmarks)
//        return renderer.buildPDFRendererIntent(assetFile, lastRead, bookmarksArray, pspdfKitLicenseKey, context, pageChangedListener, bookmarksChangedListener)
//    }

    fun pdfBookmarkSetToIntArray(bookmarks: Set<PDFBookmark>): IntArray {
        var bookmarkList = mutableListOf<Int>()

        for (pdfBookmark in bookmarks){
           bookmarkList.add(pdfBookmark.pageNumber)
        }

        return bookmarkList.toIntArray()
    }

    fun intArrayToPdfBookmarkSet(intArray: IntArray) : Set<PDFBookmark> {
        var bookmarkList = mutableListOf<PDFBookmark>()

        for (int in intArray){
            bookmarkList.add(PDFBookmark(int))
        }

        return bookmarkList.toSet()
    }
}
