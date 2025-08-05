package io.smileyjoe.applist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import io.smileyjoe.applist.databinding.RowSearchResultSummaryBinding
import io.smileyjoe.applist.db.Icon
import io.smileyjoe.library.recycler.HeadingDecoration.Companion.addHeader
import io.smileyjoe.applist.interfaces.OnAppSelected
import io.smileyjoe.applist.objects.AppDetail

fun RowSearchResultSummaryBinding.bind(app: AppDetail) {
    textTitle.text = app.name
    textStatus.text = app.getStatus(textStatus.context)
    Icon.load(
        imageView = imageIcon,
        appDetail = app
    )
}

class SearchResultsSummaryViewHolder : HeaderViewHolder<AppDetail> {

    private val binding: RowSearchResultSummaryBinding
    private val onAppSelected: OnAppSelected

    constructor(
        parent: ViewGroup,
        onAppSelected: OnAppSelected
    ) : this(
        RowSearchResultSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onAppSelected
    )

    constructor(
        view: RowSearchResultSummaryBinding,
        onAppSelected: OnAppSelected
    ) : super(view.root) {
        this.binding = view
        this.onAppSelected = onAppSelected
    }

    override fun bind(app: AppDetail, searchTerm: String?, header: String?) {
        binding.apply {
            root.addHeader(header)
            bind(app)
            root.setOnClickListener { onAppSelected.onSelected(app) }
        }
    }
}