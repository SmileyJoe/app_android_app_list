package io.smileyjoe.applist.extensions

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

object ViewExt {

    fun View.updateSize(height: Float? = null, width:Float? = null) =
        updateSize(height = height?.toInt(), width = width?.toInt())

    fun View.updateSize(height: Int? = null, width:Int? = null) {
        var params = layoutParams

        if(height != null){
            params.height = height
        }

        if(width != null){
            params.width = width
        }

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

    fun View.below(viewAbove:View, marginTop:Int = 0){
        y = viewAbove.y + viewAbove.measuredHeight + marginTop
    }

}