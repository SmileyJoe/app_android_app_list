package io.smileyjoe.applist.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import kotlin.math.abs

/**
 * Behaviour for a [ExtendedFloatingActionButton], helps with two things:
 * </p>
 * - Move the fab when a [SnackbarLayout] shows
 * - Hides the fab when a user scrolls down, shows it when the user scrolls up
 */
class ExtendedFabBehavior : CoordinatorLayout.Behavior<ExtendedFloatingActionButton> {

    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    /**
     * The fab moves up and down to make space for a snackbar, so the view
     * depends on a snackbar
     *
     * @see [CoordinatorLayout.Behavior.layoutDependsOn] for param details
     */
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        dependency: View
    ): Boolean {
        return dependency is SnackbarLayout
    }

    /**
     * Once the snackbar is removed, reset the fab position
     *
     * @see [CoordinatorLayout.Behavior.onDependentViewRemoved] for param details
     */
    override fun onDependentViewRemoved(
        parent: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        dependency: View
    ) {
        child.translationY = 0.0f
    }

    /**
     * When the snackbar changes position, update the fab to move it up
     * or down
     *
     * @see [CoordinatorLayout.Behavior.onDependentViewChanged] for param details
     */
    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        dependency: View
    ): Boolean {
        val oldY = child.translationY
        val newY = dependency.translationY - dependency.height
        child.translationY = newY
        return oldY != newY
    }

    /**
     * This is called when the user scrolls, this is where we check the direction or scroll and
     * everything else.
     *
     * @see [CoordinatorLayout.Behavior.onNestedScroll] for details on the parameters
     */
    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        // the scroll can be positive or negative, so make sure it's positive and check if //
        // it should be ignored or not based on the leeway //
        if (abs(dy) >= 10) {
            // if dy is negative the user is scrolling back up, so show content, else hide it //
            if (dy < 0) {
                child.show()
            } else {
                child.hide {
                    // The behaviour isn't called if a view is set to GONE, when hide is called, the //
                    // visibility is set to GONE, so this just overrides that so it can be reshown //
                    child.visibility = View.INVISIBLE
                }
            }
        }
    }

    /**
     * Copied from somewhere online, basically, if it's scrolling vertically we want to listen to
     * it, else we want to ignore it
     *
     * @see [CoordinatorLayout.Behavior.onStartNestedScroll] for parameter details
     */
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL !== 0
    }

    /**
     * Helper extension to save creating an object of [FloatingActionButton.OnVisibilityChangedListener]
     * when listening for a hidden callback
     */
    private fun ExtendedFloatingActionButton.hide(onHidden: () -> Unit) {
        hide(object : ExtendedFloatingActionButton.OnChangedCallback() {
            override fun onHidden(fab: ExtendedFloatingActionButton?) {
                super.onHidden(fab)
                onHidden.invoke()
            }
        })
    }

}