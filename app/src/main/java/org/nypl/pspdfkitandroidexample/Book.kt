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
    var resourceUri: Uri
    var lastPageRead: Int = 0

    constructor(bookId: Int, title: String, bookmarks: Set<AppBookmark>, lastPageRead: Int, resourceUri: Uri) {
        this.bookId = bookId
        this.title = title
        this.bookmarks = bookmarks
        this.lastPageRead = lastPageRead
        this.resourceUri = resourceUri
    }
}