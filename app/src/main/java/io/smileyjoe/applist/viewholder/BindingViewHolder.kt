package io.smileyjoe.applist.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BindingViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(app: T, searchTerm: String?)
}