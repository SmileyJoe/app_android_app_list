package io.smileyjoe.applist.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.OnUrlClick;
import io.smileyjoe.applist.view.ButtonProgress;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetailViewHolder extends RecyclerView.ViewHolder {

    private ImageView mImageIcon;
    private TextView mTextTitle;
    private TextView mTextPackage;
    private Button mButtonPlayStore;
    private ButtonProgress mButtonProgress;

    public AppDetailViewHolder(View itemView) {
        super(itemView);

        mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
        mTextPackage = (TextView) itemView.findViewById(R.id.text_package);
        mImageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
        mButtonPlayStore = (Button) itemView.findViewById(R.id.button_play_store);
        mButtonProgress = (ButtonProgress) itemView.findViewById(R.id.button_progress);
    }

    public void bind(AppDetail appDetail){
        mTextTitle.setText(appDetail.getName());
        mTextPackage.setText(appDetail.getPackage());
        mImageIcon.setImageDrawable(appDetail.getIcon());
        mButtonPlayStore.setOnClickListener(new OnUrlClick(appDetail.getPlayStoreLink()));
        mButtonProgress.setOnClickListener(new OnSaveClick(appDetail));

        if(appDetail.isSaved()){
            mButtonProgress.setState(ButtonProgress.State.DISABLED);
        } else {
            mButtonProgress.setState(ButtonProgress.State.ENABLED);
        }
    }

    private class OnSaveClick implements View.OnClickListener{

        private AppDetail mAppDetail;

        public OnSaveClick(AppDetail appDetail) {
            mAppDetail = appDetail;
        }

        @Override
        public void onClick(View view) {
            ((ButtonProgress) view).setState(ButtonProgress.State.DISABLED);
            mAppDetail.setSaved(true);

            // TODO: Save the app //
        }
    }
}
