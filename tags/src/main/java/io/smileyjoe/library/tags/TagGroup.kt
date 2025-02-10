package io.smileyjoe.library.tags

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.smileyjoe.library.utils.Color
import io.smileyjoe.library.utils.Color.Companion.toColorStateList
import io.smileyjoe.library.utils.Extensions.withNotNull

class TagGroup : ChipGroup {

    enum class State {
        EXPANDED, CONTRACTED;

        val opposite: State
            get() = when (this) {
                CONTRACTED -> EXPANDED
                EXPANDED -> CONTRACTED
            }
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
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

    var onSelectedTagsChanged: ((tags: MutableList<String>) -> Unit)? = null

    private var selectedTags: MutableList<String> = mutableListOf()

    @LayoutRes
    private var layoutChip = Resources.ID_NULL

    var state: State = if (isVisible) State.EXPANDED else State.CONTRACTED
        set(value) {
            TransitionManager.beginDelayedTransition(parent as ViewGroup, AutoTransition().apply {
                duration = 150
            })
            when (value) {
                State.CONTRACTED -> {
                    visibility = View.GONE
                    detailsView?.visibility = View.VISIBLE
                    clearView?.visibility = View.GONE
                }

                State.EXPANDED -> {
                    visibility = View.VISIBLE
                    detailsView?.visibility = View.GONE
                    clearView?.visibility = View.VISIBLE
                }
            }
            field = value
        }

    var color: Color? = null
        set(value) {
            value?.let {
                children.forEach {
                    if (it is Chip) {
                        it.updateColors(value)
                    }
                }
            }
            field = value
        }

    var tags: List<String>? = null
        set(value) {
            removeAllViews()
            value?.sortedBy { it }?.forEach { tag ->
                addView(
                    chip.apply {
                        text = tag
                        isChecked = selectedTags.contains(tag)
                        if (onSelectedTagsChanged != null) {
                            setOnCheckedChangeListener(::onTagSelected)
                        }
                    }
                )
            }
            field = value
        }

    var toggleView: ImageView? = null
        set(value) {
            value?.setOnClickListener {
                state = state.opposite
            }
            field = value
        }

    var clearView: ImageView? = null
        set(value) {
            value?.setOnClickListener {
                children.forEach {
                    if (it is Chip) {
                        it.isChecked = false
                    }
                }
            }
            value?.isVisible = when (state) {
                State.CONTRACTED -> false
                State.EXPANDED -> true
            }
            field = value
        }

    var detailsView: TextView? = null
        set(value) {
            detailsViewEmptyText = value?.text.toString()
            field = value
        }

    var detailsViewEmptyText: String? = null

    // inflate the chip used for tags //
    private val chip: Chip
        get() = (LayoutInflater.from(context).inflate(
            layoutChip,
            this,
            false
        ) as Chip).apply {
            updateColors(color)
        }

    private fun init(attrs: AttributeSet?) {
        handleAttributes(attrs)
    }

    private fun handleAttributes(attrs: AttributeSet?) =
        with(context.obtainStyledAttributes(attrs, R.styleable.TagGroup)) {
            layoutChip = getResourceId(R.styleable.TagGroup_layout_chip, layoutChip)
            recycle()
        }

    private fun Chip.updateColors(color: Color?) {
        color?.let {
            chipBackgroundColor = it.main.dim.toColorStateList()
            chipStrokeColor = it.main.original.toColorStateList()
            setTextColor(it.main.original.toColorStateList())
        }
    }

    private fun onTagSelected(chip: CompoundButton, isChecked: Boolean) {
        val text = chip.text.toString()
        if (isChecked) {
            selectedTags.add(text)
        } else {
            selectedTags.remove(text)
        }
        onSelectedTagsChanged?.invoke(selectedTags)
        setDetailsText()
    }

    private fun setDetailsText() {
        withNotNull(detailsView) {
            text = if (selectedTags.isEmpty()) {
                detailsViewEmptyText
            } else {
                selectedTags.sortedBy { it }.joinToString(", ")
            }
        }
    }

}