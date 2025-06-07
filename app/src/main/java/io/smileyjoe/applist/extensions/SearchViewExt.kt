package io.smileyjoe.applist.extensions

import com.google.android.material.search.SearchView
import com.google.android.material.search.SearchView.TransitionListener

object SearchViewExt {

    /**
     * Add a callback for when the [SearchView] is opening
     *
     * @param opening
     * @see SearchView.addTransitionListener
     * @see SearchView.TransitionState.SHOWING
     */
    fun SearchView.onOpening(opening: () -> Unit) {
        addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                opening()
            }
        }
    }

    /**
     * Add a callback for when the [SearchView] is closing
     *
     * @param closing
     * @see SearchView.addTransitionListener
     * @see SearchView.TransitionState.HIDING
     */
    fun SearchView.onClosing(closing: () -> Unit) {
        addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDING) {
                closing()
            }
        }
    }

    /**
     * Close the [SearchView]
     *
     * @param onClosed call back for when it is closed
     */
    fun SearchView.close(onClosed: () -> Unit) {
        if (isShowing) {
            addTransitionListener(CloseListener(onClosed))
            hide()
        } else {
            onClosed()
        }
    }

    /**
     * Once off listener for when the [SearchView] is finished closing
     *
     * @param onClosed
     * @see close
     */
    private class CloseListener(
        private val onClosed: () -> Unit
    ) : TransitionListener {
        override fun onStateChanged(
            searchView: SearchView,
            previousState: SearchView.TransitionState,
            newState: SearchView.TransitionState
        ) {
            if (newState == SearchView.TransitionState.HIDDEN) {
                onClosed()
                searchView.removeTransitionListener(this)
            }
        }

    }

}