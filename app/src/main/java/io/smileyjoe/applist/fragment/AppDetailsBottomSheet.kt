package io.smileyjoe.applist.fragment

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import io.smileyjoe.applist.extensions.ViewExt.updateHeight
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.util.Icon


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

    lateinit var binding: FragmentBottomSheetDetailsBinding
    var noteHeight = 0

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
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var bottomSheet = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        setupView(bottomSheet)
        val behavior = BottomSheetBehavior.from(binding.root.parent as View)

        binding.root.addLayoutListener { layoutListener(behavior) }
        behavior.addBottomSheetCallback(Callback())

        return bottomSheet
    }

    private fun layoutListener(behavior: BottomSheetBehavior<*>): Boolean {
        var height = binding.root.measuredHeight
        var noteHeight = binding.textNote.measuredHeight

        if (noteHeight > 0 && height > 0) {
            this@AppDetailsBottomSheet.noteHeight = noteHeight
            binding.textNote.updateHeight(0)
        } else if (height > 0) {
            behavior.peekHeight = height
            binding.viewSpace.updateHeight(ViewGroup.LayoutParams.MATCH_PARENT)
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
                    0,
                    R.style.Text_ListAction
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
                appDetail.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        hide(Action.FAVOURITE)
                        show(Action.UNFAVOURITE)
                    }
                }
            }
            Action.UNFAVOURITE -> {
                appDetail.isFavourite = false
                appDetail.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        hide(Action.UNFAVOURITE)
                        show(Action.FAVOURITE)
                    }
                }
            }
            Action.SAVE -> {
                appDetail.isSaved = true
                appDetail.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        hide(Action.SAVE)
                        show(Action.DELETE, Action.FAVOURITE)
                    }
                }
            }
            Action.DELETE -> {
                appDetail.delete(requireActivity()) { error, ref ->
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
                    binding.textNote.visibility = View.VISIBLE
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    binding.textNote.visibility = View.GONE
                }
                BottomSheetBehavior.STATE_DRAGGING -> {
                    binding.textNote.visibility = View.VISIBLE
                }
            }
        }

        override fun onSlide(view: View, offset: Float) {
            binding.textNote.updateHeight(noteHeight * offset)
        }
    }
}