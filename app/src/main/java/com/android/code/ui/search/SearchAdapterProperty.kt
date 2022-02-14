package com.android.code.ui.search

import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager

interface SearchAdapterProperty {
    val requestManager: RequestManager
    val searchedData: LiveData<SearchBaseData>
    val searchedText: LiveData<String>
    fun search(text: String)
    fun removeRecentSearch(text: String)
    fun clickData(searchData: SearchData)
}