package io.smileyjoe.applist.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.OnUrlClick;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetailViewHolder extends RecyclerView.ViewHolder {

    private ImageView mImageIcon;
    private TextView mTextTitle;
    private Button mButtonPlayStore;

    public AppDetailViewHolder(View itemView) {
        super(itemView);

        mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
        mImageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
        mButtonPlayStore = (Button) itemView.findViewById(R.id.button_play_store);
    }

    public void bind(AppDetail appDetail){
        mTextTitle.setText(appDetail.getName());
        mImageIcon.setImageDrawable(appDetail.getIcon());
        mButtonPlayStore.setOnClickListener(new OnUrlClick(appDetail.getPlayStoreLink()));
    }
}
