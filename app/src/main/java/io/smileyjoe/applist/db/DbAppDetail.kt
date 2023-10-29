package io.smileyjoe.applist.db

import android.app.Activity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.smileyjoe.applist.R
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.util.Notify

/**
 * Db functions for the AppDetail object
 *
 * @param appDetail the details to work with
 */
class DbAppDetail(
    val appDetail: AppDetail
) {

    companion object {
        // db key names //
        private const val DB_KEY_NAME: String = "name"
        private const val DB_KEY_PACKAGE: String = "package"
        private const val DB_KEY_IS_FAVOURITE: String = "is_favourite"
        private const val DB_KEY_NOTES: String = "notes"

        /**
         * Gets an instance of [AppDetail] from a [DataSnapshot]
         *
         * @param dataSnapshot snapshot from the db
         * @return populated [AppDetail] instance
         */
        fun get(dataSnapshot: DataSnapshot) =
            with(dataSnapshot) {
                AppDetail(
                    firebaseKey = key,
                    isSaved = true,
                    name = child(DB_KEY_NAME).getValue(String::class.java),
                    appPackage = child(DB_KEY_PACKAGE).getValue(String::class.java),
                    isFavourite = child(DB_KEY_IS_FAVOURITE).getValue(Boolean::class.java) ?: false,
                    notes = child(DB_KEY_NOTES).getValue(String::class.java)
                )
            }
    }

    /**
     * Gets the key, if the key is null, it is retrieved from the
     * [reference] and then set on the details object for later use
     *
     * @param reference the db reference
     * @return the [AppDetail.firebaseKey] or null
     */
    private fun getFirebaseKey(reference: DatabaseReference): String? =
        appDetail.firebaseKey
            ?: run {
                appDetail.firebaseKey = reference.push().key
                return appDetail.firebaseKey
            }

    /**
     * Save the app details to firebase
     * </p>
     * This will show an error notification if something goes wrong
     *
     * @param activity current activity, used to show the error
     * @param listener optional callback for when the operation is complete
     * todo: This shouldn't show the error, that should only be done in the completion listener
     */
    fun save(activity: Activity, listener: DatabaseReference.CompletionListener? = null) {
        Db.getDetailReference(activity)?.let { reference ->
            getFirebaseKey(reference)?.let { key ->
                // get a map of the data to save, not everything is saved //
                val data = mapOf(
                    DB_KEY_NAME to appDetail.name,
                    DB_KEY_PACKAGE to appDetail.appPackage,
                    DB_KEY_IS_FAVOURITE to appDetail.isFavourite,
                    DB_KEY_NOTES to appDetail.notes
                )

                // save the details //
                reference.child(key)
                    .updateChildren(data) { error, ref ->
                        // if error is not null, notify the user, else upload the icon //
                        error?.let {
                            Notify.error(activity, R.string.error_generic)
                        } ?: run {
                            Icon.upload(appDetail.appPackage, appDetail.icon)
                        }
                        // callback that the operation is complete //
                        listener?.onComplete(error, ref)
                    }
            }
        }
    }

    /**
     * Delete a record from firebase
     *
     * @param activity current activity, used to show errors
     * @param listener callback when the action is complete
     * todo: This shouldn't show the error, that should only be done in the completion listener
     */
    fun delete(activity: Activity, listener: DatabaseReference.CompletionListener? = null) {
        Db.getDetailReference(activity)?.let { reference ->
            appDetail.firebaseKey?.let { key ->
                reference.child(key)
                    .removeValue { error, ref ->
                        // if there is no error, update the details //
                        error?.let {
                            appDetail.isFavourite = false
                            appDetail.isSaved = false
                        }
                        listener?.onComplete(error, ref)
                    }
            }
        }
    }

}