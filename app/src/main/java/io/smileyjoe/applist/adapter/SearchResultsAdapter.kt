package io.smileyjoe.applist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
            notifyDataSetChanged()
        }

    var searchTerm: String? = null

    fun refresh() {
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (!searchTerm.isNullOrEmpty()) {
            if (getItem(position).name?.contains(searchTerm!!, ignoreCase = true) == true) {
                VIEW_TITLE
            } else {
                VIEW_OTHER
            }
        } else {
            VIEW_OTHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == VIEW_TITLE) {
            SearchResultsSummaryViewHolder(parent, onAppSelected)
        } else {
            SearchResultsViewHolder(parent, onAppSelected)
        }


    override fun onBindViewHolder(holder: BindingViewHolder<AppDetail>, position: Int) =
        holder.bind(getItem(position), searchTerm)

    override fun getItemCount() = items.size

    fun getItem(position: Int) = items[position]

}