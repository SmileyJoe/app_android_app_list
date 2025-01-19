package io.smileyjoe.applist.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.ViewButtonActionBinding
import io.smileyjoe.applist.enums.Action
import io.smileyjoe.applist.extensions.TypedArrayExt.getColor

/**
 * View that looks like a button that is used for the actions performed on apps.
 *
 * This exists to help with [MotionLayout], there are some restrictions on what motion layout can
 * do, this class opens some of that up.
 *
 * eg, a motion layout requires a get<> and set<> function to exist before it can animate anything,
 * so to animate a buttons text away, the button class would need a setTextVisibility and
 * getTextVisibility function, which it does not have, this has what it needs and updates
 * the elements accordingly
 */
class ButtonAction : ConstraintLayout {

    /**
     * Helper class to modify a value based on the motion value
     *
     * @param get callback to get the value
     * @param set the modified value
     */
    private class MotionItem(
        private val get: () -> Int,
        private val set: (Int) -> Unit
    ) {
        // the initial value, we want to always modify the initial, not the current //
        private var initial: Int = -1

        /**
         * Update the value by the provided [modifier]
         *
         * @param modifier from the motionlayout
         */
        fun update(modifier: Float) {
            when {
                initial <= 0 -> initial = get()
                initial > 0 -> set((initial * modifier).toInt())
            }
        }
    }

    private var _binding: ViewButtonActionBinding? = null
    private val binding
        get() = _binding!!
    private var _action: Action? = null
        set(value) {
            value?.let {
                binding.apply {
                    textTitle.text = getString(it.title)
                    imageIcon.setImageResource(it.icon)
                }
                tag = value.tag

                field = value
            } ?: run {
                isVisible = false
            }
        }
    val action
        get() = _action!!

    @ColorInt
    private var textColor: Int? = null
        set(value) {
            value?.let { binding.textTitle.setTextColor(it) }
            field = value
        }

    @ColorInt
    private var iconTint: Int? = null
        set(value) {
            value?.let { binding.imageIcon.imageTintList = ColorStateList.valueOf(it) }
            field = value
        }

    @ColorInt
    private var backgroundTint: Int? = null
        set(value) {
            value?.let { backgroundTintList = ColorStateList.valueOf(it) }
            field = value
        }

    private var textAlpha: Float? = null
        set(value) {
            value?.let { binding.textTitle.alpha = it }
            field = value
        }

    private val titleWidth = MotionItem(
        get = { binding.textTitle.measuredWidth },
        set = {
            binding.textTitle.width = it
        }
    )

    private var textVisibility: Float? = null
        set(value) {
            value?.let {
                binding.textTitle.alpha = it
                titleWidth.update(it)
            }
            field = value
        }

    constructor(context: Context) : super(context, null, R.attr.buttonActionStyle) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs,
        R.attr.buttonActionStyle
    ) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        _binding = ViewButtonActionBinding.inflate(LayoutInflater.from(context), this, true)
        handleAttributes(attrs)
    }

    private fun handleAttributes(attrs: AttributeSet?) =
        with(context.obtainStyledAttributes(attrs, R.styleable.ButtonAction)) {
            _action = Action.getById(
                getInt(
                    R.styleable.ButtonAction_action,
                    Action.UNKNOWN.id
                )
            )
            textColor = getColor(R.styleable.ButtonAction_android_textColor)
            iconTint = getColor(R.styleable.ButtonAction_android_iconTint)

            recycle()
        }

    private fun getString(@StringRes resId: Int): String =
        context.getString(resId)

    /**
     * Boiler plate code needed for updating values with a [MotionLayout]
     *
     * Each item that can be changed needs a set<> and get<> function, just adding a custom
     * kotlin setter doesn't seem to do the job. On top of that each item has a setter that
     * takes a [ConstraintSet] to make updating things less error prone
     */

    // region TextColor
    @ColorInt
    fun getTextColor() =
        textColor

    fun setTextColor(@ColorInt color: Int) {
        textColor = color
    }

    fun setTextColor(constraintSet: ConstraintSet, @ColorInt color: Int) =
        constraintSet.setColorValue(
            id, "TextColor", color
        )
    // endregion

    // region TextVisibility
    fun getTextVisibility() =
        textVisibility

    fun setTextVisibility(visibility: Float) {
        textVisibility = visibility
    }

    fun setTextVisibility(constraintSet: ConstraintSet, visibility: Float) =
        constraintSet.setFloatValue(
            id, "TextVisibility", visibility
        )
    // endregion

    // region TextAlpha
    fun getTextAlpha() =
        textAlpha

    fun setTextAlpha(alpha: Float) {
        textAlpha = alpha
    }

    fun setTextAlpha(constraintSet: ConstraintSet, alpha: Float) =
        constraintSet.setFloatValue(
            id, "TextAlpha", alpha
        )
    // endregion

    // region BackgroundTint
    @ColorInt
    fun getBackgroundTint() = backgroundTint

    fun setBackgroundTint(@ColorInt tint: Int) {
        backgroundTint = tint
    }

    fun setBackgroundTint(constraintSet: ConstraintSet, @ColorInt color: Int) =
        constraintSet.setColorValue(
            id, "BackgroundTint", color
        )
    // endregion

    // region IconTint
    @ColorInt
    fun getIconTint() = iconTint

    fun setIconTint(@ColorInt tint: Int) {
        iconTint = tint
    }

    fun setIconTint(constraintSet: ConstraintSet, @ColorInt color: Int) =
        constraintSet.setColorValue(
            id, "IconTint", color
        )
    // endregion
}