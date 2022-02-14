package com.android.code.ui

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.android.code.R
import com.android.code.ui.views.progress.LoadingDialog
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

abstract class BaseActivity<T : ViewDataBinding, F : BaseViewModel> : AppCompatActivity() {

    @LayoutRes
    abstract fun getLayoutResId(): Int

    protected val binding: T by lazy {
        DataBindingUtil.setContentView(this, getLayoutResId()) as T
    }

    protected val viewModel: F
        get() = createViewModel()

    @Suppress("UNCHECKED_CAST")
    private fun createViewModel(): F =
        (javaClass.getAnnotation(RequiresActivityViewModel::class.java)?.let {
            getViewModel(clazz = it.value)
        } ?: throw RuntimeException()) as F

    private val loadingDialog by lazy {
        LoadingDialog(this)
    }

    private val transitionMode: TransitionMode = TransitionMode.HORIZONTAL


    val requestManager by lazy {
        Glide.with(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransition(isEnter = true)
        callAbstractFunc(savedInstanceState)

        viewModel.loading.observe(this) { isShow ->
            if (isShow) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
    }

    override fun finish() {
        super.finish()
        setTransition(isEnter = false)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setTransition(isEnter = false)
    }

    private fun setTransition(isEnter: Boolean = true) {
        when (transitionMode) {
            TransitionMode.HORIZONTAL -> {
                if (isEnter) {
                    overridePendingTransition(R.anim.horizon_enter, R.anim.none)
                } else {
                    overridePendingTransition(R.anim.none, R.anim.horizon_exit)
                }
            }
            TransitionMode.VERTICAL -> {
                if (isEnter) {
                    overridePendingTransition(R.anim.vertical_enter, R.anim.none)
                } else {
                    overridePendingTransition(R.anim.none, R.anim.vertical_exit)
                }
            }
            else -> Unit
        }
    }

    private fun callAbstractFunc(savedInstanceState: Bundle?) {
        initView(savedInstanceState)
        setViewModelOutputs()
        setViewModelInputs()
    }

    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun setViewModelOutputs()
    abstract fun setViewModelInputs()

    protected fun showNetworkError(throwable: Throwable) {
        Timber.e(throwable)

        Toast.makeText(
            this,
            throwable.message ?: getString(R.string.common_network_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    enum class TransitionMode {
        NONE,
        HORIZONTAL,
        VERTICAL,
    }

}