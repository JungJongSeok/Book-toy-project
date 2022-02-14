package com.android.code.lib.network

import com.android.code.models.DetailResponse
import com.android.code.models.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BookService {

    @GET("search/{query}/{page}")
    suspend fun search(
        @Path("query") query: String,
        @Path("page") page: Int
    ): SearchResponse

    @GET("books/{isbn13}")
    suspend fun detail(
        @Path("isbn13") isbn13: String
    ): DetailResponse
}