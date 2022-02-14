package com.android.code.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.android.code.R

@SuppressLint("ClickableViewAccessibility")
class ZoomEffectLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var zoomView: View? = null

    init {
        isClickable = true

        setOnTouchListener { _, event ->
            val view = zoomView ?: return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.clearAnimation()
                    view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.zoom_in))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.clearAnimation()
                    view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.zoom_out))
                }
            }
            false
        }
    }
}

@BindingAdapter("zoomLayout")
fun setZoomLayout(zoomEffectLayout: ZoomEffectLayout, view: View) {
    zoomEffectLayout.zoomView = view
}