package io.smileyjoe.applist.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import io.smileyjoe.applist.extensions.Compat.getInstalledPackagesCompat
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.util.PackageUtil.checkInstalled

/**
 * Helper functions for the [PackageManager]
 *
 * todo: [checkInstalled] could be done better, updateSaved or something
 */
object PackageUtil {

    /**
     * Get all installed applications as a list of [AppDetail]
     *
     * @param packageManager
     * @return list of installed applications
     */
    private fun getInstalledApplications(packageManager: PackageManager): List<AppDetail> {
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

    /**
     * If the app has been saved there can be extra information, favourite, notes etc,
     * so update any of the installed apps with saved app data.
     *
     * @param packageManager
     * @param savedApps list of saved apps in firebase
     * @return a list of all installed apps, updated with their saved data
     */
    fun getInstalledApplications(
        packageManager: PackageManager,
        savedApps: List<AppDetail>
    ): List<AppDetail> {
        var installedApps = getInstalledApplications(packageManager)
        // cycle the installed apps //
        installedApps.forEach { installedApp ->
            with(installedApp) {
                // check if any saved app package matches the installed app package //
                savedApps.firstOrNull { app ->
                    appPackage == app.appPackage
                }?.let { app ->
                    // if so, update the details //
                    isSaved = true
                    isFavourite = app.isFavourite
                    firebaseKey = app.firebaseKey
                    notes = app.notes
                } ?: run {
                    // if not, set the defaults //
                    isSaved = false
                    firebaseKey = null
                    notes = null
                }
            }
        }
        return installedApps
    }

    /**
     * Check if any of the saved apps are installed and update them
     *
     * @param packageManager
     * @param savedApps list of saved apps in Firebase
     * @return a list off all saved apps updated with their install status
     *
     * todo: Check if setting the icon is needed, now that icons are saved in firebase this might be legacy
     */
    fun checkInstalled(
        packageManager: PackageManager,
        savedApps: List<AppDetail>
    ): List<AppDetail> {
        var installedApps = getInstalledApplications(packageManager)

        // cycle the saved apps //
        savedApps.forEach { savedApp ->
            with(savedApp) {
                // look for a matching installed app //
                installedApps.firstOrNull { app ->
                    appPackage == app.appPackage
                }?.let { app ->
                    // if found, set the saved app to installed //
                    isInstalled = true
                } ?: run {
                    // if not, set it to not installed //
                    isInstalled = false
                }
            }

            savedApp.icon = getIcon(packageManager, savedApp.appPackage)
        }

        return savedApps
    }

    /**
     * Get the icon from the package manager
     *
     * @param packageManager
     * @param appPackage the package name
     * @return the icon for the installed app
     */
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