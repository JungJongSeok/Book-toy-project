package com.android.code.ui.search

import androidx.lifecycle.LiveData
import com.android.code.models.Book
import com.android.code.repository.SearchRxRepository
import com.android.code.ui.BaseViewModel
import com.android.code.util.livedata.SafetyMutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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

    private val _refreshedSwipeRefreshLayout = SafetyMutableLiveData<Boolean>()
    override val refreshedSwipeRefreshLayout: LiveData<Boolean>
        get() = _refreshedSwipeRefreshLayout

    private var currentPage = 1
    private var currentText = ""
    private var totalCount = 0

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
            _responseData.setValueSafety(emptyList<Book>() to true)
            return
        }
        searchRxRepository.search(text, currentPage)
            .takeUntil(searchLock.firstElement().toFlowable())
            .delay(300, TimeUnit.MILLISECONDS)
            .doFinally { isLock?.setValueSafety(false) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                totalCount = response.parseIntTotal()
                _responseData.setValueSafety((response.books ?: emptyList()) to true)
            }, {
                if (it is CancellationException) {
                    return@subscribe
                }
                _error.setValueSafety(it)
            }).addTo(compositeDisposable)
    }

    override fun canSearchMore(): Boolean {
        return (responseData.value?.first?.size ?: Int.MAX_VALUE) < totalCount
    }

    override fun searchMore() {
        if (_loading.value == true) {
            return
        }
        _loading.setValueSafety(true)
        searchRxRepository.search(
            query = currentText,
            page = currentPage
        )
            .doFinally { _loading.setValueSafety(false) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                currentPage += 1
                totalCount = response.parseIntTotal()
                val previousList = _responseData.value?.first ?: emptyList()
                _responseData.setValueSafety(
                    previousList + (response.books ?: emptyList())
                        to false)
            }, _error).addTo(compositeDisposable)
    }

    private fun initSearchData() {
        currentPage = 1
        currentText = ""
        totalCount = 0
    }
}

interface SearchRxViewModelInput {
    fun search(text: String, isRefreshing: Boolean = false)
    fun canSearchMore(): Boolean
    fun searchMore()
}

interface SearchRxViewModelOutput {
    val responseData: LiveData<Pair<List<Book>, Boolean>>
    val refreshedSwipeRefreshLayout: LiveData<Boolean>
}