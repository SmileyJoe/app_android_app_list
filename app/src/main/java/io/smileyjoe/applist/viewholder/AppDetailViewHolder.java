package io.smileyjoe.applist.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.fragment.AppListFragment;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.OnUrlClick;
import io.smileyjoe.applist.view.ButtonProgress;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetailViewHolder extends RecyclerView.ViewHolder {

    public interface Listener {
        void onSaveClick(ButtonProgress buttonProgress, AppDetail appDetail);

        void onDeleteClick(ButtonProgress buttonProgress, AppDetail appDetail);
    }

    private ImageView mImageIcon;
    private TextView mTextTitle;
    private TextView mTextPackage;
    private TextView mTextInstalled;
    private Button mButtonPlayStore;
    private ButtonProgress mButtonProgress;
    private Listener mListener;
    private AppListFragment.Type mType;

    public AppDetailViewHolder(View itemView, AppListFragment.Type type, Listener listener) {
        super(itemView);

        mListener = listener;
        mType = type;

        mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
        mTextPackage = (TextView) itemView.findViewById(R.id.text_package);
        mTextInstalled = (TextView) itemView.findViewById(R.id.text_installed);
        mImageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
        mButtonPlayStore = (Button) itemView.findViewById(R.id.button_play_store);
        mButtonProgress = (ButtonProgress) itemView.findViewById(R.id.button_progress);
    }

    public void bind(AppDetail appDetail) {
        mTextTitle.setText(appDetail.getName());
        mTextPackage.setText(appDetail.getPackage());
        mButtonPlayStore.setOnClickListener(new OnUrlClick(appDetail.getPlayStoreLink()));
        mButtonProgress.setOnClickListener(new OnSaveClick(appDetail, mListener));

        if (appDetail.getIcon() != null) {
            mImageIcon.setVisibility(View.VISIBLE);
            mImageIcon.setImageDrawable(appDetail.getIcon());
        } else {
            mImageIcon.setVisibility(View.GONE);
        }

        if (appDetail.isSaved()) {
            mButtonProgress.setState(ButtonProgress.State.DISABLED);
        } else {
            mButtonProgress.setState(ButtonProgress.State.ENABLED);
        }

        if(mType == AppListFragment.Type.SAVED && appDetail.isInstalled()){
            mTextInstalled.setVisibility(View.VISIBLE);
        } else {
            mTextInstalled.setVisibility(View.GONE);
        }
    }

    private class OnSaveClick implements View.OnClickListener {

        private AppDetail mAppDetail;
        private Listener mListener;

        public OnSaveClick(AppDetail appDetail, Listener listener) {
            mAppDetail = appDetail;
            mListener = listener;
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                ButtonProgress buttonProgress = (ButtonProgress) view;

                switch (buttonProgress.getState()) {
                    case ENABLED:
                        mListener.onSaveClick(buttonProgress, mAppDetail);
                        break;
                    case DISABLED:
                        mListener.onDeleteClick(buttonProgress, mAppDetail);
                        break;
                }
            }
        }
    }
}
