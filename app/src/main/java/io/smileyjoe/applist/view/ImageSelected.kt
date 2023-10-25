package io.smileyjoe.applist.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import io.smileyjoe.applist.R

class ImageSelected : AppCompatImageView, View.OnClickListener {
    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    enum class State(var id: Int) {
        SELECTED(0),
        DESELECTED(1);

        companion object {
            fun fromId(id: Int): State {
                return values().firstOrNull { state -> state.id == id }
                        ?.let { return it }
                        ?: run { return DESELECTED }
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
            state = if(value) State.SELECTED else State.DESELECTED
        }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.ImageSelected)?.let { typedArray ->
                srcSelected = typedArray.getResourceId(R.styleable.ImageSelected_src_selected, Resources.ID_NULL)
                srcDeselected = typedArray.getResourceId(R.styleable.ImageSelected_src_deselected, Resources.ID_NULL)
                state = State.fromId(typedArray.getInt(R.styleable.ImageSelected_image_selected_state, State.DESELECTED.id))
            }
        }

        setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        state = if (state == State.SELECTED) State.DESELECTED else State.SELECTED

        when (state) {
            State.SELECTED -> selectedListener?.onClick(this)
            State.DESELECTED -> deselectedListener?.onClick(this)
        }
    }
}