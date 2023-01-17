package io.smileyjoe.applist.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.smileyjoe.applist.BuildConfig
import io.smileyjoe.applist.`object`.AppDetail
import java.io.ByteArrayOutputStream

object Icon {

    private val STORAGE_KEY_APP_ICON = if (BuildConfig.DEBUG) "icon-debug" else "icon"

    private fun getReference(): StorageReference? {
        return FirebaseStorage.getInstance().reference.child(STORAGE_KEY_APP_ICON)
    }

    private fun getReference(packageName: String?): StorageReference? {
        if (!packageName.isNullOrEmpty()) {
            getReference()?.let { reference ->
                return reference.child("$packageName.png")
            } ?: run {
                return null
            }
        } else {
            return null
        }
    }

    fun upload(packageName: String?, icon: Drawable?) {
        if (icon != null) {
            getReference(packageName)?.let { reference ->
                // only upload if there is no icon //
                reference.downloadUrl.addOnFailureListener { e ->
                    getBitmapFromDrawable(icon)?.let { iconBitmap ->
                        var output = ByteArrayOutputStream()
                        iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                        var data = output.toByteArray()

                        reference.putBytes(data)
                    }
                }
            }
        }
    }

    fun load(imageView: ImageView, appDetail: AppDetail) {
        if (appDetail.icon != null) {
            imageView.apply {
                visibility = View.VISIBLE
                setImageDrawable(appDetail.icon)
            }
        } else {
            getReference(appDetail.appPackage)?.let { reference ->
                reference.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(imageView.context)
                            .load(reference)
                            .into(imageView)

                    imageView.visibility = View.VISIBLE
                }.addOnFailureListener { imageView.visibility = View.GONE }
            }
        }
    }

    /**
     * Taken from https://stackoverflow.com/a/52453231
     *
     * @param drawable
     * @return
     */
    fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable != null) {
            var bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            var canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } else {
            return null
        }
    }
}