package io.smileyjoe.applist.fragment

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.smileyjoe.applist.R
import io.smileyjoe.applist.activity.SaveAppActivity
import io.smileyjoe.applist.databinding.FragmentBottomSheetDetailsBinding
import io.smileyjoe.applist.extensions.ViewExt.addLayoutListener
import io.smileyjoe.applist.extensions.ViewExt.below
import io.smileyjoe.applist.extensions.ViewExt.updateSize
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.db.Icon

@Deprecated("This has custom animations, it's staying in for learnings sake, " +
        "but it's been replaced with motionlayout in AppDetailsFragment, which is easier to manage")
class AppDetailsBottomSheet(var appDetail: AppDetail) : BottomSheetDialogFragment() {

    private enum class Action(@StringRes var title: Int, @DrawableRes var icon: Int) {
        EDIT(R.string.action_edit, R.drawable.ic_edit) {
            override fun shouldShow(appDetail: AppDetail): Boolean {
                return appDetail.isSaved
            }
        },
        SHARE(R.string.action_share, R.drawable.ic_share) {
            override fun shouldShow(appDetail: AppDetail): Boolean {
                return true
            }
        },
        PLAY_STORE(R.string.action_play_store, R.drawable.ic_play_store) {
            override fun shouldShow(appDetail: AppDetail): Boolean {
                return true
            }
        },
        FAVOURITE(R.string.action_favourite, R.drawable.ic_favourite) {
            override fun shouldShow(appDetail: AppDetail): Boolean {
                return !appDetail.isFavourite && appDetail.isSaved
            }
        },
        UNFAVOURITE(R.string.action_unfavourite, R.drawable.ic_favourite_outline) {
            override fun shouldShow(appDetail: AppDetail): Boolean {
                return appDetail.isFavourite
            }
        },
        SAVE(R.string.action_save, R.drawable.ic_save) {
            override fun shouldShow(appDetail: AppDetail): Boolean {
                return !appDetail.isSaved
            }
        },
        DELETE(R.string.action_delete, R.drawable.ic_delete) {
            override fun shouldShow(appDetail: AppDetail): Boolean {
                return appDetail.isSaved
            }
        };

        var tag = "action_$name"

        abstract fun shouldShow(appDetail: AppDetail): Boolean
    }

    private data class ViewSpecs(val view: View) {
        var x: Float = view.x
        var y: Float = view.y
        var width: Int = view.measuredWidth
        var height: Int = view.measuredHeight
        var textSize: Float =
            when (view) {
                is TextView -> view.textSize
                else -> 0F
            }
    }

    private data class Dimens(val res: Resources) {
        var screenWidth = Resources.getSystem().displayMetrics.widthPixels
        var screenHeight = Resources.getSystem().displayMetrics.heightPixels
        var paddingExtraLarge = res.getDimensionPixelSize(R.dimen.padding_extra_large)
        var paddingMedium = res.getDimensionPixelSize(R.dimen.padding_medium)
        var iconMedium = res.getDimensionPixelSize(R.dimen.icon_medium)
        var textActionBar = res.getDimensionPixelSize(R.dimen.text_actionbar)
    }

    lateinit var binding: FragmentBottomSheetDetailsBinding
    private lateinit var specsContentExpanded: ViewSpecs
    private lateinit var specsDetails: ViewSpecs
    private lateinit var specsIcon: ViewSpecs
    private lateinit var specsHandle: ViewSpecs
    private lateinit var specsTitle: ViewSpecs
    private lateinit var specsScroll: ViewSpecs
    private lateinit var specsTitlePackage: ViewSpecs
    private lateinit var specsTitleInstallStatus: ViewSpecs
    private lateinit var dimens: Dimens

