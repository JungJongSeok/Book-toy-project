package com.android.code.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.code.R
import com.android.code.util.toDp

class CommonSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SwipeRefreshLayout(context, attrs) {

    interface OnRefreshListener {

        fun refresh(view: SwipeRefreshLayout)
    }

    init {
        setProgressViewOffset(true, 0, 50.toDp(context).toInt())
        setColorSchemeResources(R.color.color_3746ff)
    }

    fun setOnRefreshListener(onRefreshListener: OnRefreshListener) {
        setOnRefreshListener {
            onRefreshListener.refresh(this)
        }
    }
}

@BindingAdapter("refreshListener")
fun setOnRefreshListener(
    refreshLayout: SwipeRefreshLayout,
    onRefreshListener: CommonSwipeRefreshLayout.OnRefreshListener
) {
    refreshLayout.setOnRefreshListener {
        onRefreshListener.refresh(refreshLayout)
    }
}