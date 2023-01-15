package io.smileyjoe.applist.util

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

object BundleUtil {

    fun <T : Parcelable> getParcelable(extras: Bundle, tag: String, clazz: Class<T>): T {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return extras.getParcelable(tag, clazz)!!
        } else {
            return extras.getParcelable(tag)!!
        }
    }

    fun <T : java.io.Serializable> getSerializable(extras: Bundle?, tag: String, clazz: Class<T>): T {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return extras?.getSerializable(tag, clazz)!!
        } else {
            return extras?.getSerializable(tag)!! as T
        }
    }

}