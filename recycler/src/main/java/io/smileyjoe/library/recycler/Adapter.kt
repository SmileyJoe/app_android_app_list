package io.smileyjoe.library.recycler

import androidx.recyclerview.widget.RecyclerView

abstract class Adapter<T, U : ViewHolder<T, *>> : RecyclerView.Adapter<U>() {

    fun interface ItemsChanged {
        fun itemsChanged()
    }

    open var items: List<T>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
            itemsChanged?.itemsChanged()
        }
    private var itemsChanged: ItemsChanged? = null
    private var onClick: ((T) -> Unit)? = null

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    open fun getItem(position: Int): T? {
        return items?.get(position)
    }

    fun onItemClicked(onClick: (T) -> Unit) {
        this.onClick = onClick
    }

    override fun onBindViewHolder(holder: U, position: Int) {
        getItem(position)?.let { holder.bind(it, onClick) }
    }

    fun setItemsChanged(listener: ItemsChanged) {
        itemsChanged = listener
    }
}