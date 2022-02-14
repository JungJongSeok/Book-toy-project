package com.android.code.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.android.code.R
import com.android.code.ui.views.progress.LoadingDialog
import com.bumptech.glide.Glide
import timber.log.Timber

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    @LayoutRes
    abstract fun getLayoutResId(): Int
    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected val loadingDialog by lazy {
        LoadingDialog(requireContext())
    }

    val requestManager by lazy {
        Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (getLayoutResId() == 0) {
            throw RuntimeException("Invalid Layout Resource ID")
        }
        _binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        setViewModelOutputs()
        setViewModelInputs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun initView(savedInstanceState: Bundle?) {}
    open fun setViewModelOutputs() {}
    open fun setViewModelInputs() {}

    protected fun showNetworkError(throwable: Throwable) {
        Timber.e(throwable)

        Toast.makeText(
            requireContext(),
            throwable.message ?: getString(R.string.common_network_error),
            Toast.LENGTH_SHORT
        ).show()
    }
}