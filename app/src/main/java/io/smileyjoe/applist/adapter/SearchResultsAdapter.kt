package io.smileyjoe.applist.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.enums.SearchResultsViewType
import io.smileyjoe.applist.enums.SearchResultsViewType.DETAILS
import io.smileyjoe.applist.enums.SearchResultsViewType.TITLE
import io.smileyjoe.applist.enums.SearchResultsViewType.UNKNOWN
import io.smileyjoe.applist.extensions.StringExt.fromRes
import io.smileyjoe.applist.fragment.SearchResultsFragment
import io.smileyjoe.applist.interfaces.OnAppSelected
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.viewholder.HeaderViewHolder
import io.smileyjoe.applist.viewholder.SearchResultsSummaryViewHolder
import io.smileyjoe.applist.viewholder.SearchResultsViewHolder

/**
 * Adapter to show a list of search results
 *
 * @param items
 * @param onAppSelected
 * @see SearchResultsFragment
 * @see SearchResultsViewType
 * @see SearchResultsViewHolder
 * @see SearchResultsSummaryViewHolder
 */
class SearchResultsAdapter(
    items: List<AppDetail> = ArrayList(),
    private val onAppSelected: OnAppSelected
) : RecyclerView.Adapter<HeaderViewHolder<AppDetail>>() {

    /**
     * Items to show
     */
    var items: List<AppDetail> = items
        set(value) {
            field = value
            viewTypes.clear()
            notifyDataSetChanged()
        }

    /**
     * Search term that was used to filter the [items]
     */
    var searchTerm: String? = null

    private val viewTypes = mutableMapOf<Int, SearchResultsViewType>()

    override fun getItemViewType(position: Int): Int =
        if (position in 0..items.size) {
            // if the type is saved, return it, else get the type
            viewTypes[position] ?: SearchResultsViewType.get(getItem(position), searchTerm)
        } else {
            // else we don't know what it is
            UNKNOWN
        }.also {
            // also save the view type if it's not saved already
            if (!viewTypes.containsKey(position)) viewTypes[position] = it
        }.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (SearchResultsViewType.fromId(viewType)) {
            TITLE -> SearchResultsSummaryViewHolder(parent, onAppSelected)
            DETAILS -> SearchResultsViewHolder(parent, onAppSelected)
            UNKNOWN -> SearchResultsViewHolder(parent, onAppSelected)
        }

    override fun onBindViewHolder(holder: HeaderViewHolder<AppDetail>, position: Int) =
        holder.bind(getItem(position), searchTerm, getHeader(holder.context, position))

    override fun getItemCount() = items.size

    fun getItem(position: Int) = items[position]

    private fun getHeader(context: Context, position: Int): String? {
        val viewType = getItemViewType(position)
        val prevViewType = getItemViewType(position - 1)

        // only show a header if this is a new view type
        return SearchResultsViewType.fromId(viewType).titleResId
            .takeIf { prevViewType != viewType }
            ?.fromRes(context)
    }

}