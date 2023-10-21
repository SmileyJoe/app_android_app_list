package io.smileyjoe.applist.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.ViewButtonProgressBinding

class ButtonProgress : RelativeLayout {
    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    enum class State(var id: Int, @IdRes var visibleView: Int) {
        ENABLED(0, R.id.button_enabled),
        DISABLED(1, R.id.button_disabled),
        LOADING(2, R.id.progress_loading);

        companion object {
            fun fromId(id: Int): State {
                return values().firstOrNull { state -> state.id == id }
                        ?.let { return it }
                        ?: run { return ENABLED }
            }
        }
    }

    private lateinit var view: ViewButtonProgressBinding
    var state: State = State.ENABLED
        set(value) {
            field = value
            style()
        }

    private fun init(attrs: AttributeSet?) {
        view = ViewButtonProgressBinding.inflate(LayoutInflater.from(context), this, true)
        handleAttributes(attrs)
    }

    private fun handleAttributes(attrs: AttributeSet?) {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.ButtonProgress)?.let { typedArray ->
                view.apply {
                    buttonEnabled.text = typedArray.getString(R.styleable.ButtonProgress_text_enabled)
                    buttonEnabled.icon = ContextCompat.getDrawable(context, typedArray.getResourceId(R.styleable.ButtonProgress_src_enabled, Resources.ID_NULL))
                    buttonDisabled.text = typedArray.getString(R.styleable.ButtonProgress_text_disabled)
                    buttonDisabled.icon = ContextCompat.getDrawable(context, typedArray.getResourceId(R.styleable.ButtonProgress_src_disabled, Resources.ID_NULL))
                }
                state = State.fromId(typedArray.getInt(R.styleable.ButtonProgress_state, State.ENABLED.id))
            }
        }
    }

    fun onEnabledClick(listener: OnClickListener) {
        view.buttonEnabled.setOnClickListener(listener)
    }

    fun onDisabledClick(listener: OnClickListener) {
        view.buttonDisabled.setOnClickListener(listener)
    }

    private fun style() {
        listOf(view.buttonEnabled, view.buttonDisabled, view.progressLoading)
                .forEach { v ->
                    if (v.id == state.visibleView) {
                        v.visibility = View.VISIBLE
                    } else {
                        v.visibility = View.GONE
                    }
                }
    }
}