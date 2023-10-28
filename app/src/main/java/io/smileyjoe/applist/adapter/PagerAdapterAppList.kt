package io.smileyjoe.applist.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.fragment.AppListFragment

/**
 * Pager adapter for the app lists
 *
 * @param activity activity the pager is in
 */
class PagerAdapterAppList(
    private val activity: FragmentActivity,
    private val onLoadComplete: OnLoadComplete? = null,
    private val onItemSelected: OnItemSelected? = null
) : FragmentStateAdapter(activity) {

    /**
     * @see [AppListFragment.OnLoadComplete]
     */
    fun interface OnLoadComplete : AppListFragment.OnLoadComplete

    /**
     * @see [AppListFragment.OnItemSelected]
     */
    fun interface OnItemSelected : AppListFragment.OnItemSelected

    override fun createFragment(position: Int) =
        AppListFragment.newInstance(Page.fromPosition(position)).apply {
            onLoadComplete = this@PagerAdapterAppList.onLoadComplete
            onItemSelected = this@PagerAdapterAppList.onItemSelected
        }

    override fun getItemCount() = Page.values().size
}