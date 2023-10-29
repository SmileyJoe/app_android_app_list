package io.smileyjoe.applist.view

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.extensions.TypedArrayExt.getResId

/**
 * [AppCompatImageView] that changes icon based on it's state
 * </p>
 * Changes state when the user clicks it, this is used for things like favourite/unfavourite
 */
class ImageSelected : AppCompatImageView, View.OnClickListener {
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

    /**
     * The states the view can be in
     *
     * @param id id that matches the attrs.xml for setup in xml
     */
    enum class State(var id: Int) {
        SELECTED(0),
        DESELECTED(1);

        companion object {
            /**
             * Get the state from the id
             *
             * @param id
             * @return the state, or [DESELECTED] if there is no matching id
             */
            private fun from(id: Int): State =
                values().firstOrNull { state -> state.id == id }
                    ?.let { return it }
                    ?: run { return DESELECTED }

            /**
             * Get the state from the typedArray
             *
             * @param typedArray details setup in xml
             * @return the state, or [DESELECTED] if not set or unknown
             */
            fun from(typedArray: TypedArray): State {
                if (typedArray.hasValue(R.styleable.ImageSelected_image_selected_state)) {
                    return from(
                        typedArray.getInt(
                            R.styleable.ImageSelected_image_selected_state,
                            DESELECTED.id
                        )
                    )
                } else {
                    return DESELECTED
                }
            }
        }
    }

    @DrawableRes
    private var srcSelected: Int = Resources.ID_NULL

    @DrawableRes
    private var srcDeselected: Int = Resources.ID_NULL
    var selectedListener: OnClickListener? = null
    var deselectedListener: OnClickListener? = null

    var state: State = State.DESELECTED
        set(value) {
            field = value
            when (value) {
                State.SELECTED -> setImageResource(srcSelected)
                State.DESELECTED -> setImageResource(srcDeselected)
            }
        }

    var isImageSelected: Boolean
        get() = state == State.SELECTED
        set(value) {
            state = if (value) State.SELECTED else State.DESELECTED
        }

    /**
     * Setup the view from the attributes set in xml
     *
     * @param attrs details set in xml
     */
    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.ImageSelected)?.let { typedArray ->
                srcSelected = typedArray.getResId(R.styleable.ImageSelected_src_selected)
                srcDeselected = typedArray.getResId(R.styleable.ImageSelected_src_deselected)
                state = State.from(typedArray)
                typedArray.recycle()
            }
        }

        setOnClickListener(this)
    }

    /**
     * Toggle the state and call the appropriate callback when the user clicks the view
     *
     * @param view this view
     */
    override fun onClick(view: View?) {
        // this feels counter intuitive, however, the user is clicking the view //
        // to activate the next state, not the current state, so we toggle the state //
        // then call the listener. Eg, the icon is deselected, meaning the item is not //
        // favourited, the user clicks the icon, they want to favourite the view, which //
        // would be the selected callback //
        state = if (state == State.SELECTED) State.DESELECTED else State.SELECTED

        when (state) {
            State.SELECTED -> selectedListener?.onClick(this)
            State.DESELECTED -> deselectedListener?.onClick(this)
        }
    }
}