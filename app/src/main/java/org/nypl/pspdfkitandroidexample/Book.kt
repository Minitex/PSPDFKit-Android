package org.nypl.pspdfkitandroidexample

import android.net.Uri
import com.pspdfkit.bookmarks.Bookmark

/**
 * Created by nieho003 on 2/23/2018.
 */
class Book {
    var bookId: Int
    var title: String
    var bookmarks: Set<AppBookmark>
    var annotations: Set<AppAnnotation>
    var resourceUri: Uri
    var lastPageRead: Int = 0

    constructor(bookId: Int, title: String, bookmarks: Set<AppBookmark>?, annotations: Set<AppAnnotation>?, lastPageRead: Int, resourceUri: Uri) {
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