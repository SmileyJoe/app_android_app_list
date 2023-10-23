package io.smileyjoe.applist.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.smileyjoe.applist.R
import io.smileyjoe.applist.activity.SaveAppActivity
import io.smileyjoe.applist.databinding.FragmentAppDetailsBinding
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.util.Icon

class AppDetailsFragment(private val appDetail: AppDetail) : Fragment() {

    companion object{
        const val TAG = "APP_DETAILS"
    }

    private enum class Action(@StringRes var title: Int, @DrawableRes var icon: Int) {
        EDIT(R.string.action_edit, R.drawable.ic_edit) {
            override fun shouldShow(appDetail: AppDetail) =
                appDetail.isSaved
        },
        SHARE(R.string.action_share, R.drawable.ic_share) {
            override fun shouldShow(appDetail: AppDetail) =
                true
        },
        PLAY_STORE(R.string.action_play_store, R.drawable.ic_play_store) {
            override fun shouldShow(appDetail: AppDetail) =
                true
        },
        FAVOURITE(R.string.action_favourite, R.drawable.ic_favourite) {
            override fun shouldShow(appDetail: AppDetail) =
                !appDetail.isFavourite && appDetail.isSaved
        },
        UNFAVOURITE(R.string.action_unfavourite, R.drawable.ic_favourite_outline) {
            override fun shouldShow(appDetail: AppDetail) =
                appDetail.isFavourite
        },
        SAVE(R.string.action_save, R.drawable.ic_save) {
            override fun shouldShow(appDetail: AppDetail) =
                !appDetail.isSaved
        },
        DELETE(R.string.action_delete, R.drawable.ic_delete) {
            override fun shouldShow(appDetail: AppDetail) =
                appDetail.isSaved
        };

        var tag = "action_$name"

        abstract fun shouldShow(appDetail: AppDetail): Boolean
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
        binding.motionMain.setTransitionListener { layout ->
            layout?.let {
                binding.motionContent.progress = it.progress
            }
        }
        populateView()
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_in).also { anim ->
            binding.motionMain.startAnimation(anim)
        }
    }

    private fun populateView() {
        binding.apply {
            textTitle.text = appDetail.name
            textPackage.text = appDetail.appPackage
            textInstalled.isVisible = appDetail.isInstalled
        }

        binding.imageClose.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        if(appDetail.notes.isNullOrEmpty()){
            binding.apply {
                textTitleNotes.isVisible = false
                textNotes.isVisible = false
            }
        } else {
            binding.apply {
                textTitleNotes.isVisible = true
                textNotes.isVisible = true
                textNotes.text = appDetail.notes
            }
        }

        Icon.load(binding.imageIcon, appDetail)
        addActions()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun MotionLayout.setTransitionListener(callback: (layout: MotionLayout?) -> Unit) {
        setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) =
                callback.invoke(p0)

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) =
                callback.invoke(p0)

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) =
                callback.invoke(p0)

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) =
                callback.invoke(p0)
        })
    }
}