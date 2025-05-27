package io.smileyjoe.applist.extensions

import com.google.android.material.search.SearchView

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

}