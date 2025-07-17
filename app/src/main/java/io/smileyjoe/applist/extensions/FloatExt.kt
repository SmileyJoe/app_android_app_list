package io.smileyjoe.applist.extensions

object FloatExt {

    /**
     * Make sure the value is greater then [min]
     *
     * @param min value
     * @param fallback value to return if this is less then [min], defaults to [min]
     * @return the current value, or [fallback]
     */
    fun Float.min(min: Float, fallback: Float = min) =
        if (this <= min) fallback else this

    /**
     * Make sure the value is less then [max]
     *
     * @param max value
     * @param fallback value to return if this is less then [max], defaults to [max]
     * @return the current value, or [max]
     */
    fun Float.max(max: Float, fallback: Float = max) =
        if (this > max) fallback else this

}