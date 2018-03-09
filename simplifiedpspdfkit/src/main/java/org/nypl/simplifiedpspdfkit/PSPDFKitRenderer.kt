package org.nypl.simplifiedpspdfkit

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by nieho003 on 3/8/2018.
 */
class PSPDFKitRenderer : RendererProviderInterface {
    override var currentPage: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun saveBookmarks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buildIntent(assetFile: Uri, lastRead: Int, bookmarks: IntArray, pspdfKitLicenseKey: String, context: Context, pageChangedListener: OnPageChangedListener, bookmarksChangedListener: OnBookmarksChangedListener): Intent {
        return SimplifiedPDFActivity.BuildIntent(assetFile, lastRead, bookmarks, pspdfKitLicenseKey, context, pageChangedListener, bookmarksChangedListener)
    }

    init {
    }
}