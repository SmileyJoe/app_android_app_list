package io.smileyjoe.applist.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.databinding.ViewButtonProgressBinding;

/**
 * Created by cody on 2018/07/04.
 */

public class ButtonProgress extends RelativeLayout {

    public enum State {
        ENABLED(0),
        DISABLED(1),
        LOADING(2);

        private int mId;

        State(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
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
                mView.buttonDisabled.setText(typedArray.getString(R.styleable.ButtonProgress_text_disabled));
                setState(State.fromId(typedArray.getInt(R.styleable.ButtonProgress_state, State.ENABLED.getId())));
            }
        }
    }

    public void onEnabledClick(OnClickListener listener){
        mView.buttonEnabled.setOnClickListener(listener);
    }

    public void onDisabledClick(OnClickListener listener){
        mView.buttonDisabled.setOnClickListener(listener);
    }

    private void style() {
        switch (mState) {
            case ENABLED:
                mView.buttonEnabled.setVisibility(VISIBLE);
                mView.buttonDisabled.setVisibility(GONE);
                mView.progressLoading.setVisibility(GONE);
                break;
            case DISABLED:
                mView.buttonEnabled.setVisibility(GONE);
                mView.buttonDisabled.setVisibility(VISIBLE);
                mView.progressLoading.setVisibility(GONE);
                break;
            case LOADING:
                mView.buttonEnabled.setVisibility(GONE);
                mView.buttonDisabled.setVisibility(GONE);
                mView.progressLoading.setVisibility(VISIBLE);
                break;
        }
    }

    public void setState(State state) {
        mState = state;
        style();
    }
}
