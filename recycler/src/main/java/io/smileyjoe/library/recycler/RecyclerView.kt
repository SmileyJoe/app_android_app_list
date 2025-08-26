package io.smileyjoe.library.recycler

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.smileyjoe.library.recycler.RecyclerViewExt.position
import io.smileyjoe.library.recycler.RecyclerViewExt.smoothScrollTo
import io.smileyjoe.library.recycler.databinding.ViewRecyclerBinding
import io.smileyjoe.library.utils.Extensions.setPadding

open class RecyclerView : ConstraintLayout {

    private val binding: ViewRecyclerBinding
    var isRefreshing: Boolean
    val adapter
        get() = binding.recycler.adapter
    val position
        get() = binding.recycler.position
    var hasFixedSize: Boolean
        set(value) = binding.recycler.setHasFixedSize(value)
        get() = binding.recycler.hasFixedSize()
    var isRefreshEnabled: Boolean = false
        set(value) {
            Log.d("RecyclerThings", "Enabled: $value")
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


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        binding = ViewRecyclerBinding.inflate(LayoutInflater.from(context), this, true)
        binding.recycler.setPadding(bottom = paddingBottom)
        setPadding(bottom = 0)
        isRefreshing = binding.swipeRecycler.isRefreshing
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
        changeView()
    }

    fun onRefresh(onRefresh: SwipeRefreshLayout.OnRefreshListener) {
        binding.swipeRecycler.setOnRefreshListener(onRefresh)
    }

    fun showError() {
        binding.swipeRecycler.isRefreshing = false
        binding.swipeRecycler.visibility = GONE
        binding.frameEmpty.visibility = GONE
        emptyView?.isVisible = false
        binding.frameError.visibility = VISIBLE
    }

    override fun setOnScrollChangeListener(l: OnScrollChangeListener?) {
        binding.recycler.setOnScrollChangeListener(l)
    }

    private fun changeView() =
        if (binding.recycler.adapter == null || binding.recycler.adapter!!.itemCount == 0) {
            binding.apply {
                swipeRecycler.visibility = GONE
                frameEmpty.visibility = VISIBLE
                emptyView?.isVisible = true
                binding.frameError.visibility = GONE
            }
        } else {
            binding.apply {
                swipeRecycler.visibility = VISIBLE
                frameEmpty.visibility = GONE
                emptyView?.isVisible = false
                binding.frameError.visibility = GONE
            }
        }


}