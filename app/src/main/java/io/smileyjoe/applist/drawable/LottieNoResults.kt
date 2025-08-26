package io.smileyjoe.applist.drawable

import io.smileyjoe.applist.R
import io.smileyjoe.library.utils.LottieRaw
import io.smileyjoe.library.utils.LottieTheme

class LottieNoResults : LottieRaw(R.raw.img_no_results) {

    private val pathFrame = listOf("main", "magnifier", "frame", "**")
    private val pathHandle = listOf("main", "magnifier", "handle", "**")
    private val pathSweat = listOf("main", "sweat", "**")
    private val themePrimary = LottieTheme(R.attr.colorPrimary)
        .add(pathFrame)
        .add(pathHandle)
    private val themeAccent = LottieTheme(R.attr.colorAccent)
        .add(pathSweat)

    init {
        add(themePrimary)
        add(themeAccent)
    }
}