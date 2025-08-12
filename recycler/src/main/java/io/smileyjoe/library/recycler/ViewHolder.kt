package io.smileyjoe.library.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ViewHolder<T, U : ViewBinding> : RecyclerView.ViewHolder {

    fun interface OnItemSelected<T> {
        fun onSelected(item: T)
    }

    var binding: U

    constructor(
        parent: ViewGroup,
        bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> U
    ) : this(bindingInflater(LayoutInflater.from(parent.context), parent, false))

    private constructor(binding: U) : super(binding.root) {
        this.binding = binding
    }

    protected abstract fun bind(item: T)

    fun bind(item: T, onclick: OnItemSelected<T>?) {
        binding.root.setOnClickListener { onclick?.onSelected(item) }
        bind(item)
    }

}