package io.smileyjoe.applist.enums

import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.google.firebase.database.DataSnapshot
import io.smileyjoe.applist.R
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.util.PackageUtil

enum class Page(var position: Int, @IdRes var id: Int, @StringRes var title: Int) {
    INSTALLED(0, R.id.nav_installed, R.string.fragment_title_installed_apps) {
        override fun getApps(context: Context, dataSnapshot: DataSnapshot): List<AppDetail> {
            return PackageUtil.getInstalledApplications(context.packageManager, getAllApps(dataSnapshot))
        }
    },
    SAVED(1, R.id.nav_saved, R.string.fragment_title_saved_apps) {
        override fun getApps(context: Context, dataSnapshot: DataSnapshot): List<AppDetail> {
            return PackageUtil.checkInstalled(context.packageManager, getAllApps(dataSnapshot))
        }
    },
    FAVOURITE(2, R.id.nav_favourite, R.string.fragment_title_favourite_apps) {
        override fun getApps(context: Context, dataSnapshot: DataSnapshot): List<AppDetail> {
            return PackageUtil.checkInstalled(context.packageManager,
                    getAllApps(dataSnapshot).filter { app -> app.isFavourite })
        }
    };

    fun getTitle(context: Context): String {
        return context.getString(title)
    }

    abstract fun getApps(context: Context, dataSnapshot: DataSnapshot): List<AppDetail>

    fun getAllApps(dataSnapshot: DataSnapshot): List<AppDetail> {
        var apps = ArrayList<AppDetail>()

        dataSnapshot.children.forEach { item ->
            apps.add(AppDetail(item))
        }

        return apps
    }

    companion object {
        @JvmStatic
        fun fromId(@IdRes id: Int): Page {
            return values()
                    .firstOrNull { it.id == id }
                    ?.let { return it }
                    ?: run { return INSTALLED }

        }

        @JvmStatic
        fun fromPosition(position: Int): Page {
            return values()
                    .firstOrNull { it.position == position }
                    ?.let { return it }
                    ?: run { return INSTALLED }
        }
    }
}