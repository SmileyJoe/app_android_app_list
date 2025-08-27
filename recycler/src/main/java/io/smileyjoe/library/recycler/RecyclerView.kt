package io.smileyjoe.library.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.smileyjoe.library.recycler.RecyclerViewExt.position
import io.smileyjoe.library.recycler.RecyclerViewExt.smoothScrollTo
import io.smileyjoe.library.recycler.databinding.ViewRecyclerBinding
import io.smileyjoe.library.utils.Extensions.setPadding

open class RecyclerView : ConstraintLayout {

    private val binding =
        ViewRecyclerBinding.inflate(LayoutInflater.from(context), this, true).apply {
            recycler.setPadding(bottom = paddingBottom)
        }
    var isRefreshing
        get() = binding.swipeRecycler.isRefreshing
        set(value) {
            binding.swipeRecycler.isRefreshing = value
        }
    val adapter
        get() = binding.recycler.adapter
    val position
        get() = binding.recycler.position
    var hasFixedSize: Boolean
        set(value) = binding.recycler.setHasFixedSize(value)
        get() = binding.recycler.hasFixedSize()
    var isRefreshEnabled: Boolean = false
        set(value) {
            binding.swipeRecycler.isEnabled = value
            field = value
        }
    var emptyView: View? = null
        set(value) {
            value?.let {
                binding.frameEmpty.addView(it)
                it.isVisible = false
            }
            field = value
        }
    var itemDecoration: ItemDecoration? = null
        set(value) {
            value?.let {
                binding.recycler.addItemDecoration(it)
            }
            field = value
        }


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setPadding(bottom = 0)
    }

    fun smoothScrollTo(position: Int) =
        binding.recycler.smoothScrollTo(position)

    fun setErrorView(view: View) = binding.frameError.addView(view)

    fun setAdapter(adapter: Adapter<*, *>, layoutManager: RecyclerView.LayoutManager) {
        adapter.setItemsChanged {
            changeView()
            binding.swipeRecycler.isRefreshing = false
        }
        binding.recycler.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }
    }

    fun onRefresh(onRefresh: SwipeRefreshLayout.OnRefreshListener) {
        binding.swipeRecycler.setOnRefreshListener(onRefresh)
    }

    fun showError() {
        binding.apply {
            swipeRecycler.isRefreshing = false
            swipeRecycler.isVisible = false
            frameEmpty.isVisible = false
            frameError.isVisible = true
        }
        emptyView?.isVisible = false
    }

    override fun setOnScrollChangeListener(l: OnScrollChangeListener?) {
        binding.recycler.setOnScrollChangeListener(l)
    }

    private fun changeView() {
        val hasItems = adapter?.let {
            it.itemCount > 0
        } ?: false

        binding.apply {
            swipeRecycler.isVisible = hasItems
            frameEmpty.isVisible = !hasItems
            binding.frameError.isVisible = false
        }

        emptyView?.isVisible = !hasItems
    }

}