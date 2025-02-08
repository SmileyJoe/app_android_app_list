package io.smileyjoe.applist.view

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import io.smileyjoe.applist.R
import io.smileyjoe.applist.extensions.Extensions.withNotNull
import io.smileyjoe.applist.extensions.TypedArrayExt.getColor
import io.smileyjoe.applist.extensions.TypedArrayExt.getDimensionPixelOffset
import io.smileyjoe.applist.span.ChipSpan
import io.smileyjoe.applist.textwatcher.TagTextWatcher

/**
 * Text input for adding tags
 *
 * Styling:
 * - `chipTint`
 * - `chipPaddingTop`
 * - `chipPaddingBottom`
 * - `chipTextColor`
 * - `chipRemoveIconTint`
 *
 * @see ChipSpan
 * @see ChipSpan.Theme for defaults
 */
class TagInputEditText : TextInputEditText {

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

    private var _chipTheme: ChipSpan.Theme? = null
    private val chipTheme
        get() = _chipTheme ?: ChipSpan.Theme(
            context = context
        )
    private val tagTextWatcher by lazy {
        TagTextWatcher(
            context = context,
            chipTheme = chipTheme
        )
    }

    /**
     * Add extra line spacing to the padding of each chip into account
     */
    private val lineSpacing: Float
        get() = (chipTheme.paddingTop * 2 + chipTheme.paddingBottom).toFloat()

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
        movementMethod = LinkMovementMethod.getInstance()
        handleAttributes(attrs)
        setLineSpacing(lineSpacing, 1f)
        addTextChangedListener(tagTextWatcher)
    }

    private fun handleAttributes(attrs: AttributeSet?) =
        with(context.obtainStyledAttributes(attrs, R.styleable.TagInputEditText)) {
            _chipTheme = ChipSpan.Theme(
                context = context,
                tint = getColor(R.styleable.TagInputEditText_chipTint),
                paddingTop = getDimensionPixelOffset(R.styleable.TagInputEditText_chipPaddingTop),
                paddingBottom = getDimensionPixelOffset(R.styleable.TagInputEditText_chipPaddingBottom),
                textColor = getColor(R.styleable.TagInputEditText_chipTextColor),
                removeIconTint = getColor(R.styleable.TagInputEditText_chipRemoveIconTint)
            )
            recycle()
        }

}