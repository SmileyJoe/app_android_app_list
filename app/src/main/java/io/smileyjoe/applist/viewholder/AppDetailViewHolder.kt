package io.smileyjoe.applist.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.databinding.RowAppDetailsBinding
import io.smileyjoe.applist.db.Icon
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.view.ButtonProgress
import io.smileyjoe.library.recycler.ViewHolder

/**
 * View holder for the details row
 */
class AppDetailViewHolder(parent: ViewGroup, var page: Page) :
    ViewHolder<AppDetail, RowAppDetailsBinding>(parent, RowAppDetailsBinding::inflate) {

    fun interface OnItemSelected : ViewHolder.OnItemSelected<AppDetail>

    /**
     * Callback for when the details need to be updated
     * </p>
     * This is used for [saveListener] and [deleteListener]
     */
    fun interface Listener {
        fun onUpdate(appDetail: AppDetail)
    }

    var saveListener: Listener? = null
    var deleteListener: Listener? = null

    /**
     * Populate the row with the provided details
     *
     * @param app app details
     */
    override fun bind(app: AppDetail) {
        binding.apply {
            textTitle.setText(app.name)
            textPackage.setText(app.appPackage)
            textInstalled.isVisible = page == Page.INSTALLED && app.isInstalled
            buttonSave.apply {
                onEnabledClick { save(app) }
                onDisabledClick { deleteListener?.onUpdate(app) }
                isButtonEnabled = !app.isSaved
            }
            imageFavourite.apply {
                selectedListener = View.OnClickListener { favourite(app, true) }
                deselectedListener = View.OnClickListener { favourite(app, false) }
                isVisible = app.isSaved
                isImageSelected = app.isFavourite
            }
        }

        Icon.load(
            imageView = binding.imageIcon,
            appDetail = app
        )
    }

    /**
     * Save the details to firebase
     *
     * @param app details to save
     */
    private fun save(app: AppDetail) {
        binding.buttonSave.state = ButtonProgress.State.LOADING
        app.isSaved = true
        saveListener?.onUpdate(app)
    }

    /**
     * Favourite or unfavourite the app
     *
     * @param app the app to favourite or unfavourite
     * @param isFavourite whether to favourite or not
     */
    private fun favourite(app: AppDetail, isFavourite: Boolean) {
        app.isFavourite = isFavourite
        saveListener?.onUpdate(app)
    }
}