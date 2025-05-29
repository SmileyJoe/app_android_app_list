package io.smileyjoe.applist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.RowSearchResultsBinding
import io.smileyjoe.applist.db.Icon
import io.smileyjoe.applist.interfaces.OnAppSelected
import io.smileyjoe.applist.objects.AppDetail

class SearchResultsViewHolder : RecyclerView.ViewHolder {

    private val binding: RowSearchResultsBinding
    private val onAppSelected: OnAppSelected

    constructor(
        parent: ViewGroup,
        onAppSelected: OnAppSelected
    ) : this(
        RowSearchResultsBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onAppSelected
    )

    constructor(view: RowSearchResultsBinding, onAppSelected: OnAppSelected) : super(view.root) {
        this.binding = view
        this.onAppSelected = onAppSelected
    }

    fun bind(app: AppDetail) {
        binding.apply {
            textTitle.text = app.name
            textStatus.text = getStatus(app)
            Icon.load(imageIcon, app)
            root.setOnClickListener { onAppSelected.onSelected(app) }
        }
    }

    private fun getStatus(app: AppDetail) =
        listOfNotNull(
            if (app.isInstalled) R.string.text_installed else null,
            if (app.isSaved) R.string.text_saved else null,
            if (app.isFavourite) R.string.text_favourite else null
        ).joinToString(separator = " | ") { binding.textStatus.context.getString(it) }
}