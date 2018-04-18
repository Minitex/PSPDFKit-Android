package org.nypl.pdfrendererprovider

import org.nypl.simplifiedpspdfkit.OnBookmarksChangedListener
import org.nypl.simplifiedpspdfkit.OnPageChangedListener
import org.nypl.simplifiedpspdfkit.SimplifiedPDFActivity

class PDFRenderer () {
    val renderer: PDFRendererProviderInterface = SimplifiedPDFActivity()

    init {

    }
}