package org.nypl.simplifiedpspdfkit

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by nieho003 on 3/8/2018.
 */
interface RendererProviderInterface {
    var currentPage: Int
    fun saveBookmarks()
    fun buildIntent(assetFile: Uri, lastRead: Int, bookmarks: IntArray, pspdfKitLicenseKey: String, context: Context, pageChangedListener: OnPageChangedListener, bookmarksChangedListener: OnBookmarksChangedListener) : Intent
}
