package io.smileyjoe.library.tags

import androidx.annotation.DrawableRes

internal enum class GroupState(
    @DrawableRes val filterIcon: Int,
    @DrawableRes val clearIcon: Int,
    val groupVisible: Boolean,
    val detailsVisible: Boolean,
    val clearVisible: Boolean,
    val toggleVisible: Boolean
) {
    EXPANDED(
        filterIcon = R.drawable.ic_contract_filter,
        clearIcon = R.drawable.ic_close_in,
        groupVisible = true,
        detailsVisible = false,
        clearVisible = true,
        toggleVisible = true
    ),
    CONTRACTED(
        filterIcon = R.drawable.ic_filter_contract,
        clearIcon = R.drawable.ic_close_out,
        groupVisible = false,
        detailsVisible = true,
        clearVisible = false,
        toggleVisible = true
    );

    val opposite: GroupState
        get() = when (this) {
            CONTRACTED -> EXPANDED
            EXPANDED -> CONTRACTED
        }
}