package io.smileyjoe.library.tags

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.annotation.LayoutRes
import androidx.core.view.children
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.smileyjoe.library.utils.Color
import io.smileyjoe.library.utils.Color.Companion.toColorStateList

class TagGroup : ChipGroup {

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
    }

}