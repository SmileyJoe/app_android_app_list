package io.smileyjoe.applist.adapter

import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.enums.SearchResultsViewType
import io.smileyjoe.applist.enums.SearchResultsViewType.DETAILS
import io.smileyjoe.applist.enums.SearchResultsViewType.TITLE
import io.smileyjoe.applist.enums.SearchResultsViewType.UNKNOWN
import io.smileyjoe.applist.interfaces.OnAppSelected
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.viewholder.HeaderViewHolder
import io.smileyjoe.applist.viewholder.SearchResultsSummaryViewHolder
import io.smileyjoe.applist.viewholder.SearchResultsViewHolder

class SearchResultsAdapter(
    items: List<AppDetail> = ArrayList(),
    private val onAppSelected: OnAppSelected
) : RecyclerView.Adapter<HeaderViewHolder<AppDetail>>() {

    var items: List<AppDetail> = items
        set(value) {
            field = value
            viewTypes.clear()
            notifyDataSetChanged()
        }

    var searchTerm: String? = null

    private val viewTypes = mutableMapOf<Int, SearchResultsViewType>()

    override fun getItemViewType(position: Int): Int =
        if (position in 0..items.size) {
            SearchResultsViewType.get(getItem(position), searchTerm)
        } else {
            UNKNOWN
        }.also {
            viewTypes[position] = it
        }.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (SearchResultsViewType.fromId(viewType)) {
            TITLE -> SearchResultsSummaryViewHolder(parent, onAppSelected)
            DETAILS -> SearchResultsViewHolder(parent, onAppSelected)
            UNKNOWN -> SearchResultsViewHolder(parent, onAppSelected)
        }

    override fun onBindViewHolder(holder: HeaderViewHolder<AppDetail>, position: Int) =
        holder.bind(getItem(position), searchTerm, getHeader(position))

    override fun getItemCount() = items.size

    fun getItem(position: Int) = items[position]

    @StringRes
    private fun getHeader(position: Int): Int? {
        val viewType = getItemViewType(position)
        val prevViewType = getItemViewType(position - 1)

        return SearchResultsViewType.fromId(viewType).titleResId
            .takeIf { prevViewType != viewType }
    }

}