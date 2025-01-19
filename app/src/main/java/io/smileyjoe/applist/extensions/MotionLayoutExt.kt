package io.smileyjoe.applist.extensions

import androidx.constraintlayout.motion.widget.MotionLayout

/**
 * [MotionLayout] extensions
 */
object MotionLayoutExt {

    /**
     * Callback for when the state has changed
     *
     * @param callback with a boolean to represent expanded or contracted
     */
    fun MotionLayout.onStateChanged(callback: (expanded: Boolean) -> Unit) {
        setTransitionListener(object : MotionLayout.TransitionListener {
            private var isExpanded = false

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                // do nothing //
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                if (p3 > 0.9) {
                    if (!isExpanded) {
                        callback.invoke(true)
                        isExpanded = true
                    }
                } else {
                    if (isExpanded) {
                        callback.invoke(false)
                        isExpanded = false
                    }
                }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                // do nothing //
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // do nothing //
            }
        })
    }

    /**
     * Refresh the [MotionLayout]
     */
    fun MotionLayout.refresh() {
        rebuildScene()
        requestLayout()
    }

}