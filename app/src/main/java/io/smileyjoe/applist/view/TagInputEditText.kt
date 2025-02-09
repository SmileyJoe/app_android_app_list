package io.smileyjoe.applist.view

import android.content.Context
import android.content.res.Resources
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.annotation.XmlRes
import com.google.android.material.textfield.TextInputEditText
import io.smileyjoe.applist.R
import io.smileyjoe.applist.extensions.Extensions.withNotNull
import io.smileyjoe.applist.textwatcher.TagTextWatcher

/**
 * Text input for adding tags
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

    @XmlRes
    private var chipXml = Resources.ID_NULL

    private val tagTextWatcher by lazy {
        TagTextWatcher(
            context = context,
            chipXml = chipXml
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
            chipXml = getResourceId(R.styleable.TagInputEditText_chipXml, chipXml)
            recycle()
        }

}