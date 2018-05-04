package org.nypl.simplifiedpspdfkit

import android.os.Parcel
import android.os.Parcelable
import org.nypl.pdfrendererprovider.PDFBookmark
import org.nypl.pdfrendererprovider.PDFRendererListener

class HostListener() : PDFRendererListener, Parcelable {

    /*
    Parcelable Methods
     */

    constructor(parcel: Parcel) : this() {
        print("Serialize and set data from Parcel. Currently no data to read.")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        print("Write data to the parcel. Currently no data to write.")
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HostListener> {
        override fun createFromParcel(parcel: Parcel): HostListener {
            return HostListener(parcel)
        }

        override fun newArray(size: Int): Array<HostListener?> {
            return arrayOfNulls(size)
        }
    }

    /*
    PDFRendererListener Methods
     */

    override fun onPageChanged(pageIndex: Int) {
        print("Page Index changed! The host is listening: ${pageIndex}")
    }

    override fun onBookmarkChanged(newBookmarks: Set<PDFBookmark>) {
        print("New bookmarks received! The host is listening: ${newBookmarks}")
    }
}
