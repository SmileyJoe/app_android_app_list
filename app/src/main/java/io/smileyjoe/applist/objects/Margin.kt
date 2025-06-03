package io.smileyjoe.applist.objects

import android.view.View
import android.view.ViewGroup

class Margin(val view: View) {
    private val params = view.layoutParams as ViewGroup.MarginLayoutParams
    var start = params.leftMargin
    var top = params.topMargin
    var end = params.rightMargin
    var bottom = params.bottomMargin

    fun apply() {
        params.apply {
            leftMargin = start
            topMargin = top
            rightMargin = end
            bottomMargin = bottom
        }
        view.layoutParams = params
    }
}
