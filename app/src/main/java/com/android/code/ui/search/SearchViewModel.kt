package com.android.code.ui.search

import androidx.lifecycle.LiveData
import com.android.code.models.Book
import com.android.code.models.SearchResponse
import com.android.code.repository.SearchRepository
import com.android.code.ui.BaseViewModel
import com.android.code.util.livedata.SafetyMutableLiveData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException

class SearchBaseViewModel(private val searchRepository: SearchRepository) :
    BaseViewModel(),
    SearchViewModelInput, SearchViewModelOutput {

    val inputs: SearchViewModelInput by lazy {
        this
    }
    val outputs: SearchViewModelOutput by lazy {
        this
    }

    private val _responseData = SafetyMutableLiveData<Pair<List<Book>, Boolean>>()
    override val responseData: LiveData<Pair<List<Book>, Boolean>>
        get() = _responseData

    private val _refreshedSwipeRefreshLayout = SafetyMutableLiveData<Boolean>()
    override val refreshedSwipeRefreshLayout: LiveData<Boolean>
        get() = _refreshedSwipeRefreshLayout

    private var currentPage = 1
    private var currentText = ""
    private var totalCount = 0

    private var searchTask: Deferred<SearchResponse?>? = null
    override fun search(text: String, isRefreshing: Boolean) {
        launchDataLoad(
            if (isRefreshing) {
                _refreshedSwipeRefreshLayout
            } else {
                null
            },
            onLoad = {
                searchTask?.cancel()
                initSearchData()
                currentText = text
                if (text.isEmpty()) {
                    _responseData.setValueSafety(emptyList<Book>() to true)
                    return@launchDataLoad
                }
                searchTask = async {
                    kotlin.runCatching {
                        delay(300)
                        searchRepository.search(text, currentPage)
                    }.getOrNull()
                }
                searchTask?.await()?.let { response ->
                    totalCount = response.parseIntTotal()
                    _responseData.setValueSafety((response.books ?: emptyList()) to true)
                }
            },
            onError = {
                if (it is CancellationException) {
                    return@launchDataLoad
                }
                _error.setValueSafety(it)
            }
        )
    }

    override fun canSearchMore(): Boolean {
        return (responseData.value?.first?.size ?: Int.MAX_VALUE) < totalCount
    }

    override fun searchMore() {
        launchDataLoad(
            onLoad = {
                val response = searchRepository.search(
                    query = currentText,
                    page = currentPage
                )
                currentPage += 1
                totalCount = response.parseIntTotal()
                val previousList = _responseData.value?.first ?: emptyList()
                _responseData.setValueSafety(
                    previousList + (response.books ?: emptyList())
                        to false)
            },
            onError = {
                _error.setValueSafety(it)
            }
        )
    }

    private fun initSearchData() {
        currentPage = 1
        currentText = ""
        totalCount = 0
    }
}

interface SearchViewModelInput {
    fun search(text: String, isRefreshing: Boolean = false)
    fun canSearchMore(): Boolean
    fun searchMore()
}

interface SearchViewModelOutput {
    val responseData: LiveData<Pair<List<Book>, Boolean>>
    val refreshedSwipeRefreshLayout: LiveData<Boolean>
}