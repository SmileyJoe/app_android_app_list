package io.smileyjoe.applist.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import io.smileyjoe.applist.databinding.RowAppDetailsBinding
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.library.recycler.ViewHolder

abstract class HeaderViewHolder<T, U:ViewBinding>(parent: ViewGroup, bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> U) :
    ViewHolder<AppDetail, U>(parent, bindingInflater) {
    val context = parent.context

    abstract fun bind(app: T, searchTerm: String?, header: String?)
}