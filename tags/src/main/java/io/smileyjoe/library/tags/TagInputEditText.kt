package io.smileyjoe.library.tags

import android.content.Context
import android.content.res.Resources
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.annotation.XmlRes
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import io.smileyjoe.library.utils.Extensions.withNotNull
import io.smileyjoe.library.utils.TokenizerSpace

/**
 * Text input for adding tags
 */
class TagInputEditText : AppCompatMultiAutoCompleteTextView {

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

    @XmlRes
    private var xmlChip = Resources.ID_NULL

    @LayoutRes
    private var layoutAutocompleteRow = Resources.ID_NULL

    private val tagTextWatcher by lazy {
        TagTextWatcher(
            context = context,
            chipXml = xmlChip
        )
    }

    /**
     * Get a list of all the tags in the input
     */
    var tags: List<String>?
        get() {
            return withNotNull(text) {
                split(" ")
                    .filter { it.isNotBlank() }
                    .map { it.trim() }
            }
        }
        set(value) {
            value?.let {
                setText(
                    it.joinToString(" ")
                        // tags are only processed when the user presses space, so add a space at //
                        // the end when they are added externally //
                        .plus(" ")
                )
            }
        }

    /**
     * The tags that will be used to populate the autocomplete
     */
    var allTags: List<String>? = null
        set(value) {
            value?.let {
                setAdapter(
                    ArrayAdapter(
                        context,
                        layoutAutocompleteRow, it.toMutableList()
                    )
                )
                setTokenizer(TokenizerSpace())
            }
            field = value
        }

    /**
     * Prevent the user from selecting any text or moving the cursor.
     *
     * Once a space is pressed, the word becomes a tag and is no longer editable, so the cursor
     * always needs to be at the end. Additionally clicking on a tag will remove it, so we don't
     * want that to move the cursor as well
     */
    override fun onSelectionChanged(start: Int, end: Int) {
        withNotNull(text) {
            if (start != length || end != length) {
                setSelection(length, length)
                return
            }
        }
        super.onSelectionChanged(start, end)
    }

    private fun init(attrs: AttributeSet?) {
        // this allows the clickable span to work properly
        movementMethod = LinkMovementMethod.getInstance()
        handleAttributes(attrs)
        addTextChangedListener(tagTextWatcher)
    }

    private fun handleAttributes(attrs: AttributeSet?) =
        with(context.obtainStyledAttributes(attrs, R.styleable.TagInputEditText)) {
            xmlChip = getResourceId(R.styleable.TagInputEditText_xml_chip, xmlChip)
            layoutAutocompleteRow = getResourceId(
                R.styleable.TagInputEditText_layout_autocompleteRow,
                layoutAutocompleteRow
            )
            recycle()
        }

}