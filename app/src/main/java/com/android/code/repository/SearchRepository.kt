package com.android.code.repository

import com.android.code.lib.network.BookService
import com.android.code.models.DetailResponse
import com.android.code.models.SearchResponse


interface SearchRepository {

    suspend fun search(
        query: String,
        page: Int
    ): SearchResponse

    suspend fun detail(
        isbn13: String
    ): DetailResponse

}

class SearchRepositoryImpl(
    private val bookService: BookService
) : SearchRepository {

    override suspend fun search(
        query: String,
        page: Int
    ): SearchResponse {
        return bookService.search(query, page)
    }

    override suspend fun detail(
        isbn13: String
    ): DetailResponse {
        return bookService.detail(isbn13)
    }

}