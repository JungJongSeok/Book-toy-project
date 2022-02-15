package com.android.code.repository

import com.android.code.lib.network.BookService
import com.android.code.models.DetailResponse
import io.reactivex.rxjava3.core.Single


interface BookRepository {

    fun detail(
        isbn13: String
    ): Single<DetailResponse>

}

class BookRepositoryImpl(
    private val bookService: BookService
) : BookRepository {

    override fun detail(
        isbn13: String
    ): Single<DetailResponse> {
        return bookService.detail(isbn13)
    }

}