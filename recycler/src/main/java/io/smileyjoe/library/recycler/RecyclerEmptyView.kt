package io.smileyjoe.library.recycler

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import io.smileyjoe.library.recycler.databinding.ViewRecyclerEmptyBinding
import io.smileyjoe.library.utils.LottieRaw
import io.smileyjoe.library.utils.logPaths
import io.smileyjoe.library.utils.theme

class RecyclerEmptyView : ConstraintLayout {

    private val binding: ViewRecyclerEmptyBinding =
        ViewRecyclerEmptyBinding.inflate(LayoutInflater.from(context), this, true)
    private var isLottieThemed: Boolean = false

    var title: String? = null
        set(value) {
            binding.textTitle.setTextAndVisibility(value)
            field = value
        }
    var message: String? = null
        set(value) {
            binding.textMessage.setTextAndVisibility(value)
            field = value
        }
    var retryText: String? = null
        set(value) {
            binding.buttonRetry.setTextAndVisibility(value)
            field = value
        }

    @StringRes
    var titleResId: Int = Resources.ID_NULL
        set(value) {
            value.stringRes { title = it }
            field = value
        }

    @StringRes
    var messageResId: Int = Resources.ID_NULL
        set(value) {
            value.stringRes { message = it }
            field = value
        }

    @StringRes
    var retryTextResId: Int = Resources.ID_NULL
        set(value) {
            value.stringRes { retryText = it }
            field = value
        }

    @DrawableRes
    var iconResId: Int = Resources.ID_NULL
        set(value) {
            if (value != Resources.ID_NULL) {
                binding.image.apply {
                    setImageResource(value)
                    isVisible = true
                }
            }
            field = value
        }

    var lottieImage: LottieRaw? = null
        set(value) {
            value?.let {
                binding.lottie.apply {
                    setAnimation(it.image)
                    theme(it.themes)
                    isVisible = true
                }
            }
            field = value
        }

    var lottieLog: Boolean = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun onRetry(retry: () -> Unit) {
        binding.buttonRetry.setOnClickListener { retry.invoke() }
    }

    private fun TextView.setTextAndVisibility(value: String? = null) {
        text = value
        isVisible = value != null
    }

    private fun Int.stringRes(
        callback: (String) -> Unit
    ) {
        if (this != Resources.ID_NULL) callback(context.getString(this))
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE && binding.lottie.isVisible) {
            binding.lottie.playAnimation()
            if (lottieLog) {
                binding.lottie.logPaths()
            }
        }
    }

}