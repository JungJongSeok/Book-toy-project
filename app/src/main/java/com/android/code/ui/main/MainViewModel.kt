package com.android.code.ui.main

import androidx.lifecycle.LiveData
import com.android.code.ui.BaseViewModel
import com.android.code.util.livedata.SafetyMutableLiveData

class MainViewModel : BaseViewModel(),
    MainViewModelInput, MainViewModelOutput {

    val inputs: MainViewModelInput = this
    val outputs: MainViewModelOutput = this

    private val _scrollToTop = SafetyMutableLiveData<Int>()
    override val scrollToTop: LiveData<Int>
        get() = _scrollToTop

    override fun scrollToTop(@MainActivity.Page page: Int) {
        _scrollToTop.setValueSafety(page)
    }
}

interface MainViewModelInput {
    fun scrollToTop(@MainActivity.Page page: Int)
}

interface MainViewModelOutput {
    val scrollToTop: LiveData<Int>
}