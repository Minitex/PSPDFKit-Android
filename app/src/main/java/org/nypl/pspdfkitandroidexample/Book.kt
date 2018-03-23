package org.nypl.pspdfkitandroidexample

import android.net.Uri
import com.pspdfkit.bookmarks.Bookmark

/**
 * Created by nieho003 on 2/23/2018.
 */
class Book {
    var title: String
    var bookmarks: Set<PDFBookmark>
    var resourceUri: Uri
    var lastPageRead: Int = 0

    constructor(title: String, bookmarks: Set<PDFBookmark>, lastPageRead: Int, resourceUri: Uri){
        this.title = title
        this.bookmarks = bookmarks
        this.lastPageRead = lastPageRead
        this.resourceUri = resourceUri
    }
}