package io.smileyjoe.applist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.smileyjoe.applist.R
import io.smileyjoe.applist.activity.SaveAppActivity
import io.smileyjoe.applist.databinding.FragmentAppDetailsBinding
import io.smileyjoe.applist.db.Icon
import io.smileyjoe.applist.enums.Action
import io.smileyjoe.applist.extensions.ConstraintSetExt.isVisible
import io.smileyjoe.applist.extensions.ConstraintSetExt.setCardBackgroundColor
import io.smileyjoe.applist.extensions.ConstraintSetExt.setTextColor
import io.smileyjoe.applist.extensions.Extensions.statusBarColor
import io.smileyjoe.applist.extensions.Extensions.withEach
import io.smileyjoe.applist.extensions.MotionLayoutExt.onStateChanged
import io.smileyjoe.applist.extensions.MotionLayoutExt.refresh
import io.smileyjoe.applist.extensions.ViewExt.getColors
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.util.Color
import io.smileyjoe.applist.util.IntentUtil
import io.smileyjoe.applist.view.ButtonAction

/**
 * Fragment to display the app details, as well as all appropriate actions
 *
 * @param appDetail the details to load
 */
class AppDetailsFragment(private val appDetail: AppDetail) : Fragment() {

    companion object {
        // the tag to use when adding the fragment //
        const val TAG = "APP_DETAILS"
    }


    private var _binding: FragmentAppDetailsBinding? = null
    private val binding: FragmentAppDetailsBinding
        get() = _binding!!
    private val actionButtons: List<ButtonAction> by lazy {
        listOf(
            binding.actionDelete,
            binding.actionSave,
            binding.actionFavourite,
            binding.actionUnfavourite,
            binding.actionShare,
            binding.actionPlayStore,
            binding.actionEdit
        )
    }
    private val collapsed by lazy {
        binding.motionMain.getConstraintSet(R.id.collapsed)
    }
    private val expanded by lazy {
        binding.motionMain.getConstraintSet(R.id.expanded)
    }

    // all the buttons that will have a background with the view is expanded //
    private val expandedBackgroundList = listOf(
        R.id.action_save,
        R.id.action_delete,
        R.id.action_favourite,
        R.id.action_unfavourite,
        R.id.action_play_store
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateView()
        // Slide the fragment up, replicating a bottom sheet //
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_in).also { anim ->
            binding.motionMain.startAnimation(anim)
        }
    }

    /**
     * Populate the view
     */
    private fun populateView() {
        val hasNotes = appDetail.notes.isNullOrEmpty().not()
        binding.apply {
            textTitle.text = appDetail.name
            textPackage.text = appDetail.appPackage
            textInstalled.isVisible = appDetail.isInstalled
            textNotes.apply {
                isVisible(expanded, hasNotes)
                isVisible(collapsed, false)
                text = if (hasNotes) appDetail.notes else null
            }
        }

        setActionVisibility()

        Icon.load(binding.imageIcon, appDetail) { imageView ->
            imageView.getColors { updateColors(it) }
        }
        handleActions()
    }

    /**
     * Update the colors of elements based on the app icon
     *
     * @param color details of the main color of the icon
     */
    private fun updateColors(color: Color) {
        actionButtons.withEach {
            val hasBackground = id in expandedBackgroundList
            val iconTint = if (hasBackground) color.title.original else color.main.original

            setIconTint(expanded, iconTint)
            if (hasBackground) {
                setBackgroundTint(expanded, color.main.muted)
                setTextColor(expanded, color.body.original)
            }
        }

        binding.apply {
            frameBackgroundHeader.setCardBackgroundColor(color.main.original)
            frameMain.setCardBackgroundColor(expanded, color.main.dim)
            textTitle.setTextColor(expanded, color.body.original)
            motionMain.refresh()
        }

        binding.motionMain.onStateChanged { expanded ->
            statusBarColor = if (expanded) color.main.dim else android.graphics.Color.TRANSPARENT
        }

    }

    /**
     * Add all the actions.
     * </p>
     * While not all actions are always relevant, they are all always added, and then
     * shown and hidden as needed. This is so they are always in the same order when things change.
     * </p>
     * All action views are tagged with [Action.tag] so they can be found and have the visibility
     * changed
     */
    private fun handleActions() {
        actionButtons.forEach { button ->
            button.apply {
                setOnClickListener { onActionButtonClicked(this) }
            }
        }
    }

    /**
     * What to do when an [Action] is clicked
     *
     * @param button the button that was clicked
     * //todo show and hide when actions are clicked should go through [Action.shouldShow]
     */
    private fun onActionButtonClicked(button: ButtonAction) {
        when (button.action) {
            // start the save/edit activity //
            // todo: this might need to be with a result listener to update with the changes //
            Action.EDIT -> startActivity(SaveAppActivity.getIntent(requireContext(), appDetail))
            // open the app in the play store //
            Action.PLAY_STORE -> startActivity(IntentUtil.open(appDetail.playstoreLink))
            // open the native share dialog populate with the app link //
            Action.SHARE -> startActivity(IntentUtil.share(appDetail.playstoreLink))
            // favourite the app and update firebase //
            Action.FAVOURITE -> {
                appDetail.db.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        appDetail.isFavourite = true
                        hide(binding.actionFavourite)
                        show(binding.actionUnfavourite)
                    }
                }
            }
            // unfavourite the app and update firebase //
            Action.UNFAVOURITE -> {
                appDetail.db.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        appDetail.isFavourite = false
                        hide(binding.actionUnfavourite)
                        show(binding.actionFavourite)
                    }
                }
            }
            // save the app in firebase //
            Action.SAVE -> {
                appDetail.db.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        appDetail.isSaved = true
                        hide(binding.actionSave)
                        show(binding.actionDelete, binding.actionFavourite, binding.actionEdit)
                    }
                }
            }
            // remove the app from firebase //
            Action.DELETE -> {
                appDetail.db.delete(requireActivity()) { error, ref ->
                    if (error == null) {
                        appDetail.isFavourite = true
                        hide(
                            binding.actionDelete,
                            binding.actionFavourite,
                            binding.actionUnfavourite,
                            binding.actionEdit
                        )
                        show(binding.actionSave)
                    }
                }
            }

            Action.UNKNOWN -> {
                // do nothing //
            }
        }
    }

    /**
     * Hide actions
     *
     * @see [show]
     */
    private fun hide(vararg buttons: ButtonAction) =
        setActionVisibility(show = false, buttons = buttons.toList())

    /**
     * Show actions, this uses the [Action.tag] to find the view and show it
     *
     * @param buttons list of buttons to show or hide
     */
    private fun show(vararg buttons: ButtonAction) =
        setActionVisibility(show = true, buttons = buttons.toList())

    /**
     * Update the visibility of the action buttons, the [MotionLayout] controls the visibility
     *
     * @param buttons to update, if this is null, all the buttons will be updated
     * @param show show or hide the buttons, if this is null, the visibility will be set based on the [appDetail] state
     */
    private fun setActionVisibility(buttons: List<ButtonAction>? = null, show: Boolean? = null) {
        // if no buttons are passed in, cycle all the buttons
        (buttons ?: actionButtons).withEach {
            val shouldShow = show ?: action.shouldShow(appDetail)
            isVisible(collapsed, shouldShow)
            isVisible(expanded, shouldShow)
        }
        binding.motionMain.refresh()
    }


    /**
     * Destroy the binding with the fragment is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}