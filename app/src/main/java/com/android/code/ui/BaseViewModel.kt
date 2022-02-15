package com.android.code.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.android.code.util.livedata.SafetyMutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresActivityViewModel(val value: KClass<out BaseViewModel>)

open class BaseViewModel : ViewModel() {

    protected val _loading: SafetyMutableLiveData<Boolean> = SafetyMutableLiveData()
    val loading: LiveData<Boolean>
        get() = _loading

    protected val _error: SafetyMutableLiveData<Throwable> = SafetyMutableLiveData()
    val error: LiveData<Throwable>
        get() = _error

    protected val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
