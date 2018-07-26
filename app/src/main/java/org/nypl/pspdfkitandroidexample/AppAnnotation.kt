package org.nypl.pspdfkitandroidexample

import android.graphics.RectF
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppAnnotation(val pageNumber: Int,
                    val annotationType: String,
                    val boundingRect: String,
                    val rects: ArrayList<String>,
                    val color: String?,
                    val opacity: String?) : Parcelable{
}