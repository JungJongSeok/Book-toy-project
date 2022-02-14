package com.android.code.ui.search

import androidx.lifecycle.LiveData
import com.android.code.models.Book
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

    private val _clickData = SafetyMutableLiveData<Book>()
    override val clickData: LiveData<Book>
        get() = _clickData

    private val _refreshedSwipeRefreshLayout = SafetyMutableLiveData<Boolean>()
    override val refreshedSwipeRefreshLayout: LiveData<Boolean>
        get() = _refreshedSwipeRefreshLayout

    private var currentOffset = 0
    private var currentTotal = 0
    private var currentText: String? = null

    override fun initData(isRefreshing: Boolean) {
        launchDataLoad(
            if (isRefreshing) {
                _refreshedSwipeRefreshLayout
            } else {
                _loading
            },
            onLoad = {
                initSearchData()
                _responseData.setValueSafety(totalList to true)
            },
            onError = {
                _error.setValueSafety(it)
            }
        )
    }

    private var searchTask: Deferred<List<Book>?>? = null
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
                    return@launchDataLoad
                }
                searchTask = async {
                    kotlin.runCatching {
                        delay(300)
                    }.getOrNull()
                }
                searchTask?.await()?.run {
                    _responseData.setValueSafety(totalList to true)
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
        return currentOffset < currentTotal
    }

    override fun searchMore() {
        launchDataLoad(
            onLoad = {
                _responseData.setValueSafety(totalList to false)
            },
            onError = {
                _error.setValueSafety(it)
            }
        )
    }

    private fun initSearchData() {
    }
}

interface SearchViewModelInput {
    fun initData(isRefreshing: Boolean = false)
    fun search(text: String, isRefreshing: Boolean = false)
    fun canSearchMore(): Boolean
    fun searchMore()
}

interface SearchViewModelOutput {
    val responseData: LiveData<Pair<List<SearchData>, Boolean>>
    val clickData: LiveData<SearchData>
    val refreshedSwipeRefreshLayout: LiveData<Boolean>
}