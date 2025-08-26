package io.smileyjoe.library.utils

import android.graphics.ColorFilter
import android.util.Log
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.google.android.material.color.MaterialColors

fun LottieAnimationView.theme(themes: List<LottieTheme>) {
    themes.forEach { theme(it) }
}

fun LottieAnimationView.theme(theme: LottieTheme) {
    MaterialColors.getColorOrNull(context, theme.color)?.let {
        val colorFilter = SimpleColorFilter(it)
        val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(colorFilter)
        theme.paths.forEach { addValueCallback(it, LottieProperty.COLOR_FILTER, callback) }
    }
}

fun LottieAnimationView.logPaths() {
    resolveKeyPath(KeyPath("**")).forEach {
        Log.d("LottieAnimation", "KeyPath: $it")
    }
}