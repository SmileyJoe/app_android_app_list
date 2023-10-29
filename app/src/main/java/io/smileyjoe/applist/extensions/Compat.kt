package io.smileyjoe.applist.extensions

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import io.smileyjoe.applist.extensions.Compat.getInstalledPackagesCompat
import io.smileyjoe.applist.extensions.Compat.getParcelableCompat
import io.smileyjoe.applist.extensions.Compat.getSerializableCompat

/**
 * Compat extensions
 * </p>
 * These are kept here so that it is easy to find and remove once we move on
 * </p>
 * - [getParcelableCompat]
 * - [getSerializableCompat]
 * - [getInstalledPackagesCompat]
 */
object Compat {

    /**
     * @see [Bundle.getParcelable]
     */
    fun <T : Parcelable> Bundle.getParcelableCompat(tag: String, clazz: Class<T>): T? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getParcelable(tag, clazz)
        } else {
            return getParcelable(tag)
        }
    }

    /**
     * @see [Bundle.getSerializable]
     */
    fun <T : java.io.Serializable> Bundle.getSerializableCompat(tag: String, clazz: Class<T>): T {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getSerializable(tag, clazz)!!
        } else {
            return getSerializable(tag)!! as T
        }
    }

    /**
     * @see [PackageManager.getInstalledPackages]
     */
    fun PackageManager.getInstalledPackagesCompat(): List<PackageInfo> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            return getInstalledPackages(0)
        }
    }

}