package io.smileyjoe.applist.extensions

import androidx.constraintlayout.motion.widget.MotionLayout
import io.smileyjoe.applist.extensions.MotionLayoutExt.setTransitionListener

/**
 * [MotionLayout] extensions
 * </p>
 * - [setTransitionListener]
 */
object MotionLayoutExt {

    /**
     * Helper to set the listener with a lambda
     *
     * @param callback fires anytime something happens
     */
    fun MotionLayout.setTransitionListener(callback: (layout: MotionLayout?) -> Unit) {
        setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) =
                callback.invoke(p0)

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) =
                callback.invoke(p0)

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) =
                callback.invoke(p0)

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) =
                callback.invoke(p0)
        })
    }

}