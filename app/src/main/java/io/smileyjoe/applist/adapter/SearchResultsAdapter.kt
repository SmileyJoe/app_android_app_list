package io.smileyjoe.applist.adapter

import android.content.res.Resources
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.interfaces.OnAppSelected
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.viewholder.BindingViewHolder
import io.smileyjoe.applist.viewholder.SearchResultsSummaryViewHolder
import io.smileyjoe.applist.viewholder.SearchResultsViewHolder

class SearchResultsAdapter(
    items: List<AppDetail> = ArrayList(),
    private val onAppSelected: OnAppSelected
) : RecyclerView.Adapter<BindingViewHolder<AppDetail>>() {

    companion object {
        const val VIEW_TITLE = 1
        const val VIEW_OTHER = 2
    }

    var items: List<AppDetail> = items
        set(value) {
            field = value
            firstSummary = true
            notifyDataSetChanged()
        }

    var searchTerm: String? = null
    var firstSummary: Boolean = true

    private fun isTitle(position: Int): Boolean =
        searchTerm?.let {
            getItem(position).name?.contains(it, ignoreCase = true) ?: false
        } ?: true

    override fun getItemViewType(position: Int): Int =
        if (isTitle(position)) {
            VIEW_TITLE
        } else {
            VIEW_OTHER
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == VIEW_TITLE) {
            SearchResultsSummaryViewHolder(parent, onAppSelected)
        } else {
            SearchResultsViewHolder(parent, onAppSelected)
        }


    override fun onBindViewHolder(holder: BindingViewHolder<AppDetail>, position: Int) =
        holder.bind(getItem(position), searchTerm, getHeader(position), getItemViewType(position))

    override fun getItemCount() = items.size

    fun getItem(position: Int) = items[position]

    @StringRes
    private fun getHeader(position: Int): Int {
        val isTitle = isTitle(position)
        return if (position == 0) {
            if (isTitle) {
                R.string.header_search_title
            } else if(firstSummary){
                firstSummary = false
                R.string.header_search_summary
            } else {
                Resources.ID_NULL
            }
        } else if (!searchTerm.isNullOrEmpty() && !isTitle && firstSummary) {
            firstSummary = false
            R.string.header_search_summary
        } else {
            Resources.ID_NULL
        }
    }

}