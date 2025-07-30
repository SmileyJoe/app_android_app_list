package io.smileyjoe.applist.util

import android.view.View
import android.view.ViewTreeObserver

fun View.onLayout(onLayout: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(LayoutListener(this, onLayout))
}

class LayoutListener(
    private val view: View,
    private val onLayout: () -> Unit
) : ViewTreeObserver.OnGlobalLayoutListener {

    override fun onGlobalLayout() {
        if (view.measuredWidth > 0 && view.measuredHeight > 0) {
            onLayout()
            view.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }

}