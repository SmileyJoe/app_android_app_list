package io.smileyjoe.applist.util

import android.app.Activity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.smileyjoe.applist.BuildConfig
import io.smileyjoe.applist.R
import io.smileyjoe.applist.`object`.User

object Db {

    private val DB_KEY_APP_DETAIL = if (BuildConfig.DEBUG) "app-debug" else "app"

    private fun getReference(activity: Activity): DatabaseReference? {
        User.getCurrent()?.let { user ->
            return FirebaseDatabase.getInstance().reference.child(user.id)
        } ?: run {
            Notify.error(activity, R.string.error_not_signed_in)
            return null
        }
    }

    fun getDetailReference(activity: Activity): DatabaseReference? {
        getReference(activity)?.let { reference ->
            return reference.child(DB_KEY_APP_DETAIL)
        } ?: run {
            return null
        }
    }
}