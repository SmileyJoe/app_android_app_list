package io.smileyjoe.applist.extensions

import com.google.android.material.search.SearchView
import com.google.android.material.search.SearchView.TransitionListener

object SearchViewExt {

    fun SearchView.onOpening(opening: () -> Unit) {
        addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                opening()
            }
        }
    }

    fun SearchView.onClosing(closing: () -> Unit) {
        addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDING) {
                closing()
            }
        }
    }

    fun SearchView.close(onClosed: () -> Unit) {
        if(isShowing) {
            addTransitionListener(CloseListener(onClosed))
            hide()
        } else {
            onClosed()
        }
    }

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