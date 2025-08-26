package io.smileyjoe.library.utils

import androidx.annotation.RawRes

open class LottieRaw(
    @RawRes val image: Int
) {
    val themes: MutableList<LottieTheme> = mutableListOf()

    fun add(theme: LottieTheme) =
        apply { this.themes.add(theme) }
}
