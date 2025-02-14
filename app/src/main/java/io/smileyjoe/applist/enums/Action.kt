package io.smileyjoe.applist.enums

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.smileyjoe.applist.R
import io.smileyjoe.applist.objects.AppDetail

/**
 * Actions that can be performed on an [AppDetail]
 *
 * @param title resource value for the title
 * @param icon resource value for the icon
 */
enum class Action(var id: Int, @StringRes var title: Int, @DrawableRes var icon: Int) {
    UNKNOWN(-1, R.string.error_generic, R.drawable.ic_close) {
        override fun shouldShow(appDetail: AppDetail): Boolean =
            false
    },
    EDIT(0, R.string.action_edit, R.drawable.ic_edit) {
        override fun shouldShow(appDetail: AppDetail) =
            appDetail.isSaved
    },
    SHARE(1, R.string.action_share, R.drawable.ic_share) {
        override fun shouldShow(appDetail: AppDetail) =
            true
    },
    PLAY_STORE(2, R.string.action_play_store, R.drawable.ic_play_store) {
        override fun shouldShow(appDetail: AppDetail) =
            true
    },
    FAVOURITE(3, R.string.action_favourite, R.drawable.ic_favourite) {
        override fun shouldShow(appDetail: AppDetail) =
            !appDetail.isFavourite && appDetail.isSaved
    },
    UNFAVOURITE(4, R.string.action_unfavourite, R.drawable.ic_favourite_outline) {
        override fun shouldShow(appDetail: AppDetail) =
            appDetail.isFavourite
    },
    SAVE(5, R.string.action_save, R.drawable.ic_save) {
        override fun shouldShow(appDetail: AppDetail) =
            !appDetail.isSaved
    },
    DELETE(6, R.string.action_delete, R.drawable.ic_delete) {
        override fun shouldShow(appDetail: AppDetail) =
            appDetail.isSaved
    },
    SETTINGS(7, R.string.action_settings, R.drawable.ic_settings) {
        override fun shouldShow(appDetail: AppDetail): Boolean =
            appDetail.isInstalled
    };

    // tag for the view, so it can be found //
    var tag = "action_$name"

    /**
     * Not all actions are shown all the time, this is overridden
     * in each action to determine if it can show based on the [AppDetail]
     *
     * @param appDetail the details this action is being applies to
     */
    abstract fun shouldShow(appDetail: AppDetail): Boolean

    fun getVisibility(appDetail: AppDetail): Int =
        if (shouldShow(appDetail))
            View.VISIBLE
        else
            View.GONE

    companion object {
        fun getById(id: Int): Action =
            values().firstOrNull {
                it.id == id
            } ?: UNKNOWN
    }
}