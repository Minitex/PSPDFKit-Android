package org.nypl.pdfrendererprovider

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.nypl.simplifiedpspdfkit.OnBookmarksChangedListener
import org.nypl.simplifiedpspdfkit.OnPageChangedListener

/**
 * Created by nieho003 on 3/8/2018.
 */
interface PDFRendererProviderInterface {
    var currentPage: Int
    var currentBookmarks: List<PDFRendererBookmark>
//    fun buildIntent(assetFile: Uri, lastRead: Int, bookmarks: IntArray, pspdfKitLicenseKey: String, context: Context, pageChangedListener: OnPageChangedListener, bookmarksChangedListener: OnBookmarksChangedListener) : Intent
    fun buildIntent(assetFile: Uri, lastRead: Int, bookmarks: Set<PDFRendererBookmark>, pspdfKitLicenseKey: String, context: Context) : Intent

}

interface PDFRendererPage {
    var currentPageNumber: Int
}

interface PDFRendererBookmark{
    fun location(): PDFRendererPage
}
