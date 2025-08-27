package io.smileyjoe.library.recycler

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.core.view.children
import androidx.core.view.isVisible
import io.smileyjoe.library.recycler.databinding.ViewLayoutAlphabetItemBinding
import io.smileyjoe.library.utils.Color.Companion.toColorStateList
import io.smileyjoe.library.utils.Extensions.containsY
import io.smileyjoe.library.utils.Extensions.runOnUiThread
import io.smileyjoe.library.utils.Extensions.setPadding
import io.smileyjoe.library.utils.Language
import io.smileyjoe.library.utils.ThemeUtil.getThemeColor
import io.smileyjoe.library.utils.ViewExt.animate
import io.smileyjoe.library.utils.ViewExt.hide
import io.smileyjoe.library.utils.ViewExt.hitRect
import io.smileyjoe.library.utils.ViewExt.layoutInflater
import io.smileyjoe.library.utils.ViewExt.show
import io.smileyjoe.library.utils.onLayout
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

class AlphabetLayout : LinearLayout {

    private val letterViews: MutableList<Pair<TextView, Rect>> = mutableListOf()
    private val headingView: TextView? by lazy {
        (parent as? ViewGroup)?.findViewById(headingResId) as? TextView
    }
    private var headingResId: Int = Resources.ID_NULL
    private val language = Language.from(context)
    var onShow: (() -> Unit)? = null
    var onHide: (() -> Unit)? = null
    var onSelected: ((Char) -> Unit)? = null
    var itemPaddingStart: Int = 0
    var itemPaddingEnd: Int = 0
    private var timerHide: TimerTask? = null
    private var currentHeading: Char? = null
    var availableLetters: List<Char>? = language.alphabetUpper
        set(value) {
            value?.let {
                letterViews.forEach { pair ->
                    pair.view.isEnabled = it.contains(pair.text.first())
                }
            }
            field = value
        }

    @AnimRes
    var animShow: Int = Resources.ID_NULL

    @AnimRes
    var animHide: Int = Resources.ID_NULL

    constructor(context: Context) : super(context, null, R.attr.alphabetLayoutStyle) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs,
        R.attr.alphabetLayoutStyle
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
        handleAttributes(attrs)
        addLetterViews()
        updateStyle()
        onLayout { onLayoutDrawn() }
    }

    private fun onLayoutDrawn() {
        children.forEach {
            if (it is TextView) {
                letterViews.add(Pair(it, it.hitRect))
            }
        }
        highlightLetter(letterViews.first())
        availableLetters?.first()?.let {
            highlightLetter(it)
        }
    }

    private fun addLetterViews() {
        language.alphabetUpper.forEach {
            val binding = ViewLayoutAlphabetItemBinding.inflate(layoutInflater).apply {
                root.text = it.toString()
                root.isEnabled = availableLetters?.contains(it) ?: false
                setPadding(start = itemPaddingStart, end = itemPaddingEnd)
            }
            addView(binding.root)
        }
    }

    private fun updateStyle() {
        backgroundTintList =
            context.getThemeColor(R.attr.colorSurfaceContainerHigh).toColorStateList()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if(isVisible) {
            parent.requestDisallowInterceptTouchEvent(true)
            when (event?.action) {
                MotionEvent.ACTION_MOVE -> {
                    show()
                    letterViews.firstOrNull {
                        it.hitRect.containsY(event.y.toInt())
                    }?.let {
                        highlightLetter(it)
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    hide()
                    return true
                }

                else -> {
                    return true
                }
            }
        } else {
            parent.requestDisallowInterceptTouchEvent(false)
            return super.dispatchTouchEvent(event)
        }
    }

    private fun highlightLetter(pair: Pair<TextView, Rect>, fromExternal: Boolean = false) {
        val newHeading = pair.text.first()
        if (newHeading != currentHeading) {
            currentHeading = newHeading
            if (!fromExternal) {
                onSelected?.invoke(newHeading)
            }

            if (isVisible && pair.view.isEnabled) headingView?.apply {
                pair.view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                text = currentHeading.toString()
                y = pair.hitRect.exactCenterY() + top - (measuredHeight / 2)
                onLayout {
                    width = measuredHeight
                }
            }
        }
    }

    fun highlightLetter(letter: Char) {
        letterViews.firstOrNull {
            it.text.first() == letter
        }?.let {
            highlightLetter(it, true)
        }
    }

    fun show() {
        cancelTimerHide()
        if (!isVisible) {
            onShow?.invoke()
            headingView?.show(animShow)
            show(animShow)
        }
    }

    fun hide(delay: Long = 0) {
        if (delay > 0) {
            startTimerHide(delay)
        } else if (isVisible) {
            currentHeading = null
            headingView?.hide(animHide)
            hide(animHide)
            onHide?.invoke()
        }
    }

    private fun handleAttributes(attrs: AttributeSet?) =
        with(context.obtainStyledAttributes(attrs, R.styleable.AlphabetLayout)) {
            headingResId = getResourceId(R.styleable.AlphabetLayout_view_header, Resources.ID_NULL)
            itemPaddingStart = getDimensionPixelOffset(
                R.styleable.AlphabetLayout_item_paddingStart,
                itemPaddingStart
            )
            itemPaddingEnd =
                getDimensionPixelOffset(R.styleable.AlphabetLayout_item_paddingEnd, itemPaddingEnd)
            recycle()
        }

    private fun startTimerHide(delay: Long) {
        if (timerHide == null) {
            timerHide = Timer().schedule(delay) {
                context.runOnUiThread { hide() }
            }
        }
    }

    private fun cancelTimerHide() {
        timerHide?.let {
            it.cancel()
            timerHide = null
        }
    }

    private val Pair<TextView, Rect>.view: TextView
        get() = first

    private val Pair<TextView, Rect>.text: CharSequence
        get() = view.text

    private val Pair<TextView, Rect>.hitRect: Rect
        get() = second

}