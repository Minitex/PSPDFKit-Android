package org.nypl.pspdfkitandroidexample

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.nypl.simplifiedpspdfkit.OnBookmarksChangedListener
import org.nypl.simplifiedpspdfkit.OnPageChangedListener
import org.nypl.simplifiedpspdfkit.PSPDFKitRenderer
import org.nypl.simplifiedpspdfkit.RendererProviderInterface

class PDFRendererProvider {
    lateinit var renderer : RendererProviderInterface

    fun BuildIntent(assetFile: Uri, lastRead: Int, bookmarks: IntArray, pspdfKitLicenseKey: String, context: Context, pageChangedListener: OnPageChangedListener, bookmarksChangedListener: OnBookmarksChangedListener): Intent? {
        renderer = PSPDFKitRenderer()
        return renderer.buildIntent(assetFile, lastRead, bookmarks, pspdfKitLicenseKey, context, pageChangedListener, bookmarksChangedListener)
    }

}
