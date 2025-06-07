package io.smileyjoe.applist.enums

import androidx.annotation.StringRes
import io.smileyjoe.applist.R
import io.smileyjoe.applist.objects.AppDetail

enum class SearchResultsViewType(
    val id: Int,
    @StringRes val titleResId: Int
) {
    UNKNOWN(0, R.string.header_search_details),
    TITLE(1, R.string.header_search_title),
    DETAILS(2, R.string.header_search_details);

    companion object {
        fun fromId(id: Int): SearchResultsViewType =
            SearchResultsViewType.values()
                .firstOrNull { it.id == id }
                ?: UNKNOWN

        fun get(app: AppDetail, searchTerm: String?): SearchResultsViewType =
            searchTerm?.let {
                if (app.name?.contains(it, ignoreCase = true) == true) TITLE else DETAILS
            } ?: TITLE
    }
}