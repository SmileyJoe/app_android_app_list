package io.smileyjoe.applist.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class HeaderViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val context = itemView.context
    abstract fun bind(app: T, searchTerm: String?, header: String?)
}