package com.android.code.ui.book

import androidx.lifecycle.LiveData
import com.android.code.models.DetailResponse
import com.android.code.repository.BookRepository
import com.android.code.ui.BaseViewModel
import com.android.code.util.livedata.SafetyMutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo

class BookViewModel(private val bookRepository: BookRepository) :
    BaseViewModel(),
    BookViewModelInput, BookViewModelOutput {

    val inputs: BookViewModelInput by lazy {
        this
    }
    val outputs: BookViewModelOutput by lazy {
        this
    }

    private val _detailData = SafetyMutableLiveData<DetailResponse>()
    override val detailData: LiveData<DetailResponse>
        get() = _detailData

    override fun init(isbn13: String) {
        val isLock = _loading
        if (isLock.value == true) {
            return
        }
        isLock.setValueSafety(true)
        bookRepository.detail(isbn13)
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLock.setValueSafety(false) }
            .subscribe(_detailData, _error).addTo(compositeDisposable)
    }
}

interface BookViewModelInput {
    fun init(isbn13: String)
}

interface BookViewModelOutput {
    val detailData: LiveData<DetailResponse>
}