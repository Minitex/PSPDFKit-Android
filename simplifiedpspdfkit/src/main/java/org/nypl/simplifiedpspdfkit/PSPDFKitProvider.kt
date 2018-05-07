package org.nypl.simplifiedpspdfkit

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.nypl.pdfrendererprovider.PDFAnnotation
import org.nypl.pdfrendererprovider.PDFBookmark
import org.nypl.pdfrendererprovider.PDFRendererProviderInterface
import com.pspdfkit.ui.PdfActivityIntentBuilder
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.exceptions.PSPDFKitInitializationFailedException
import com.pspdfkit.PSPDFKit



class PSPDFKitProvider() : PDFRendererProviderInterface {

    override var currentPage: Int? = null
    override var currentBookmarks: Set<PDFBookmark>? = null
    override var notes: List<PDFAnnotation>? = null

    override fun buildPDFRendererIntent(assetFile: Uri,
                                        lastRead: Int,
                                        bookmarks: Set<PDFBookmark>,
                                        context: Context): Intent {

        // Set license key
        try {
            PSPDFKit.initialize(context, ApiKeys.PSPDFKitLicenseKey)
        } catch (e: PSPDFKitInitializationFailedException) {
            print("Failed to init with PSPDFKit license key.")
            System.exit(1)
        }

        //This class would take 'lastRead' and 'bookmarks' and convert those
        //into working objects to add to "PSPDFKit's" config object here..
        //in order to get the state of the book back to where it was

        // Set configuration
        val config = PdfActivityConfiguration.Builder(context)
                .disableDocumentEditor()
                .disableAnnotationEditing()
                .disableAnnotationList()
                .disableShare()
                .disablePrinting()
                .disableFormEditing()
                .page(lastRead - 1)
                .build()

        return PdfActivityIntentBuilder.fromUri(context, assetFile)
                .configuration(config)
                .activityClass(SimplifiedPDFActivity::class.java)
                .build()
    }
}