    private fun setupView(bottomSheet: BottomSheetDialog) {
        binding =
            FragmentBottomSheetDetailsBinding.inflate(LayoutInflater.from(context), null, false)
                .apply {
                    textTitle.text = appDetail.name
                    textPackage.text = appDetail.appPackage
                    textInstalled.visibility =
                        if (appDetail.isInstalled) View.VISIBLE else View.GONE
                }

        Icon.load(binding.imageIcon, appDetail)
        addActions()
        bottomSheet.setContentView(binding.root)
        binding.imageSlideDown.alpha = 0F
        binding.imageSlideDown.setOnClickListener { dismiss() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dimens = Dimens(resources)
        var bottomSheet = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        setupView(bottomSheet)
        val behavior = BottomSheetBehavior.from(binding.root.parent as View)

        binding.root.addLayoutListener { layoutListener(behavior) }
        behavior.addBottomSheetCallback(Callback())

        return bottomSheet
    }

    private fun layoutListener(behavior: BottomSheetBehavior<*>): Boolean {
        var height = binding.root.measuredHeight
        var contentExpandedHeight = binding.layoutContentExpanded.measuredHeight

        if (contentExpandedHeight > 0 && height > 0) {
            specsContentExpanded = ViewSpecs(binding.layoutContentExpanded)
            specsDetails = ViewSpecs(binding.layoutDetails)
            specsIcon = ViewSpecs(binding.imageIcon)
            specsHandle = ViewSpecs(binding.dragHandle)
            specsTitle = ViewSpecs(binding.textTitle)
            specsScroll = ViewSpecs(binding.scrollContent)
            specsTitlePackage = ViewSpecs(binding.textTitlePackage)
            specsTitleInstallStatus = ViewSpecs(binding.textTitleInstallStatus)
            binding.layoutContentExpanded.updateSize(height = 0)
            binding.textTitlePackage.updateSize(height = 0)
            binding.textTitleInstallStatus.updateSize(height = 0)
        } else if (height > 0) {
            behavior.peekHeight = height
            binding.viewSpace.updateSize(height = ViewGroup.LayoutParams.MATCH_PARENT)
            return true
        }

        return false
    }

    private fun addActions() {
        Action.values().forEach { action ->
            binding.layoutActions.addView(
                TextView(
                    context,
                    null,
                    0
                ).apply {
                    text = getString(action.title)
                    setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            context,
                            action.icon
                        ), null, null, null
                    )
                    visibility = if (action.shouldShow(appDetail)) View.VISIBLE else View.GONE
                    setOnClickListener { onActionClicked(action) }
                    tag = action.tag
                })
        }
    }

    private fun onActionClicked(action: Action) {
        when (action) {
            Action.EDIT -> startActivity(SaveAppActivity.getIntent(requireContext(), appDetail))
            Action.PLAY_STORE -> openUrl()
            Action.SHARE -> share()
            Action.FAVOURITE -> {
                appDetail.isFavourite = true
                appDetail.db.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        hide(Action.FAVOURITE)
                        show(Action.UNFAVOURITE)
                    }
                }
            }
            Action.UNFAVOURITE -> {
                appDetail.isFavourite = false
                appDetail.db.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        hide(Action.UNFAVOURITE)
                        show(Action.FAVOURITE)
                    }
                }
            }
            Action.SAVE -> {
                appDetail.isSaved = true
                appDetail.db.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        hide(Action.SAVE)
                        show(Action.DELETE, Action.FAVOURITE)
                    }
                }
            }
            Action.DELETE -> {
                appDetail.db.delete(requireActivity()) { error, ref ->
                    if (error == null) {
                        appDetail.isFavourite = true
                        hide(Action.DELETE, Action.FAVOURITE, Action.UNFAVOURITE)
                        show(Action.SAVE)
                    }
                }
            }
        }
    }

    private fun openUrl() {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(appDetail.playstoreLink))
        startActivity(intent)
    }

    private fun share() {
        var sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, appDetail.playstoreLink)
            type = "text/plain"
        }

        var shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun hide(vararg actions: Action) {
        actions.forEach { action ->
            binding.layoutActions.findViewWithTag<View>(action.tag).visibility = View.GONE
        }
    }

    private fun show(vararg actions: Action) {
        actions.forEach { action ->
            binding.layoutActions.findViewWithTag<View>(action.tag).visibility = View.VISIBLE
        }
    }

    private inner class Callback : BottomSheetCallback() {
        override fun onStateChanged(view: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    binding.layoutContentExpanded.visibility = View.VISIBLE
                    binding.imageSlideDown.visibility = View.VISIBLE
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    binding.layoutContentExpanded.visibility = View.GONE
                    binding.imageSlideDown.visibility = View.GONE
                }
                else -> {
                    binding.layoutContentExpanded.visibility = View.VISIBLE
                    binding.imageSlideDown.visibility = View.VISIBLE
                }
            }
        }

        override fun onSlide(view: View, offset: Float) {
            // only animate things when view is bigger then the default peek size
            if (offset >= 0) {
                animateNote(offset)
                var iconSize = animateIcon(offset)
                animateHandle(offset)
                animateTitle(offset)
                animateDetails(offset, iconSize)
                shiftAllViews()
            }
        }

        private fun animateNote(offset: Float) {
            binding.layoutContentExpanded.updateSize(height = specsContentExpanded.height * offset)
        }

        private fun animateTitle(offset: Float) {
            // the icon and the title start at the same place, so move the title the width of the icon
            binding.textTitle.x = specsTitle.x + (binding.imageSlideDown.measuredWidth * offset)
            // move all the scroll content up to cover the drag handle
            binding.scrollContent.y =
                specsScroll.y - (binding.dragHandle.measuredHeight * offset) + dimens.paddingMedium
            // make the text smaller
            binding.textTitle.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                specsTitle.textSize - ((specsTitle.textSize - dimens.textActionBar) * offset)
            )
            // fade the slide down icon
            binding.imageSlideDown.alpha = offset
        }

        private fun animateHandle(offset: Float) {
            binding.dragHandle.y = specsHandle.y - (specsHandle.height * offset)
        }

        private fun animateIcon(offset: Float): Int {
            // (original height * the offset * 2) gives us an icon that is 2x the original height
            // (+ original height) means when the offset is 0 the icon is the original height
            // iconSize is then 3 times the original size when bottom sheet is expanded
            var iconSize = ((specsIcon.height * offset * 2) + specsIcon.height).toInt()
            binding.imageIcon.updateSize(height = iconSize, width = iconSize)

            // we want to move the icon to the center of the screen horizontally
            // (dimens.screenWidth - (specsIcon.x * 2)) start with the width of the screen minus the start position * 2 because we want the center
            // (/ 2) divide it by 2 to get the center
            // (- (iconSize / 2)) subtract half the icon size, this gives us the left most position of the icon
            // (* offset) move the view at the speed of the expanding
            // (+ specsIcon.x) puts the icon back at it's start position with the offset is 0
            var x =
                ((((dimens.screenWidth - (specsIcon.x * 2)) / 2) - (iconSize / 2)) * offset) + specsIcon.x

            if (x >= specsIcon.x) {
                binding.imageIcon.x = x
            }

            return iconSize
        }

        private fun animateDetails(offset: Float, iconSize: Int) {
            // the details are centered by default, so we only need to move them half the new icon height
            var iconBottom = binding.imageIcon.y + (iconSize / 2)
            var y = (iconBottom * offset) + specsDetails.y
            binding.layoutDetails.y = y
            // the view is moving left, so 1 - offset
            binding.layoutDetails.x = specsDetails.x * (1 - offset)
            // seeing details is moving underneath the icon, it can now be full screen width
            binding.layoutDetails.updateSize(width = dimens.screenWidth - binding.layoutDetails.x)
            binding.textTitlePackage.updateSize(height = specsTitlePackage.height * offset)
            binding.textTitleInstallStatus.updateSize(height = specsTitleInstallStatus.height * offset)
        }

        private fun shiftAllViews() {
            // move all the views down as needed so they don't overlap
            binding.layoutContentExpanded.below(binding.layoutDetails)
            binding.dividerContentActions.below(
                binding.layoutContentExpanded,
                dimens.paddingExtraLarge
            )
            binding.layoutActions.below(binding.dividerContentActions)
            binding.viewSpace.below(binding.layoutContent)
        }
    }
}