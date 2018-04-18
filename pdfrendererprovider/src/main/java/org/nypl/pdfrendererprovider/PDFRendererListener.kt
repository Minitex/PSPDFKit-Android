package org.nypl.pdfrendererprovider

interface PDFRendererListener {
    fun onBookmarkEvent(newBookmarks: IntArray)
    fun onPageChangedEvent(pageIndex: Int)
}