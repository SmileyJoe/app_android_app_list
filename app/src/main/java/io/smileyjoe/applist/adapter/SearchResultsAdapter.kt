package io.smileyjoe.applist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.comparator.AppDetailComparator
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.viewholder.SearchResultsViewHolder
import java.util.Collections

class SearchResultsAdapter(
    items: List<AppDetail> = ArrayList()
) : RecyclerView.Adapter<SearchResultsViewHolder>() {

    var items: List<AppDetail> = items
        set(value) {
            Collections.sort(value, AppDetailComparator())
            field = value
            notifyDataSetChanged()
        }

    fun refresh() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SearchResultsViewHolder(parent)

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun getItemCount() = items.size

    fun getItem(position: Int) = items[position]

}