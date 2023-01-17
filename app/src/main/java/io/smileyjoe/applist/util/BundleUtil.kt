package io.smileyjoe.applist.util

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

object BundleUtil {

    fun <T : Parcelable> Bundle.getParcelableCompat(tag: String, clazz: Class<T>): T {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getParcelable(tag, clazz)!!
        } else {
            return getParcelable(tag)!!
        }
    }

    fun <T : java.io.Serializable> Bundle.getSerializableCompat(tag: String, clazz: Class<T>): T {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getSerializable(tag, clazz)!!
        } else {
            return getSerializable(tag)!! as T
        }
    }

}