package io.smileyjoe.applist.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.ViewButtonActionBinding
import io.smileyjoe.applist.enums.Action
import io.smileyjoe.applist.extensions.TypedArrayExt.getColor

class ButtonAction : ConstraintLayout {

    private class MotionItem(
        private val get: () -> Int,
        private val set: (Int) -> Unit
    ) {
        private var initial: Int = -1

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
    var textColor: Int? = null
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
    private var alphaText: Float? = null
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

    private var visibilityText: Float? = null
        set(value) {
            value?.let {
                binding.textTitle.alpha = it
                titleWidth.update(it)
            }
            field = value
        }

    fun setIconTint(@ColorInt tint: Int) {
        iconTint = tint
    }

    @ColorInt
    fun getIconTint() = iconTint

    fun setBackgroundTint(@ColorInt tint: Int) {
        backgroundTint = tint
    }

    @ColorInt
    fun getBackgroundTint() = backgroundTint

    fun setAlphaText(alpha: Float) {
        alphaText = alpha
    }

    fun getAlphaText() =
        alphaText

    fun setVisibilityText(visibility: Float) {
        visibilityText = visibility
    }

    fun getVisibilityText() =
        visibilityText

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

    private fun handleAttributes(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.ButtonAction).let { typedArray ->
            _action = Action.getById(
                typedArray.getInt(
                    R.styleable.ButtonAction_action,
                    Action.UNKNOWN.id
                )
            )
            textColor = typedArray.getColor(R.styleable.ButtonAction_android_textColor)
            iconTint = typedArray.getColor(R.styleable.ButtonAction_android_iconTint)

            typedArray.recycle()
        }
    }

    private fun getString(@StringRes resId: Int): String =
        context.getString(resId)

}