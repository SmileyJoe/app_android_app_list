package io.smileyjoe.applist.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import java.util.Arrays;

import io.smileyjoe.applist.R;

public class ImageSelected extends androidx.appcompat.widget.AppCompatImageView implements View.OnClickListener {

    public enum State {
        SELECTED(0),
        DESELECTED(1);

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
                    .orElse(DESELECTED);
        }
    }

    private State mState;
    private OnClickListener mOnSelected;
    private OnClickListener mOnDeselected;
    @DrawableRes
    private int mSrcSelected;
    @DrawableRes
    private int mSrcDeselected;

    public ImageSelected(Context context) {
        super(context);
        init(null);
    }

    public ImageSelected(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ImageSelected(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ImageSelected);

            if (typedArray != null) {
                mSrcSelected = typedArray.getResourceId(R.styleable.ImageSelected_src_selected, Resources.ID_NULL);
                mSrcDeselected = typedArray.getResourceId(R.styleable.ImageSelected_src_deselected, Resources.ID_NULL);
                setState(State.fromId(typedArray.getInt(R.styleable.ImageSelected_image_selected_state, State.DESELECTED.getId())));
            }
        }

        setOnClickListener(this);
    }

    public void setState(State state) {
        mState = state;
        switch (mState) {
            case SELECTED:
                setImageResource(mSrcSelected);
                break;
            case DESELECTED:
                setImageResource(mSrcDeselected);
                break;
        }
    }

    public void onSelected(View.OnClickListener listener) {
        mOnSelected = listener;
    }

    public void onDeselected(View.OnClickListener listener) {
        mOnDeselected = listener;
    }

    @Override
    public void onClick(View view) {
        setState(mState == State.SELECTED ? State.DESELECTED : State.SELECTED);

        switch (mState) {
            case SELECTED:
                mOnSelected.onClick(this);
                break;
            case DESELECTED:
                mOnDeselected.onClick(this);
                break;
        }
    }
}
