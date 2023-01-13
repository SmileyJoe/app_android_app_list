package io.smileyjoe.applist.`object`

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.smileyjoe.applist.R
import io.smileyjoe.applist.util.Db
import io.smileyjoe.applist.util.Icon
import io.smileyjoe.applist.util.Notify
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
class AppDetail() : Parcelable {

    private companion object : Parceler<AppDetail> {

        const val DB_KEY_NAME: String = "name"
        const val DB_KEY_PACKAGE: String = "package"
        const val DB_KEY_IS_FAVOURITE: String = "is_favourite"

        override fun AppDetail.write(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(appPackage)
            parcel.writeBoolean(isFavourite)
            parcel.writeString(firebaseKey)
            parcel.writeParcelable(Icon.getBitmapFromDrawable(icon), flags)
            parcel.writeBoolean(isSaved)
            parcel.writeBoolean(isInstalled)
        }

        override fun create(parcel: Parcel): AppDetail {
            var app = AppDetail()
            app.name = parcel.readString()
            app.appPackage = parcel.readString()
            app.isFavourite = parcel.readBoolean()
            app.firebaseKey = parcel.readString()
            var icon: Bitmap? = parcel.readParcelable(ClassLoader.getSystemClassLoader()) as Bitmap?
            if (icon != null) {
                app.icon = BitmapDrawable(icon);
            }
            app.isSaved = parcel.readBoolean()
            app.isInstalled = parcel.readBoolean()
            return app
        }
    }

    var icon: Drawable? = null
    var name: String? = null
    var isFavourite: Boolean = false
    var isSaved: Boolean = false
    var playstoreLink: String? = null
    var isInstalled: Boolean = false

    var appPackage: String? = null
        set(value) {
            field = value
            playstoreLink = "http://play.google.com/store/apps/details?id=$value"
        }

    var firebaseKey: String? = null
        set(value) {
            // make it null or set, this caters for empty strings //
            field = if (value.isNullOrEmpty()) null else value
        }

    constructor(dataSnapshot: DataSnapshot) : this() {
        dataSnapshot?.let { data ->
            firebaseKey = data.key
            isSaved = true

            if (data.hasChild(DB_KEY_NAME)) {
                name = data.child(DB_KEY_NAME).getValue(String::class.java)
            }

            if (data.hasChild(DB_KEY_PACKAGE)) {
                appPackage = data.child(DB_KEY_PACKAGE).getValue(String::class.java)
            }

            if (data.hasChild(DB_KEY_IS_FAVOURITE)) {
                data.child(DB_KEY_IS_FAVOURITE).getValue(Boolean::class.java)?.let {
                    isFavourite = it
                }
            }
        }
    }

    fun getFirebaseKey(reference: DatabaseReference): String? {
        if (TextUtils.isEmpty(firebaseKey)) {
            firebaseKey = reference.push().key
        }

        return firebaseKey
    }

    fun save(activity: Activity): Boolean {
        return save(activity, null)
    }

    fun save(activity: Activity, listener: DatabaseReference.CompletionListener?): Boolean {
        Db.getDetailReference(activity)?.let { reference ->
            getFirebaseKey(reference)?.let { key ->
                val data = mapOf(
                        DB_KEY_NAME to name,
                        DB_KEY_PACKAGE to appPackage,
                        DB_KEY_IS_FAVOURITE to isFavourite
                )

                reference.child(key)
                        .updateChildren(data) { error, ref ->
                            if (error == null) {
                                Icon.upload(appPackage, icon)
                            } else {
                                Notify.error(activity, R.string.error_generic)
                            }

                            listener?.onComplete(error, ref)
                        }
            }

            return true
        } ?: run {
            return false
        }
    }

    fun delete(activity: Activity): Boolean {
        return delete(activity, null)
    }

    fun delete(activity: Activity, listener: DatabaseReference.CompletionListener?): Boolean {
        Db.getDetailReference(activity)?.let { reference ->
            firebaseKey?.let { key ->
                reference.child(key)
                        .removeValue { error, ref ->
                            error?.let {
                                isFavourite = false
                                isSaved = false
                            }
                            listener?.onComplete(error, ref)
                        }
            }
            return true
        } ?: run {
            return false
        }
    }

    fun onSavedUpdated(savedApps: List<AppDetail>): Boolean {
        savedApps.firstOrNull { app ->
            appPackage == app.appPackage
        }?.let { app ->
            isSaved = true
            isFavourite = app.isFavourite
            firebaseKey = app.firebaseKey
            return true
        } ?: run {
            isSaved = false
            firebaseKey = null
            return false
        }
    }

    fun onInstalledUpdated(installedApps: List<AppDetail>): Boolean {
        installedApps.firstOrNull { app ->
            appPackage == app.appPackage
        }?.let { app ->
            isInstalled = true
            return true
        } ?: run {
            isInstalled = false
            return false
        }
    }

    override fun toString(): String {
        return "AppDetail(icon=$icon, appPackage=$appPackage, name=$name, firebaseKey=$firebaseKey, isFavourite=$isFavourite, isSaved=$isSaved, playstoreLink='$playstoreLink', isInstalled=$isInstalled)"
    }
}