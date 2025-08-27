package io.smileyjoe.applist.adapter

import android.content.Context
import android.util.Log
import android.view.ViewGroup
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
import io.smileyjoe.library.recycler.Adapter

/**
 * Adapter to show a list of search results
 *
 * @param onAppSelected
 * @see SearchResultsFragment
 * @see SearchResultsViewType
 * @see SearchResultsViewHolder
 * @see SearchResultsSummaryViewHolder
 */
class SearchResultsAdapter(
    private val onAppSelected: OnAppSelected
) : Adapter<AppDetail, HeaderViewHolder<AppDetail, *>>() {

    /**
     * Search term that was used to filter the [items]
     */
    var searchTerm: String? = null

    private val viewTypes = mutableMapOf<Int, SearchResultsViewType>()

    override var items: List<AppDetail>?
        get() = super.items
        set(value) {
            viewTypes.clear()
            super.items = value
        }

    override fun getItemViewType(position: Int): Int =
        (getItem(position)?.let {
            // if the type is saved, return it, else get the type
            viewTypes[position] ?: SearchResultsViewType.get(it, searchTerm)
        } ?: UNKNOWN)
            .also {
                // also save the view type if it's not saved already
                if (!viewTypes.containsKey(position)) viewTypes[position] = it
            }.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HeaderViewHolder<AppDetail, *> {
        return when (SearchResultsViewType.fromId(viewType)) {
            TITLE -> SearchResultsSummaryViewHolder(parent, onAppSelected)
            DETAILS -> SearchResultsViewHolder(parent, onAppSelected)
            UNKNOWN -> SearchResultsViewHolder(parent, onAppSelected)
        }
    }

    override fun onBindViewHolder(holder: HeaderViewHolder<AppDetail, *>, position: Int) {
        getItem(position)?.let { holder.bind(it, searchTerm, getHeader(holder.context, position)) }
    }

    /**
     * Get the header based on the [SearchResultsViewType]
     *
     * @param context
     * @param position
     * @return section header or null
     */
    private fun getHeader(context: Context, position: Int): String? {
        val viewType = getItemViewType(position)
        val prevViewType = getItemViewType(position - 1)

        // only show a header if this is a new view type
        return SearchResultsViewType.fromId(viewType).titleResId
            .takeIf { prevViewType != viewType }
            ?.fromRes(context)
    }

}