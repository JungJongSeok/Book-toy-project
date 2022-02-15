package com.android.code.repository

import com.android.code.lib.network.BookService
import com.android.code.models.SearchResponse
import io.reactivex.rxjava3.core.Single


interface SearchRepository {

    fun search(
        query: String,
        page: Int
    ): Single<SearchResponse>
}

class SearchRepositoryImpl(
    private val bookService: BookService
) : SearchRepository {

    override fun search(
        query: String,
        page: Int
    ): Single<SearchResponse> {
        return bookService.search(query, page)
    }
}