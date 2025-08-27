package io.smileyjoe.applist.viewholder

import android.view.ViewGroup
import io.smileyjoe.applist.databinding.RowSearchResultSummaryBinding
import io.smileyjoe.applist.db.Icon
import io.smileyjoe.applist.interfaces.OnAppSelected
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.library.recycler.SectionDecoration.Companion.addHeader

fun RowSearchResultSummaryBinding.bind(app: AppDetail) {
    textTitle.text = app.name
    textStatus.text = app.getStatus(textStatus.context)
    Icon.load(
        imageView = imageIcon,
        appDetail = app
    )
}

class SearchResultsSummaryViewHolder : HeaderViewHolder<AppDetail, RowSearchResultSummaryBinding> {

    private val onAppSelected: OnAppSelected

    constructor(
        parent: ViewGroup,
        onAppSelected: OnAppSelected
    ) : super(parent, RowSearchResultSummaryBinding::inflate) {
        this.onAppSelected = onAppSelected
    }

    override fun bind(item: AppDetail) =
        bind(item, null, null)

    override fun bind(app: AppDetail, searchTerm: String?, header: String?) {
        binding.apply {
            root.addHeader(header)
            bind(app)
            root.setOnClickListener { onAppSelected.onSelected(app) }
        }
    }
}