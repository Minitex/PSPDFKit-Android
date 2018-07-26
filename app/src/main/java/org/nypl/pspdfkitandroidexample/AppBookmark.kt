package org.nypl.pspdfkitandroidexample

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by nieho003 on 3/23/2018.
 */
@Parcelize
data class AppBookmark(val pageNumber: Int) : Parcelable {
}