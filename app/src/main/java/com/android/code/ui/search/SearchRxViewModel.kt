package com.android.code.ui.search

import androidx.lifecycle.LiveData
import com.android.code.models.Book
import com.android.code.models.SearchResponse
import com.android.code.repository.SearchRepository
import com.android.code.ui.BaseViewModel
import com.android.code.util.livedata.SafetyMutableLiveData
import com.android.code.util.zipToTriple
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.ceil

class SearchRxBaseViewModel(private val searchRepository: SearchRepository) :
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
    private var isSpecialOperator = false

    private var searchDisposable: Disposable? = null
    private var searchLock = PublishSubject.create<Boolean>()
    override fun search(text: String, isRefreshing: Boolean) {
        val isLock = if (isRefreshing) {
            _refreshedSwipeRefreshLayout
        } else {
            _loading
        }
        searchLock.onNext(true)
        searchDisposable?.dispose()
        initSearchData()
        currentText = text
        if (text.isEmpty()) {
            isLock.setValueSafety(false)
            _responseData.setValueSafety(emptyList<Book>() to true)
            return
        }
        val indexOfOrOperator = text.indexOf('|')
        val indexOfNotOperator = text.indexOf('-')

        isLock.setValueSafety(true)
        searchDisposable = when {
            indexOfOrOperator != -1 && indexOfNotOperator == -1 -> {
                searchOrOperator(text)
            }
            indexOfOrOperator == -1 && indexOfNotOperator != -1 -> {
                searchNotOperator(text)
            }
            indexOfOrOperator != -1 && indexOfNotOperator != -1 -> {
                if (indexOfOrOperator > indexOfNotOperator) {
                    searchNotOperator(text)
                } else {
                    searchOrOperator(text)
                }
            }
            else -> {
                searchNormal(text)
            }
        }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLock.setValueSafety(false) }
            .subscribe({ books ->
                _responseData.setValueSafety(books to true)
            }, {
                if (it is CancellationException
                    || it is java.util.concurrent.ExecutionException) {
                    return@subscribe
                }
                _error.setValueSafety(it)
            }).addTo(compositeDisposable)
    }

    private fun searchNormal(text: String): Single<List<Book>> {
        isSpecialOperator = false
        return searchRepository.search(text, currentPage)
            .takeUntil(searchLock.firstElement().toFlowable())
            .delay(300, TimeUnit.MILLISECONDS)
            .map { response ->
                totalCount = response.parseIntTotal()
                response.books ?: emptyList()
            }
    }

    private fun searchOrOperator(text: String): Single<List<Book>> {
        val split = text.split('|')
        return if (split.size < 2) {
            searchNormal(text)
        } else if (split[0].isEmpty() || split[1].isEmpty()) {
            searchNormal(text)
        } else {
            isSpecialOperator = true
            Single.zip(searchRepository.search(split[0], currentPage),
                searchRepository.search(split[1], currentPage),
                Single.just(split),
                zipToTriple())
                .takeUntil(searchLock.firstElement().toFlowable())
                .delay(300, TimeUnit.MILLISECONDS)
                .map { (responseFirst, responseSecond, split) ->
                    responseFirst.searchCombineTask(split[0]) to
                        responseSecond.searchCombineTask(split[1])
                }
                .flatMap { (taskFirst, taskSecond) ->
                    Single.fromCallable {
                        (taskFirst.map { it.get() } + taskSecond.map { it.get() })
                            .map { it.books ?: emptyList() }
                            .flatten()
                    }.subscribeOn(Schedulers.io())
                }
        }
    }

    private fun searchNotOperator(text: String): Single<List<Book>> {
        val split = text.split('-')
        return if (split.size < 2) {
            searchNormal(text)
        } else if (split[0].isEmpty() || split[1].isEmpty()) {
            searchNormal(text)
        } else {
            isSpecialOperator = true
            Single.zip(searchRepository.search(split[0], currentPage),
                searchRepository.search(split[1], currentPage),
                Single.just(split),
                zipToTriple())
                .takeUntil(searchLock.firstElement().toFlowable())
                .delay(300, TimeUnit.MILLISECONDS)
                .map { (responseFirst, responseSecond, split) ->
                    responseFirst.searchCombineTask(split[0]) to
                        responseSecond.searchCombineTask(split[1])
                }
                .flatMap { (taskFirst, taskSecond) ->
                    Single.fromCallable {
                        val resultTaskFirst =
                            taskFirst.map { it.get() }.map { it.books ?: emptyList() }.flatten()
                        val resultTaskSecond =
                            taskSecond.map { it.get() }.map { it.books ?: emptyList() }.flatten()
                        resultTaskFirst.filter { !resultTaskSecond.contains(it) }
                    }.subscribeOn(Schedulers.io())
                }
        }
    }

    private fun SearchResponse.searchCombineTask(text: String): List<Future<SearchResponse>> {
        val totalSize = this.parseIntTotal()
        val listSize = this.books?.size ?: 0
        if (listSize == 0) {
            return listOf(Single.just(this).toFuture())
        }
        val totalCount = ceil(totalSize / listSize.toDouble()).toInt()

        return if (totalCount <= 1) {
            listOf(Single.just(this).toFuture())
        } else {
            (2..totalCount).map {
                searchRepository.search(text, it)
                    .retryWhen { t -> t.take(10).delay(500, TimeUnit.MILLISECONDS, Schedulers.io()) }
                    .takeUntil(searchLock.firstElement().toFlowable())
                    .toFuture()
            }
        }
    }

    override fun canSearchMore(): Boolean {
        return (responseData.value?.first?.size ?: Int.MAX_VALUE) < totalCount && !isSpecialOperator
    }

    override fun searchMore() {
        if (_loading.value == true) {
            return
        }
        _loading.setValueSafety(true)
        searchRepository.search(
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