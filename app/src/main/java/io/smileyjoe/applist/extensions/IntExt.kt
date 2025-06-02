package io.smileyjoe.applist.extensions

object IntExt {

    fun Int.min(min: Int) =
        if (this < min) min else this

    fun Int.max(max: Int) =
        if (this > max) max else this
}