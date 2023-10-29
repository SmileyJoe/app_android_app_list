package io.smileyjoe.applist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.comparator.AppDetailComparator
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.viewholder.AppDetailViewHolder
import java.util.*

/**
 * Adapter for the app details
 */
class AppDetailAdapter(
    private val page: Page,
    items: List<AppDetail> = ArrayList(),
    private val saveListener: AppDetailViewHolder.Listener? = null,
    private val deleteListener: AppDetailViewHolder.Listener? = null,
    private val onItemSelected: AppDetailViewHolder.OnItemSelected? = null
) :
    RecyclerView.Adapter<AppDetailViewHolder>() {

    // list of items, sorted by [AppDetailComparator] when set //
    var items: List<AppDetail> = items
        set(value) {
            Collections.sort(value, AppDetailComparator())
            field = value
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

    override fun getItemCount() = items.size

    fun hasApps() = items.isNotEmpty()

    fun getItem(position: Int) = items[position]
}