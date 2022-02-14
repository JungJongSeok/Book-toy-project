package com.android.code.ui.search

import androidx.lifecycle.LiveData
import com.android.code.models.Book
import com.android.code.repository.SearchRxRepository
import com.android.code.ui.BaseViewModel
import com.android.code.util.empty
import com.android.code.util.livedata.SafetyMutableLiveData
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

class SearchRxBaseViewModel(private val searchRxRepository: SearchRxRepository) :
    BaseViewModel(),
    SearchRxViewModelInput, SearchRxViewModelOutput {

    val inputs: SearchRxViewModelInput by lazy {
        this
    }
    val outputs: SearchRxViewModelOutput by lazy {
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
        val isLock = if (isRefreshing) {
            _refreshedSwipeRefreshLayout
        } else {
            _loading
        }
        if (isLock.value == true) {
            return
        }
        initSearchData()
        searchRxRepository.search()
            .doFinally { isLock.setValueSafety(false) }
            .subscribe({ response ->
                _responseData.setValueSafety(totalList to true)
            }, _error).addTo(compositeDisposable)
    }

    private var searchLock = PublishSubject.create<Boolean>()
    override fun search(text: String, isRefreshing: Boolean) {
        val isLock = if (isRefreshing) {
            _refreshedSwipeRefreshLayout
        } else {
            null
        }
        if (isLock?.value == true) {
            return
        }
        searchLock.onNext(true)
        initSearchData()
        currentText = text
        if (text.isEmpty()) {
            val recentSearchList =
                getPreferencesRecentSearchList()?.run { SearchRecentData(this) }
            val totalList = if (recentSearchList != null) {
                listOf(recentSearchList) + initializeDataList
            } else {
                initializeDataList
            }
            currentOffset = initializeOffset
            currentTotal = initializeTotal
            _searchedText.setValueSafety(String.empty())
            _responseData.setValueSafety(totalList to true)
            return
        }
        searchRxRepository.search()
            .takeUntil(searchLock.firstElement().toFlowable())
            .delay(300, TimeUnit.MILLISECONDS)
            .doFinally { isLock?.setValueSafety(false) }
            .subscribe({ response ->
                _responseData.setValueSafety(totalList to true)
            }, {
                if (it is CancellationException) {
                    return@subscribe
                }
                _error.setValueSafety(it)
            }).addTo(compositeDisposable)
    }

    override fun canSearchMore(): Boolean {
        return currentOffset < currentTotal
    }

    override fun searchMore() {
        if (_loading.value == true) {
            return
        }
        _loading.setValueSafety(true)
        searchRxRepository.search(
            nameStartsWith = currentText,
            offset = currentOffset
        )
            .doFinally { _loading.setValueSafety(false) }
            .subscribe({ response ->
                _responseData.setValueSafety(totalList to false)
            }, _error).addTo(compositeDisposable)
    }

    private fun initSearchData() {
        currentOffset = 0
        currentTotal = 0
        currentText = null
    }
}

interface SearchRxViewModelInput {
    fun initData(isRefreshing: Boolean = false)
    fun search(text: String, isRefreshing: Boolean = false)
    fun canSearchMore(): Boolean
    fun searchMore()
}

interface SearchRxViewModelOutput {
    val responseData: LiveData<Pair<List<Book>, Boolean>>
    val clickData: LiveData<Book>
    val refreshedSwipeRefreshLayout: LiveData<Boolean>
}