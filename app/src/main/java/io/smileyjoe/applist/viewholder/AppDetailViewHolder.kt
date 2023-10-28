package io.smileyjoe.applist.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.databinding.RowAppDetailsBinding
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.util.Icon
import io.smileyjoe.applist.view.ButtonProgress

/**
 * View holder for the details row
 */
class AppDetailViewHolder : RecyclerView.ViewHolder {

    /**
     * Callback for when an item is selected
     */
    fun interface OnItemSelected {
        fun onSelected(appDetail: AppDetail)
    }

    /**
     * Callback for when the details need to be updated
     * </p>
     * This is used for [saveListener] and [deleteListener]
     */
    fun interface Listener {
        fun onUpdate(appDetail: AppDetail)
    }

    var binding: RowAppDetailsBinding
    var page: Page
    var saveListener: Listener? = null
    var deleteListener: Listener? = null
    var onItemSelected: OnItemSelected? = null

    constructor(
        parent: ViewGroup,
        page: Page
    ) : this(RowAppDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false), page)

    constructor(view: RowAppDetailsBinding, page: Page) : super(view.root) {
        this.binding = view
        this.page = page
    }

    /**
     * Populate the row with the provided details
     *
     * @param app app details
     */
    fun bind(app: AppDetail) {
        binding.apply {
            textTitle.setText(app.name)
            textPackage.setText(app.appPackage)
            textInstalled.isVisible = page == Page.INSTALLED && app.isInstalled
            root.setOnClickListener { onItemSelected?.onSelected(app) }
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

        Icon.load(binding.imageIcon, app)
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