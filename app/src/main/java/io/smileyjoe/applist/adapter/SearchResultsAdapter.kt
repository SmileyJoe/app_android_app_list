package io.smileyjoe.applist.adapter

import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.interfaces.OnAppSelected
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.viewholder.HeaderViewHolder
import io.smileyjoe.applist.viewholder.SearchResultsSummaryViewHolder
import io.smileyjoe.applist.viewholder.SearchResultsViewHolder

class SearchResultsAdapter(
    items: List<AppDetail> = ArrayList(),
    private val onAppSelected: OnAppSelected
) : RecyclerView.Adapter<HeaderViewHolder<AppDetail>>() {

    companion object {
        const val VIEW_UNKNOWN = 0
        const val VIEW_TITLE = 1
        const val VIEW_OTHER = 2
    }

    var items: List<AppDetail> = items
        set(value) {
            field = value
            viewTypes.clear()
            notifyDataSetChanged()
        }

    private val viewTypes = mutableMapOf<Int, Int>()

    var searchTerm: String? = null

    private fun isTitle(position: Int): Boolean =
        searchTerm?.let {
            getItem(position).name?.contains(it, ignoreCase = true) ?: false
        } ?: true

    override fun getItemViewType(position: Int): Int =
        if (position in 0..items.size) {
            viewTypes[position] ?: run {
                val viewType = if (isTitle(position)) {
                    VIEW_TITLE
                } else {
                    VIEW_OTHER
                }
                viewTypes[position] = viewType
                return viewType
            }
        } else {
            VIEW_UNKNOWN
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == VIEW_TITLE) {
            SearchResultsSummaryViewHolder(parent, onAppSelected)
        } else {
            SearchResultsViewHolder(parent, onAppSelected)
        }

    override fun onBindViewHolder(holder: HeaderViewHolder<AppDetail>, position: Int) =
        holder.bind(getItem(position), searchTerm, getHeader(position))

    override fun getItemCount() = items.size

    fun getItem(position: Int) = items[position]

    @StringRes
    private fun getHeader(position: Int): Int? {
        val viewType = getItemViewType(position)
        val prevViewType = getItemViewType(position - 1)

        if (prevViewType != viewType) {
            return when (viewType) {
                VIEW_TITLE -> R.string.header_search_title
                VIEW_OTHER -> R.string.header_search_summary
                else -> null
            }
        } else {
            return null
        }
    }

}