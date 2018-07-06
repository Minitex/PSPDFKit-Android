package org.nypl.pspdfkitandroidexample

import android.graphics.RectF

class AppAnnotation(val pageNumber: Int,
                    val annotationType: String,
                    val boundingRect: String,
                    val rects: ArrayList<String>,
                    val color: String?,
                    val opacity: String?) {
}