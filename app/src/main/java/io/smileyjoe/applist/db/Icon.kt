package io.smileyjoe.applist.db

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.smileyjoe.applist.BuildConfig
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.util.FirebaseGlide
import java.io.ByteArrayOutputStream

/**
 * Icons are stored differently, there is no reason to store an icon to a user, so
 * they have their key and reference.
 */
object Icon {

    /**
     * Root key for the icon storage
     */
    private val STORAGE_KEY_APP_ICON = if (BuildConfig.DEBUG) "icon-debug" else "icon"

    /**
     * Get the reference to the icon storage
     *
     * @return reference to the icon storage
     */
    private fun getReference(): StorageReference? {
        return FirebaseStorage.getInstance().reference.child(STORAGE_KEY_APP_ICON)
    }

    /**
     * Get the reference to a specific packages icon
     *
     * @param packageName the name of the package
     * @return reference to the packages icon
     */
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

    /**
     * Upload an icon into firebase storage
     *
     * @param packageName the package name
     * @param icon the icon to save
     */
    fun upload(packageName: String?, icon: Drawable?) {
        if (icon != null) {
            getReference(packageName)?.let { reference ->
                // only upload if there is no icon //
                reference.downloadUrl.addOnFailureListener { e ->
                    getBitmapFromDrawable(icon)?.let { iconBitmap ->
                        val output = ByteArrayOutputStream()
                        iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)

                        reference.putBytes(output.toByteArray())
                    }
                }
            }
        }
    }

    /**
     * Load an icon from firebase into a view
     *
     * @param imageView the view to put the icon into
     * @param appDetail the app whose icon is needed
     * @see [FirebaseGlide]
     */
    fun load(imageView: ImageView, appDetail: AppDetail, onComplete: ((ImageView) -> Unit)?=null) {
        // if the icon has already been retrieved from firebase, or from the packagemanager //
        // set it on the imageView //
        if (appDetail.icon != null) {
            imageView.apply {
                visibility = View.VISIBLE
                setImageDrawable(appDetail.icon)
                onComplete?.invoke(imageView)
            }
        } else {
            // if not, get the icon from firebase //
            getReference(appDetail.appPackage)?.let { reference ->
                reference.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(imageView.context)
                        .load(reference)
                        .listener(object : RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                onComplete?.invoke(imageView)
                                return false
                            }
                        })
                        .into(imageView)

                    imageView.visibility = View.VISIBLE
                }.addOnFailureListener {
                    // if there is no icon, hide the view //
                    imageView.visibility = View.GONE
                }
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
            var bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            var canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } else {
            return null
        }
    }
}