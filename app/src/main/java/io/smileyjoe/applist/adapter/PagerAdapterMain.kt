package io.smileyjoe.applist.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.fragment.AppListFragment

class PagerAdapterMain(var activity: FragmentActivity) : FragmentStateAdapter(activity) {

    fun interface Listener : AppListFragment.Listener
    fun interface ItemSelectedListener : AppListFragment.ItemSelectedListener

    var listener: Listener? = null
    var itemSelectedListener: ItemSelectedListener? = null

    override fun createFragment(position: Int): Fragment {
        return AppListFragment.newInstance(Page.fromPosition(position)).apply {
            listener = this@PagerAdapterMain.listener
            itemSelectedListener = this@PagerAdapterMain.itemSelectedListener
        }
    }

    override fun getItemCount(): Int {
        return Page.values().size
    }
}