package io.smileyjoe.applist.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.IdRes
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.ViewButtonProgressBinding
import io.smileyjoe.applist.extensions.TypedArrayExt.getResId

/**
 * A button that has two states and a loader.
 * </p>
 * Click the button, it turns into a loader, instead of showing a dialog loader,
 * action is complete, button changes state.
 */
class ButtonProgress : RelativeLayout {
    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    /**
     * The different states available
     *
     * @param id the state id, matches the id's setup in the attrs file for setting up in xml
     * @param visibleView resource id of the vire that relates to the state
     */
    enum class State(var id: Int, @IdRes var visibleView: Int) {
        ENABLED(0, R.id.button_enabled),
        DISABLED(1, R.id.button_disabled),
        LOADING(2, R.id.progress_loading);

        companion object {
            /**
             * Get the state from the id
             *
             * @param id
             * @return the state, or [ENABLED] if there is no matching id
             */
            private fun from(id: Int): State =
                values().firstOrNull { state -> state.id == id }
                    ?.let { return it }
                    ?: run { return ENABLED }

            /**
             * Get the state from the typedArray
             *
             * @param typedArray details setup in xml
             * @return the state, or [ENABLED] if not set or unknown
             */
            fun from(typedArray: TypedArray): State {
                if (typedArray.hasValue(R.styleable.ButtonProgress_state)) {
                    return from(
                        typedArray.getInt(
                            R.styleable.ButtonProgress_state,
                            ENABLED.id
                        )
                    )
                } else {
                    return ENABLED
                }
            }
        }
    }

    private lateinit var binding: ViewButtonProgressBinding
    var state: State = State.ENABLED
        set(value) {
            field = value
            style()
        }

    var isButtonEnabled: Boolean
        get() = state == State.ENABLED || state == State.LOADING
        set(value) {
            state = if (value) State.ENABLED else State.DISABLED
        }

    /**
     * Setup the view from the attributes set in xml
     *
     * @param attrs details set in xml
     */
    private fun init(attrs: AttributeSet?) {
        binding = ViewButtonProgressBinding.inflate(LayoutInflater.from(context), this, true)
        handleAttributes(attrs)
    }

    /**
     * Setup the view based on the attributes set in the xml
     */
    private fun handleAttributes(attrs: AttributeSet?) {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.ButtonProgress)?.let { typedArray ->
                binding.apply {
                    buttonEnabled.setup(
                        typedArray,
                        R.styleable.ButtonProgress_text_enabled,
                        R.styleable.ButtonProgress_src_enabled
                    )
                    buttonDisabled.setup(
                        typedArray,
                        R.styleable.ButtonProgress_text_disabled,
                        R.styleable.ButtonProgress_src_disabled
                    )
                }
                state = State.from(typedArray)
                typedArray.recycle()
            }
        }
    }

    /**
     * What happens when the user clicks the button in the [State.ENABLED] state
     *
     * @param listener click listener
     */
    fun onEnabledClick(listener: OnClickListener) {
        binding.buttonEnabled.setOnClickListener(listener)
    }

    /**
     * What happens when the user clicks the button in the [State.DISABLED] state
     *
     * @param listener click listener
     */
    fun onDisabledClick(listener: OnClickListener) {
        binding.buttonDisabled.setOnClickListener(listener)
    }

    /**
     * Change what the button looks like based on it's state.
     */
    private fun style() {
        // cycle a list of all the state views //
        listOf(binding.buttonEnabled, binding.buttonDisabled, binding.progressLoading)
            .forEach { v ->
                // if the id matches the view id set in the current state, make it visible //
                // else make it gone //
                v.isVisible = v.id == state.visibleView
            }
    }

    /**
     * Setup the buttons with the details set in the xml attributes
     *
     * @param typedArray
     * @param idText style res id for the button text
     * @param idIcon style res if for the icon on the button
     */
    private fun MaterialButton.setup(
        typedArray: TypedArray,
        @StyleableRes idText: Int,
        @StyleableRes idIcon: Int
    ) {
        apply {
            text = typedArray.getString(idText)
            icon = ContextCompat.getDrawable(
                context,
                typedArray.getResId(idIcon)
            )
        }
    }
}