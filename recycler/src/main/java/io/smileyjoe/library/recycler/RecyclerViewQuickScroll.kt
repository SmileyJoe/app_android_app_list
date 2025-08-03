package io.smileyjoe.library.recycler

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.library.recycler.RecyclerViewExt.position
import io.smileyjoe.library.recycler.RecyclerViewExt.smoothScrollTo
import kotlin.math.absoluteValue

class AlphabetLayoutRecyclerView : RecyclerView {

    private var externalScroll: Boolean = false
    private var alphabetLayoutResId: Int = Resources.ID_NULL
    private val alphabetLayout: AlphabetLayout? by lazy {
        (parent as? ViewGroup)?.findViewById(alphabetLayoutResId) as? AlphabetLayout
    }
    private val alphabetLayoutAdapter by lazy {
        adapter as? AlphabetLayoutAdapter<*>
    }

    enum class State {
        QUICK, STOPPED, SCROLL
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
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
        setOnScrollChangeListener { _, _, _, _, oldScrollY ->
            onScroll(oldScrollY.absoluteValue)
        }
    }

    private fun handleAttributes(attrs: AttributeSet?) =
        with(context.obtainStyledAttributes(attrs, R.styleable.AlphabetLayoutRecyclerView)) {
            alphabetLayoutResId = getResourceId(
                R.styleable.AlphabetLayoutRecyclerView_layout_alphabetLayout,
                Resources.ID_NULL
            )
            recycle()
        }

    private fun getState(distance: Int): State =
        if (distance <= 2 || position == RecyclerViewExt.POSITION_TOP || position == RecyclerViewExt.POSITION_BOTTOM) {
            State.STOPPED
        } else if (distance >= 100) {
            State.QUICK
        } else {
            State.SCROLL
        }

    private fun onScroll(distance: Int) {
        when (getState(distance)) {
            State.QUICK -> alphabetLayout?.show()
            State.STOPPED -> {
                alphabetLayout?.hide(2000)
                externalScroll = false
            }

            State.SCROLL -> {
                // do nothing //
            }
        }

        if (!externalScroll) {
            alphabetLayoutAdapter?.getItem(position)?.section?.let {
                alphabetLayout?.highlightLetter(it)
            }
        }
    }

    fun scrollTo(section: Char) {
        alphabetLayoutAdapter?.getSectionPosition(section)?.let {
            externalScroll = true
            smoothScrollTo(it)
        }
    }

}