package com.android.code.lib.network

import com.android.code.models.DetailResponse
import com.android.code.models.SearchResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface BookService {

    @GET("search/{query}/{page}")
    fun search(
        @Path("query") query: String,
        @Path("page") page: Int
    ): Single<SearchResponse>


    @GET("books/{isbn13}")
    fun detail(
        @Path("isbn13") isbn13: String
    ): Single<DetailResponse>

}