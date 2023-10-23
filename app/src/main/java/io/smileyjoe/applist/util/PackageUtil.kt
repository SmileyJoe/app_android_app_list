package io.smileyjoe.applist.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import io.smileyjoe.applist.extensions.Compat.getInstalledPackagesCompat
import io.smileyjoe.applist.`object`.AppDetail

object PackageUtil {

    fun getInstalledApplications(packageManager: PackageManager): List<AppDetail> {
        return packageManager.getInstalledPackagesCompat()
            .filter { packageInfo -> packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .map { packageInfo ->
                return@map AppDetail(
                    appPackage = packageInfo.packageName,
                    name = packageInfo.applicationInfo.loadLabel(packageManager).toString(),
                    isInstalled = true,
                    icon = getIcon(packageManager, packageInfo.packageName)
                )
            }
    }

    fun getInstalledApplications(
        packageManager: PackageManager,
        savedApps: List<AppDetail>
    ): List<AppDetail> {
        var installedApps = getInstalledApplications(packageManager)
        installedApps.forEach { installedApp ->
            with(installedApp) {
                savedApps.firstOrNull { app ->
                    appPackage == app.appPackage
                }?.let { app ->
                    isSaved = true
                    isFavourite = app.isFavourite
                    firebaseKey = app.firebaseKey
                    notes = app.notes
                } ?: run {
                    isSaved = false
                    firebaseKey = null
                    notes = null
                }
            }
        }
        return installedApps
    }

    fun checkInstalled(
        packageManager: PackageManager,
        savedApps: List<AppDetail>
    ): List<AppDetail> {
        var installedApps = getInstalledApplications(packageManager)

        savedApps.forEach { savedApp ->
            with(savedApp) {
                installedApps.firstOrNull { app ->
                    appPackage == app.appPackage
                }?.let { app ->
                    isInstalled = true
                } ?: run {
                    isInstalled = false
                }
            }
            savedApp.icon = getIcon(packageManager, savedApp.appPackage)
        }

        return savedApps
    }

    private fun getIcon(packageManager: PackageManager, appPackage: String?): Drawable? {
        if (!appPackage.isNullOrEmpty()) {
            try {
                return packageManager.getApplicationIcon(appPackage)
            } catch (e: PackageManager.NameNotFoundException) {
                return null
            }
        }
        return null
    }

}