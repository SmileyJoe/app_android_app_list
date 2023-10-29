package io.smileyjoe.applist.db

import android.app.Activity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.smileyjoe.applist.BuildConfig
import io.smileyjoe.applist.R
import io.smileyjoe.applist.`object`.User
import io.smileyjoe.applist.util.Notify

/**
 * Base db operations
 */
object Db {

    /**
     * Root key used for the app details
     */
    private val DB_KEY_APP_DETAIL = if (BuildConfig.DEBUG) "app-debug" else "app"

    /**
     * Get the reference for the user
     * </p>
     * Db structure is <user_id>/DB_KEY_/items
     *
     * @param activity current activity used to show error
     * todo: Should be a callback, shouldn't show the error
     * @return [DatabaseReference] for the users node
     */
    private fun getReference(activity: Activity): DatabaseReference? {
        User.current?.let { user ->
            return FirebaseDatabase.getInstance().reference.child(user.id)
        } ?: run {
            Notify.error(activity, R.string.error_not_signed_in)
            return null
        }
    }

    /**
     * Get the reference for the user app details node
     *
     * @param activity current activity used to show error
     * todo: Should be a callback, shouldn't show the error
     * @return [DatabaseReference] for the user app details node
     */
    fun getDetailReference(activity: Activity): DatabaseReference? {
        getReference(activity)?.let { reference ->
            return reference.child(DB_KEY_APP_DETAIL)
        } ?: run {
            return null
        }
    }
}