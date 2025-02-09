package io.smileyjoe.applist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.comparator.AppDetailComparator
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.viewholder.AppDetailViewHolder
import java.util.Collections

/**
 * Adapter for the app details
 */
class AppDetailAdapter(
    private val page: Page,
    items: List<AppDetail> = ArrayList(),
    private val saveListener: AppDetailViewHolder.Listener? = null,
    private val deleteListener: AppDetailViewHolder.Listener? = null,
    private val onItemSelected: AppDetailViewHolder.OnItemSelected? = null,
    private val getFilters: GetFilters? = null
) : RecyclerView.Adapter<AppDetailViewHolder>() {

    fun interface GetFilters {
        fun getFilters(): List<String>
    }

    // the items list is filtered, so we need to keep a record of the original and a record of what //
    // is being used by the adapter for the list //
    private var displayItems: List<AppDetail> = listOf()
        set(value) {
            val filters = getFilters?.getFilters() ?: listOf()
            if (filters.isEmpty()) {
                field = value
            } else {
                field = value.filter { app ->
                    // if the app contains any of the filter tags select it //
                    app.tags?.any(filters::contains)
                        ?: run {
                            false
                        }
                }
            }
        }

    // list of items, sorted by [AppDetailComparator] when set //
    var items: List<AppDetail> = items
        set(value) {
            Collections.sort(value, AppDetailComparator())
            displayItems = value
            field = value
            notifyDataSetChanged()
        }

    // refresh the list, set the display items back to the full list, which will trigger the //
    // filtering //
    fun refresh() {
        displayItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AppDetailViewHolder(parent, page).apply {
            saveListener = this@AppDetailAdapter.saveListener
            deleteListener = this@AppDetailAdapter.deleteListener
            onItemSelected = this@AppDetailAdapter.onItemSelected
        }

    override fun onBindViewHolder(holder: AppDetailViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun getItemCount() = displayItems.size

    fun hasApps() = displayItems.isNotEmpty()

    fun getItem(position: Int) = displayItems[position]
}