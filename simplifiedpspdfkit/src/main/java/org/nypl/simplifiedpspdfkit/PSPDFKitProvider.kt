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
import com.pspdfkit.annotations.AnnotationType
import org.nypl.pdfrendererprovider.PDFConstants


class PSPDFKitProvider() : PDFRendererProviderInterface {

    override var currentPage: Int? = null
    override var currentBookmarks: Set<PDFBookmark>? = null
    override var notes: List<PDFAnnotation>? = null

    override fun buildPDFRendererIntent(assetFile: Uri,
                                        bookId: Int,
                                        lastRead: Int,
                                        bookmarks: ArrayList<PDFBookmark>?,
                                        annotations: ArrayList<PDFAnnotation>?,
                                        context: Context): Intent {

        // Set license key
        try {
            PSPDFKit.initialize(context, ApiKeys.PSPDFKitLicenseKey)
        } catch (e: PSPDFKitInitializationFailedException) {
            print("Failed to init with PSPDFKit license key.")
            System.exit(1)
        }

        val approvedAnnotations = listOf(AnnotationType.HIGHLIGHT, AnnotationType.UNDERLINE)

        //This class would take 'lastRead' and 'bookmarks' and convert those
        //into working objects to add to "PSPDFKit's" config object here..
        //in order to get the state of the book back to where it was

        // Set configuration
        val config = PdfActivityConfiguration.Builder(context)
                .disableDocumentEditor()
                .editableAnnotationTypes(approvedAnnotations)
                .disableShare()
                .disablePrinting()
                .disableFormEditing()
                .page(lastRead - 1)
                .build()

        val intent =  PdfActivityIntentBuilder.fromUri(context, assetFile)
                .configuration(config)
                .activityClass(SimplifiedPDFActivity::class.java)
                .build()

        intent.putExtra(PDFConstants.PDF_ID_EXTRA, bookId)
        if (bookmarks != null) {
            intent.putExtra(PDFConstants.PDF_BOOKMARKS_EXTRA, bookmarks)
        }

        if (annotations != null) {
            intent.putExtra(PDFConstants.PDF_ANNOTATIONS_EXTRA, annotations)
        }

        return intent
    }
}
