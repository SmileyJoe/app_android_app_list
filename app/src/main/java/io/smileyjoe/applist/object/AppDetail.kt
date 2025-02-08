package io.smileyjoe.applist.`object`

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import io.smileyjoe.applist.db.DbAppDetail
import io.smileyjoe.applist.db.Icon
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

/**
 * The details of the app
 * </p>
 * Due to having to save the icon as a Drawable, we have to include custom code to
 * handle parcelable
 *
 * @param icon the app icon
 * @param name the name of the app
 * @param isFavourite whether the user has favourited the app or not
 * @param notes any notes the user has added about the app
 * @param isSaved true is the app is saved, false if it's just installed
 * @param isInstalled is the app currently just installed
 * @param appPackage the package name for the app, this is used to make the [playstoreLink]
 * @param firebaseKey the key used for the record in the firebase db
 * @param tags list of tags for the app
 */
@Parcelize
class AppDetail(
    var icon: Drawable? = null,
    var name: String? = null,
    var isFavourite: Boolean = false,
    var notes: String? = null,
    var isSaved: Boolean = false,
    var isInstalled: Boolean = false,
    var appPackage: String? = null,
    firebaseKey: String? = null,
    var tags: List<String>? = null
) : Parcelable {

    // caters for an empty key, if it's empty, make it null //
    var firebaseKey: String? = firebaseKey
        get() = if (field.isNullOrEmpty()) null else field

    // play store link, this is never set as it's generated from the appPackage //
    var playstoreLink: String
        get() = "http://play.google.com/store/apps/details?id=$appPackage"
        private set(value) {}

    // helper to use db functions //
    var db: DbAppDetail
        get() = DbAppDetail(this)
        private set(value) {}

    private companion object : Parceler<AppDetail> {

        /**
         * Write the object to a parcel to be used with [Parcelable]
         */
        override fun AppDetail.write(parcel: Parcel, flags: Int) {
            parcel.apply {
                writeString(name)
                writeString(appPackage)
                writeBoolean(isFavourite)
                writeString(notes)
                writeString(firebaseKey)
                writeParcelable(Icon.getBitmapFromDrawable(icon), flags)
                writeBoolean(isSaved)
                writeBoolean(isInstalled)
                writeStringList(tags)
            }
        }

        /**
         * Create the object from the parcel used with [Parcelable]
         */
        override fun create(parcel: Parcel): AppDetail {
            return AppDetail().apply {
                name = parcel.readString()
                appPackage = parcel.readString()
                isFavourite = parcel.readBoolean()
                notes = parcel.readString()
                firebaseKey = parcel.readString()
                var bitmap: Bitmap? =
                    parcel.readParcelable(ClassLoader.getSystemClassLoader()) as Bitmap?
                if (bitmap != null) {
                    icon = BitmapDrawable(bitmap)
                }
                isSaved = parcel.readBoolean()
                isInstalled = parcel.readBoolean()
                tags = mutableListOf()
                parcel.readStringList(tags!!)
            }
        }
    }

    override fun toString(): String {
        return "AppDetail(" +
                "icon=$icon, " +
                "appPackage=$appPackage, " +
                "name=$name, " +
                "notes=$notes, " +
                "firebaseKey=$firebaseKey, " +
                "isFavourite=$isFavourite, " +
                "isSaved=$isSaved, " +
                "playstoreLink='$playstoreLink', " +
                "isInstalled=$isInstalled, " +
                "tags=$tags" +
                ")"
    }
}