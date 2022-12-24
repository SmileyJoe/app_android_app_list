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

import io.smileyjoe.applist.R;

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
            for (State state : values()) {
                if (state.getId() == id) {
                    return state;
                }
            }

            return State.ENABLED;
        }
    }

    private TextView mTextTitle;
    private String mTextEnabled;
    private String mTextDisabled;
    private int mTextColorResEnabled;
    private int mTextColorResDisabled;
    private int mColorResEnabled;
    private int mColorResDisabled;
    private int mColorResLoading;
    private State mState;
    private ProgressBar mProgressLoading;

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
        LayoutInflater.from(getContext()).inflate(R.layout.view_button_progress, this, true);

        mTextTitle = (TextView) findViewById(R.id.text_title);
        mProgressLoading = (ProgressBar) findViewById(R.id.progress_loading);

        handleAttributes(attrs);
    }

    private void handleAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ButtonProgress);

            if (typedArray != null) {
                setTextEnabled(typedArray.getString(R.styleable.ButtonProgress_text_enabled));
                setTextDisabled(typedArray.getString(R.styleable.ButtonProgress_text_disabled));
                setTextColorResEnabled(typedArray.getColor(R.styleable.ButtonProgress_text_color_enabled, 0));
                setTextColorResDisabled(typedArray.getColor(R.styleable.ButtonProgress_text_color_disabled, 0));
                setColorResEnabled(typedArray.getColor(R.styleable.ButtonProgress_background_enabled, 0));
                setColorResDisabled(typedArray.getColor(R.styleable.ButtonProgress_background_disabled, 0));
                setColorResLoading(typedArray.getColor(R.styleable.ButtonProgress_background_loading, 0));
                setState(State.fromId(typedArray.getInt(R.styleable.ButtonProgress_state, State.ENABLED.getId())));
            }
        }
    }

    private void style() {
        switch (mState) {
            case ENABLED:
                mProgressLoading.setVisibility(GONE);
                mTextTitle.setVisibility(VISIBLE);
                mTextTitle.setText(mTextEnabled);
                mTextTitle.setTextColor(mTextColorResEnabled);
                setBackgroundColor(mColorResEnabled);
                break;
            case DISABLED:
                mProgressLoading.setVisibility(GONE);
                mTextTitle.setVisibility(VISIBLE);
                mTextTitle.setText(mTextDisabled);
                mTextTitle.setTextColor(mTextColorResDisabled);
                setBackgroundColor(mColorResDisabled);
                break;
            case LOADING:
                mTextTitle.setText(mTextEnabled);
                mProgressLoading.setVisibility(VISIBLE);
                mTextTitle.setVisibility(INVISIBLE);
                setBackgroundColor(mColorResLoading);
                break;
        }
    }

    public void setState(State state) {
        mState = state;
        style();
    }

    public State getState() {
        return mState;
    }

    public void setTextEnabled(String textEnabled) {
        mTextEnabled = textEnabled;
    }

    public void setTextDisabled(String textDisabled) {
        mTextDisabled = textDisabled;
    }

    public void setTextColorResEnabled(int textColorResEnabled) {
        mTextColorResEnabled = textColorResEnabled;
    }

    public void setTextColorResDisabled(int textColorResDisabled) {
        mTextColorResDisabled = textColorResDisabled;
    }

    public void setColorResEnabled(int colorResEnabled) {
        mColorResEnabled = colorResEnabled;
    }

    public void setColorResDisabled(int colorResDisabled) {
        mColorResDisabled = colorResDisabled;
    }

    public void setColorResLoading(int colorResLoading) {
        mColorResLoading = colorResLoading;
    }
}
