# PSPDFKit-Android
This repository is made up of 3 modules:

#### pdfrendererprovider
  - Contains an interface an application can use to get a PDFRenderer

## pdfrendererprovider

This module defines the `PDFRendererProviderInterface`. 

This is what the `simplifiedpspdfkit` implements(and any other PDF renderer librarys would need to implement) so that a client app can call into it.

The module also defines a `PDFBroadcaster` companion object to define communications that will be sent from the wrapped `simplifiedpspdfkit` library back to the application.
Currently those events are `PAGE_CHANGED_BROADCAST_EVENT_NAME`,  `BOOKMARKS_CHANGED_BROADCAST_EVENT_NAME`, and `ANNOTATIONS_CHANGED_BROADCAST_EVENT_NAME`
 
## simplifiedpsdpdfkit

This is the wrapper for the PSPDFKit SDK.

To use this module, you must provide a `PSPDFKitLicenseKey` and change the `ApiKeysTemplate` to `ApiKeys` and provide the value of your license key.

The `PSPDFKitProvider` implements the `PDFRendererProviderInterface` from the above module. This exposes the `buildPDFRendererIntent`, which returns an Intent tied to the `SimplifiedPDFActivity`.

The `SimplifiedPDFActivity` is our wrapper of PSPDFKit's `PdfActivity` and it implements that SDK's `DocumentListener` interface as well.

## Sample App

A simple sample applications that has a couple PDFs in-memory that it can pass to the `simplifiedpspdfkit` via the `pdfrendererprovider`.

The call into the `simplifiedpspdfkit` module via `pdfrendererprovider` is seen in the `BookListAdapter` class in the `startPdfActivity`. 
This defines a `classString` referencing the `PDFRendererProviderInterface` the application in to use, in this case the `PSPDFKitProvider`, then creates an instance of that class.
It then builds an intent calling that class' `buildPDFRendererIntent` method and then starting that activity with the created intent.

Bookmarks, annotations, and last page read are synced with the in-memory


