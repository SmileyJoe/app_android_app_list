package io.smileyjoe.applist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.comparator.AppDetailComparator
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.objects.Filter
import io.smileyjoe.applist.viewholder.AppDetailViewHolder
import io.smileyjoe.library.recycler.Adapter
import io.smileyjoe.library.recycler.SectionAdapter
import io.smileyjoe.library.utils.Extensions.withNotNull
import java.util.Collections

/**
 * Adapter for the app details
 */
class AppDetailAdapter(
    private val page: Page,
    items: List<AppDetail> = ArrayList(),
    private val saveListener: AppDetailViewHolder.Listener? = null,
    private val deleteListener: AppDetailViewHolder.Listener? = null,
    private val getFilter: GetFilter? = null
) : Adapter<AppDetail, AppDetailViewHolder>(), SectionAdapter<AppDetail> {

    fun interface GetFilter {
        fun getFilter(): Filter
    }

    override val adapter: RecyclerView.Adapter<*> = this

    override var externalScroll: Boolean = false

    // the items list is filtered, so we need to keep a record of the original and a record of what //
    // is being used by the adapter for the list //
    var allItems: List<AppDetail>? = listOf()

    // list of items, sorted by [AppDetailComparator] when set //
    override var items: List<AppDetail>?
        get() = super.items
        set(value) {
            Collections.sort(value, AppDetailComparator())
            withNotNull(getFilter?.getFilter()) {
                if (tags.isEmpty()) {
                    super.items = value
                } else {
                    super.items = value?.filter { app ->
                        // if the app contains any of the filter tags select it //
                        app.tags?.any(tags::contains)
                            ?: run {
                                false
                            }
                    }
                }
            }
            allItems = value
        }

    init {
        this.items = items
    }

    // refresh the list, set the display items back to the full list, which will trigger the //
    // filtering //
    fun refresh() {
        items = allItems
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): AppDetail? {
        return if (items?.indices?.contains(position) == true) {
            items!![position]
        } else {
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AppDetailViewHolder(parent, page).apply {
            saveListener = this@AppDetailAdapter.saveListener
            deleteListener = this@AppDetailAdapter.deleteListener
        }

    fun hasApps() = items?.isNotEmpty() ?: false
}