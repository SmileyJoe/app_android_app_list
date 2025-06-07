package io.smileyjoe.applist.extensions

object IntExt {

    /**
     * Make sure the value is greater then [min]
     *
     * @param min value
     * @return the current value, or [min]
     */
    fun Int.min(min: Int) =
        if (this < min) min else this

    /**
     * Make sure the value is less then [max]
     *
     * @param max value
     * @return the current value, or [max]
     */
    fun Int.max(max: Int) =
        if (this > max) max else this
}