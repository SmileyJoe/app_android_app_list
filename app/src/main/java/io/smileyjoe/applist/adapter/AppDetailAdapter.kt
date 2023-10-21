package io.smileyjoe.applist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.comparator.AppDetailComparator
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.viewholder.AppDetailViewHolder
import java.util.*

class AppDetailAdapter : RecyclerView.Adapter<AppDetailViewHolder> {

    var items: List<AppDetail>
        set(value) {
            Collections.sort(value, AppDetailComparator())
            field = value
        }
    var page: Page
    var saveListener: AppDetailViewHolder.Listener? = null
    var deleteListener: AppDetailViewHolder.Listener? = null
    var itemSelectedListener: AppDetailViewHolder.ItemSelectedListener? = null

    constructor(items: List<AppDetail>, page: Page) {
        this.items = items
        this.page = page
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppDetailViewHolder {
        return AppDetailViewHolder(parent, page).apply {
            saveListener = this@AppDetailAdapter.saveListener
            deleteListener = this@AppDetailAdapter.deleteListener
            itemSelectedListener = this@AppDetailAdapter.itemSelectedListener
        }
    }

    override fun onBindViewHolder(holder: AppDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(items: List<AppDetail>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun hasApps(): Boolean {
        return !items.isNullOrEmpty()
    }

    fun getItem(position: Int): AppDetail {
        return items[position]
    }
}