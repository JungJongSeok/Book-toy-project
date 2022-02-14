package com.android.code.ui.views.progress

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.android.code.databinding.LayoutLoadingDialogBinding

class LoadingDialog(context: Context) : Dialog(context) {

    private val binding =
        LayoutLoadingDialogBinding.inflate(LayoutInflater.from(context), null, false)

    init {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}