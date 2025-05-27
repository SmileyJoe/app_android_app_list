package io.smileyjoe.applist.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit

object FragmentExt {

    fun <T : Fragment> FragmentContainerView.add(
        fragment: T,
        tag: String
    ) =
        FragmentManager.findFragmentManager(this).commit {
            add(id, fragment, tag)
        }

    fun <T : Fragment> FragmentContainerView.clear() {
        getFragment<T>()?.let {
            FragmentManager.findFragmentManager(this).commit {
                remove(it)
            }
        }
    }

}