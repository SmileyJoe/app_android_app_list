package io.smileyjoe.applist.extensions

object IntExt {

    /**
     * Make sure the value is greater then [min]
     *
     * @param min value
     * @param fallback value to return if this is less then [min], defaults to [min]
     * @return the current value, or [fallback]
     */
    fun Int.min(min: Int, fallback: Int = min) =
        if (this <= min) fallback else this

    /**
     * Make sure the value is less then [max]
     *
     * @param max value
     * @param fallback value to return if this is less then [max], defaults to [max]
     * @return the current value, or [max]
     */
    fun Int.max(max: Int, fallback: Int = max) =
        if (this > max) fallback else this
}