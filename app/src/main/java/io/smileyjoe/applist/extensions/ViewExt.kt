package io.smileyjoe.applist.extensions

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

object ViewExt {

    fun View.updateHeight(height: Float) = updateHeight(height.toInt())

    fun View.updateHeight(height: Int) =
        layoutParams.let { params ->
            params.height = height
            layoutParams = params
        }

    fun ViewGroup.addLayoutListener(callback: () -> Boolean) =
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (callback()) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

}