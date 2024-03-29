package io.smileyjoe.applist.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.smileyjoe.applist.R
import io.smileyjoe.applist.activity.SaveAppActivity
import io.smileyjoe.applist.databinding.FragmentAppDetailsBinding
import io.smileyjoe.applist.enums.Action
import io.smileyjoe.applist.extensions.MotionLayoutExt.setTransitionListener
import io.smileyjoe.applist.extensions.TextViewExt.setDrawable
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.db.Icon

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
        // because of the design, the following structure exists //
        // MotionLayout -> ScrollView -> MotionLayout //
        // this just keeps everything in sync so it all plays nice, as the main motionlayout //
        // changes, it updates to the child motion layout inside the scrollview to be in the //
        // same state //
        binding.motionMain.setTransitionListener { layout ->
            layout?.let {
                binding.motionContent.progress = it.progress
            }
        }
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
        binding.apply {
            textTitle.text = appDetail.name
            textPackage.text = appDetail.appPackage
            textInstalled.isVisible = appDetail.isInstalled
        }

        binding.imageClose.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val hasNotes = appDetail.notes.isNullOrEmpty().not()
        binding.apply {
            textTitleNotes.isVisible = hasNotes
            textNotes.isVisible = hasNotes
            textNotes.text = if (hasNotes) appDetail.notes else null
        }

        Icon.load(binding.imageIcon, appDetail)
        addActions()
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
    private fun addActions() {
        Action.values().forEach { action ->
            // add each action //
            binding.layoutActions.addView(
                // create a textview and set it up with all the details //
                TextView(
                    context,
                    null,
                    0,
                    R.style.Text_ListAction
                ).apply {
                    text = getString(action.title)
                    setDrawable(left = action.icon)
                    isVisible = action.shouldShow(appDetail)
                    setOnClickListener { onActionClicked(action) }
                    tag = action.tag
                })
        }
    }

    /**
     * What to do when an [Action] is clicked
     *
     * @param action the action that was clicked
     * //todo show and hide when actions are clicked should go through [Action.shouldShow]
     */
    private fun onActionClicked(action: Action) {
        when (action) {
            // start the save/edit activity //
            // todo: this might need to be with a result listener to update with the changes //
            Action.EDIT -> startActivity(SaveAppActivity.getIntent(requireContext(), appDetail))
            // open the app in the play store //
            Action.PLAY_STORE -> openUrl()
            // open the native share dialog populate with the app link //
            Action.SHARE -> share()
            // favourite the app and update firebase //
            Action.FAVOURITE -> {
                appDetail.db.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        appDetail.isFavourite = true
                        hide(Action.FAVOURITE)
                        show(Action.UNFAVOURITE)
                    }
                }
            }
            // unfavourite the app and update firebase //
            Action.UNFAVOURITE -> {
                appDetail.db.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        appDetail.isFavourite = false
                        hide(Action.UNFAVOURITE)
                        show(Action.FAVOURITE)
                    }
                }
            }
            // save the app in firebase //
            Action.SAVE -> {
                appDetail.db.save(requireActivity()) { error, ref ->
                    if (error == null) {
                        appDetail.isSaved = true
                        hide(Action.SAVE)
                        show(Action.DELETE, Action.FAVOURITE, Action.EDIT)
                    }
                }
            }
            // remove the app from firebase //
            Action.DELETE -> {
                appDetail.db.delete(requireActivity()) { error, ref ->
                    if (error == null) {
                        appDetail.isFavourite = true
                        hide(Action.DELETE, Action.FAVOURITE, Action.UNFAVOURITE, Action.EDIT)
                        show(Action.SAVE)
                    }
                }
            }
        }
    }

    /**
     * Open the link to the app, this will open the play store assuming this is
     * on a device with the play store installed
     */
    private fun openUrl() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appDetail.playstoreLink)))
    }

    /**
     * Open the native share dialog with the link to the app
     */
    private fun share() {
        var sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, appDetail.playstoreLink)
            type = "text/plain"
        }

        var shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    /**
     * Hide actions
     *
     * @see [show]
     */
    private fun hide(vararg actions: Action) =
        show(show = false, actions = actions)

    /**
     * Show actions, this uses the [Action.tag] to find the view and show it
     *
     * @param actions list of actions to show or hide
     * @param show whether to show or hide the actions
     */
    private fun show(vararg actions: Action, show: Boolean = true) {
        actions.forEach { action ->
            binding.layoutActions.findViewWithTag<View>(action.tag).isVisible = show
        }
    }

    /**
     * Destroy the binding with the fragment is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}