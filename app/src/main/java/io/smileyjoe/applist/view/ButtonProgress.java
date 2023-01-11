package io.smileyjoe.applist.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.stream.Stream;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.databinding.ViewButtonProgressBinding;

public class ButtonProgress extends RelativeLayout {

    public enum State {
        ENABLED(0, R.id.button_enabled),
        DISABLED(1, R.id.button_disabled),
        LOADING(2, R.id.progress_loading);

        private int mId;
        @IdRes
        private int mVisibleView;

        State(int id, @IdRes int visibleView) {
            mId = id;
            mVisibleView = visibleView;
        }

        public int getId() {
            return mId;
        }

        @IdRes
        public int getVisibleView() {
            return mVisibleView;
        }

        public static State fromId(int id) {
            return Arrays.stream(values())
                    .filter(state -> state.getId() == id)
                    .findFirst()
                    .orElse(State.ENABLED);
        }
    }

    private State mState;
    private ViewButtonProgressBinding mView;

    public ButtonProgress(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ButtonProgress(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ButtonProgress(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mView = ViewButtonProgressBinding.inflate(LayoutInflater.from(getContext()), this, true);

        handleAttributes(attrs);
    }

    private void handleAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ButtonProgress);

            if (typedArray != null) {
                mView.buttonEnabled.setText(typedArray.getString(R.styleable.ButtonProgress_text_enabled));
                mView.buttonEnabled.setIcon(ContextCompat.getDrawable(getContext(), typedArray.getResourceId(R.styleable.ButtonProgress_src_enabled, Resources.ID_NULL)));
                mView.buttonDisabled.setText(typedArray.getString(R.styleable.ButtonProgress_text_disabled));
                mView.buttonDisabled.setIcon(ContextCompat.getDrawable(getContext(), typedArray.getResourceId(R.styleable.ButtonProgress_src_disabled, Resources.ID_NULL)));
                setState(State.fromId(typedArray.getInt(R.styleable.ButtonProgress_state, State.ENABLED.getId())));
            }
        }
    }

    public void onEnabledClick(OnClickListener listener) {
        mView.buttonEnabled.setOnClickListener(listener);
    }

    public void onDisabledClick(OnClickListener listener) {
        mView.buttonDisabled.setOnClickListener(listener);
    }

    private void style() {
        Stream.of(mView.buttonEnabled, mView.buttonDisabled, mView.progressLoading)
                .forEach(v -> {
                    if (v.getId() == mState.getVisibleView()) {
                        v.setVisibility(VISIBLE);
                    } else {
                        v.setVisibility(GONE);
                    }
                });
    }

    public void setState(State state) {
        mState = state;
        style();
    }
}
