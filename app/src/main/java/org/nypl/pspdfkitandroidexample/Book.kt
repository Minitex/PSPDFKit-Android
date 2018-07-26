package org.nypl.pspdfkitandroidexample

import android.net.Uri
import android.os.Parcelable
import com.pspdfkit.bookmarks.Bookmark
import kotlinx.android.parcel.Parcelize


/**
 * Created by nieho003 on 2/23/2018.
 */
@Parcelize
class Book() : Parcelable {
    var bookId: Int = 0
    lateinit var title: String
    var bookmarks: Set<AppBookmark> = kotlin.collections.emptySet()
    var annotations: Set<AppAnnotation> = kotlin.collections.emptySet()
    lateinit var resourceUri: Uri
    var lastPageRead: Int = 0

    constructor(bookId: Int, title: String, bookmarks: Set<AppBookmark>?, annotations: Set<AppAnnotation>?, lastPageRead: Int, resourceUri: Uri) : this() {
        this.bookId = bookId
        this.title = title
        this.lastPageRead = lastPageRead
        this.resourceUri = resourceUri

        if (bookmarks == null) {
            this.bookmarks = kotlin.collections.emptySet()
        } else {
            this.bookmarks = bookmarks
        }

        if (annotations == null) {
            this.annotations = kotlin.collections.emptySet()
        } else {
            this.annotations = annotations
        }
    }
}