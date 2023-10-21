package io.smileyjoe.applist.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.databinding.RowAppDetailsBinding
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.util.Icon
import io.smileyjoe.applist.view.ButtonProgress
import io.smileyjoe.applist.view.ImageSelected

class AppDetailViewHolder : RecyclerView.ViewHolder {

    fun interface ItemSelectedListener {
        fun onSelected(appDetail: AppDetail)
    }

    fun interface Listener {
        fun onUpdate(appDetail: AppDetail)
    }

    var view: RowAppDetailsBinding
    var page: Page
    var saveListener: Listener? = null
    var deleteListener: Listener? = null
    var itemSelectedListener: ItemSelectedListener? = null

    constructor(parent: ViewGroup, page: Page) : this(RowAppDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false), page)

    constructor(view: RowAppDetailsBinding, page: Page) : super(view.root) {
        this.view = view
        this.page = page
    }

    fun bind(app: AppDetail) {
        view.apply {
            textTitle.setText(app.name)
            textPackage.setText(app.appPackage)
            root.setOnClickListener { itemSelectedListener?.onSelected(app) }
            buttonProgress.onEnabledClick { save(app) }
            buttonProgress.onDisabledClick { deleteListener?.onUpdate(app) }
            imageFavourite.selectedListener = View.OnClickListener { favourite(app, true) }
            imageFavourite.deselectedListener = View.OnClickListener { favourite(app, false) }
        }

        Icon.load(view.imageIcon, app)

        updateState(app)
    }

    fun save(app: AppDetail) {
        view.buttonProgress.state = ButtonProgress.State.LOADING
        app.isSaved = true
        saveListener?.onUpdate(app)
    }

    fun favourite(app: AppDetail, isFavourite: Boolean) {
        app.isFavourite = isFavourite
        saveListener?.onUpdate(app)
    }

    fun updateState(app: AppDetail) {
        if (app.isSaved) {
            view.apply {
                buttonProgress.state = ButtonProgress.State.DISABLED
                imageFavourite.visibility = View.VISIBLE
                imageFavourite.state = if (app.isFavourite) ImageSelected.State.SELECTED else ImageSelected.State.DESELECTED
            }
        } else {
            view.apply {
                buttonProgress.state = ButtonProgress.State.ENABLED
                imageFavourite.visibility = View.GONE
                imageFavourite.state = ImageSelected.State.DESELECTED
            }
        }

        if (page == Page.INSTALLED && app.isInstalled) {
            view.textInstalled.visibility = View.VISIBLE
        } else {
            view.textInstalled.visibility = View.GONE
        }
    }
}