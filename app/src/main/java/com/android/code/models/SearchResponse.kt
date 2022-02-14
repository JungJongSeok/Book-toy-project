package com.android.code.models

data class SearchResponse(
    val books: List<Book>?,
    val error: String,
    val page: String,
    val total: String
)

data class Book(
    val image: String,
    val isbn13: String,
    val price: String,
    val subtitle: String,
    val title: String,
    val url: String
)