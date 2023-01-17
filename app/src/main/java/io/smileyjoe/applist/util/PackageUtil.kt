package io.smileyjoe.applist.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import io.smileyjoe.applist.`object`.AppDetail

object PackageUtil {

    fun getInstalledApplications(packageManager: PackageManager): List<AppDetail> {
        var packages: List<PackageInfo>

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packages = packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            packages = packageManager.getInstalledPackages(0)
        }

        return packages
                .filter { packageInfo -> packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                .map { packageInfo ->
                    return@map AppDetail().apply {
                        appPackage = packageInfo.packageName
                        name = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                        isInstalled = true
                        icon = getIcon(packageManager, packageInfo.packageName)
                    }
                }
    }

    fun getInstalledApplications(packageManager: PackageManager, savedApps: List<AppDetail>): List<AppDetail> {
        var installedApps = getInstalledApplications(packageManager)
        installedApps.forEach { installedApp -> installedApp.onSavedUpdated(savedApps) }
        return installedApps
    }

    fun checkInstalled(packageManager: PackageManager, savedApps: List<AppDetail>): List<AppDetail> {
        var installedApps = getInstalledApplications(packageManager)

        savedApps.forEach { savedApp ->
            savedApp.onInstalledUpdated(installedApps)
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