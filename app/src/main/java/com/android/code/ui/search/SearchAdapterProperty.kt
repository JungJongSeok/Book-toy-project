package com.android.code.ui.search

import com.android.code.models.Book
import com.bumptech.glide.RequestManager

interface SearchAdapterProperty {
    val requestManager: RequestManager
    fun clickBook(book: Book)
}