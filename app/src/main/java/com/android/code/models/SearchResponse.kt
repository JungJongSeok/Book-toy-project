package com.android.code.models

import com.android.code.util.zero

data class SearchResponse(
    val books: List<Book>?,
    val error: String,
    val page: String,
    val total: String
) {
    fun parseIntPage() = page.toIntOrNull() ?: Int.zero()
    fun parseIntTotal() = total.toIntOrNull() ?: Int.zero()
}

data class Book(
    val image: String,
    val isbn13: String,
    val price: String,
    val subtitle: String,
    val title: String,
    val url: String
)