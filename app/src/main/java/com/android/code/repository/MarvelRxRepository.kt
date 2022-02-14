package com.android.code.repository

import com.android.code.lib.network.BookRxService
import com.android.code.models.DetailResponse
import com.android.code.models.SearchResponse
import io.reactivex.rxjava3.core.Single


interface SearchRxRepository {

    fun search(
        query: String,
        page: Int
    ): Single<SearchResponse>

    fun detail(
        isbn13: String
    ): Single<DetailResponse>

}

class SearchRxRepositoryImpl(
    private val bookRxService: BookRxService
) : SearchRxRepository {

    override fun search(
        query: String,
        page: Int
    ): Single<SearchResponse> {
        return bookRxService.search(query, page)
    }

    override fun detail(
        isbn13: String
    ): Single<DetailResponse> {
        return bookRxService.detail(isbn13)
    }

}