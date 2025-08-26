package io.smileyjoe.library.utils

import androidx.annotation.AttrRes
import com.airbnb.lottie.model.KeyPath

class LottieTheme(
    @AttrRes val color: Int
) {

    val paths: MutableList<KeyPath> = mutableListOf()

    fun add(vararg paths: String) =
        apply { this.paths.add(KeyPath(*paths)) }

    fun add(paths: List<String>) =
        apply { this.paths.add(KeyPath(*paths.toTypedArray())) }
}
