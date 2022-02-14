package com.android.code.util

import androidx.recyclerview.widget.RecyclerView

interface ViewDetectable {
    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder)

    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder)
}