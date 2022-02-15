package com.android.code.models

import android.os.Parcelable
import com.android.code.util.zero
import kotlinx.parcelize.Parcelize

data class SearchResponse(
    val books: List<Book>?,
    val error: String,
    val page: String,
    val total: String,
) {
    fun parseIntPage() = page.toIntOrNull() ?: Int.zero()
    fun parseIntTotal() = total.toIntOrNull() ?: Int.zero()
}

@Parcelize
data class Book(
    val image: String,
    val isbn13: String,
    val price: String,
    val subtitle: String,
    val title: String,
    val url: String,
) : Parcelable