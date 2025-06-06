package io.smileyjoe.applist.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.google.android.material.color.MaterialColors
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.RowSearchResultBinding
import io.smileyjoe.applist.decorator.HeadingDecorator.Companion.addHeader
import io.smileyjoe.applist.extensions.StringExt.highlight
import io.smileyjoe.applist.extensions.StringExt.removeBreaks
import io.smileyjoe.applist.extensions.StringExt.summary
import io.smileyjoe.applist.interfaces.OnAppSelected
import io.smileyjoe.applist.objects.AppDetail

class SearchResultsViewHolder : BindingViewHolder<AppDetail> {

    private val binding: RowSearchResultBinding
    private val onItemSelected: OnAppSelected
    private val highlightColor: Int

    constructor(
        parent: ViewGroup,
        onAppSelected: OnAppSelected
    ) : this(
        RowSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onAppSelected
    )

    constructor(
        view: RowSearchResultBinding,
        onAppSelected: OnAppSelected
    ) : super(view.root) {
        binding = view
        onItemSelected = onAppSelected
        highlightColor =
            MaterialColors.getColor(binding.root.context, R.attr.colorPrimary, Color.WHITE)
    }

    override fun bind(app: AppDetail, searchTerm: String?, @StringRes header: Int, viewType: Int) {
        binding.apply {
            root.addHeader(header)
            root.setTag(R.id.tag_view_type, viewType)
            layoutSummary.bind(app)
            layoutTags.apply {
                app.tags?.takeIf { it.isNotEmpty() }?.let {
                    this.searchTerm = searchTerm
                    tags = it
                } ?: run {
                    clear()
                }
            }
            textNotes.apply {
                text = app.notes
                    ?.trimIndent()
                    ?.removeBreaks()
                    ?.summary(searchTerm, 50, true)
                    ?.highlight(searchTerm, highlightColor)
                isVisible = !app.notes.isNullOrEmpty()
            }
            root.setOnClickListener { onItemSelected.onSelected(app) }
        }
    }
}