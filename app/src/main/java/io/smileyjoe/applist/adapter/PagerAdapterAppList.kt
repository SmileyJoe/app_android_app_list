package io.smileyjoe.applist.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.fragment.AppListFragment
import java.lang.ref.WeakReference

/**
 * Pager adapter for the app lists
 *
 * @param activity activity the pager is in
 */
class PagerAdapterAppList(
    private val activity: FragmentActivity,
    private val onLoadComplete: OnLoadComplete? = null,
    private val onItemSelected: OnItemSelected? = null,
    private val getFilters: GetFilters? = null
) : FragmentStateAdapter(activity) {

    // keep track of the fragments so we can update them with filtes //
    val fragments = mutableMapOf<Int, WeakReference<AppListFragment>>()

    /**
     * @see [AppListFragment.OnLoadComplete]
     */
    fun interface OnLoadComplete : AppListFragment.OnLoadComplete

    /**
     * @see [AppListFragment.OnItemSelected]
     */
    fun interface OnItemSelected : AppListFragment.OnItemSelected

    /**
     * @see AppListFragment.GetFilters
     */
    fun interface GetFilters : AppListFragment.GetFilters

    override fun createFragment(position: Int): AppListFragment {
        val fragment = AppListFragment.newInstance(Page.fromPosition(position)).apply {
            onLoadComplete = this@PagerAdapterAppList.onLoadComplete
            onItemSelected = this@PagerAdapterAppList.onItemSelected
            getFilters = this@PagerAdapterAppList.getFilters
        }
        fragments[position] = WeakReference(fragment)
        return fragment
    }

    override fun getItemCount() = Page.values().size

    fun refresh() {
        fragments.forEach { (_, fragment) ->
            fragment.get()?.refresh()
        }
    }
}