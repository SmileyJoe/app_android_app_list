package io.smileyjoe.applist.viewholder

import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView

abstract class HeaderViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(app: T, searchTerm: String?, @StringRes header: Int?)
}