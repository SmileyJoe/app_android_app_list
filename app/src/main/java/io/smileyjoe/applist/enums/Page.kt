package io.smileyjoe.applist.enums

import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.google.firebase.database.DataSnapshot
import io.smileyjoe.applist.R
import io.smileyjoe.applist.db.DbAppDetail
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.util.PackageUtil

/**
 * Enum for the pages used in [PagerAdapterAppList]
 *
 * @param position position in the menu
 * @param id resource id in the menu
 * @param title resource id for the title of the page
 */
enum class Page(var position: Int, @IdRes var id: Int, @StringRes var title: Int) {
    INSTALLED(0, R.id.nav_installed, R.string.fragment_title_installed_apps) {
        override fun getApps(context: Context, dataSnapshot: DataSnapshot): List<AppDetail> {
            return PackageUtil.getInstalledApplications(
                context.packageManager,
                getAllSavedApps(dataSnapshot)
            )
        }
    },
    SAVED(1, R.id.nav_saved, R.string.fragment_title_saved_apps) {
        override fun getApps(context: Context, dataSnapshot: DataSnapshot): List<AppDetail> {
            return PackageUtil.checkInstalled(context.packageManager, getAllSavedApps(dataSnapshot))
        }
    },
    FAVOURITE(2, R.id.nav_favourite, R.string.fragment_title_favourite_apps) {
        override fun getApps(context: Context, dataSnapshot: DataSnapshot): List<AppDetail> {
            return PackageUtil.checkInstalled(context.packageManager,
                getAllSavedApps(dataSnapshot).filter { app -> app.isFavourite })
        }
    };

    /**
     * Get the apps for the specific page
     *
     * @param context current context
     * @param dataSnapshot snapshot from the firebase db
     */
    abstract fun getApps(context: Context, dataSnapshot: DataSnapshot): List<AppDetail>

    /**
     * Get the page title
     * </p>
     * Convenience function for context.getString([Page.title])
     *
     * @param context current context
     * @return [title] as a [String]
     */
    fun getTitle(context: Context) = context.getString(title)

    /**
     * Get all the apps from the db
     *
     * @param dataSnapshot snapshot from the firebase db
     * @return all saved apps
     */
    fun getAllSavedApps(dataSnapshot: DataSnapshot): List<AppDetail> {
        var apps = ArrayList<AppDetail>()

        dataSnapshot.children.forEach { item ->
            apps.add(DbAppDetail.get(item))
        }

        return apps
    }

    companion object {
        /**
         * Get the [Page] based on the navigation id
         *
         * @param id the resource id for this page
         * @return corresponding page, or [Page.INSTALLED] if not found
         */
        fun fromId(@IdRes id: Int): Page {
            return values()
                .firstOrNull { it.id == id }
                ?.let { return it }
                ?: run { return INSTALLED }

        }

        /**
         * Get the [Page] based on the position in the [BottomNavigation]
         *
         * @param position position in the menu
         * @return corresponding page, or [Page.INSTALLED] if note found
         */
        fun fromPosition(position: Int): Page {
            return values()
                .firstOrNull { it.position == position }
                ?.let { return it }
                ?: run { return INSTALLED }
        }
    }
}