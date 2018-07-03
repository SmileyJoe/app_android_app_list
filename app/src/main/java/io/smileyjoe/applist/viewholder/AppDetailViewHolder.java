package io.smileyjoe.applist.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.AppDetail;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetailViewHolder extends RecyclerView.ViewHolder {

    private ImageView mImageIcon;
    private TextView mTextTitle;

    public AppDetailViewHolder(View itemView) {
        super(itemView);

        mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
        mImageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
    }

    public void bind(AppDetail appDetail){
        mTextTitle.setText(appDetail.getName());
        mImageIcon.setImageDrawable(appDetail.getIcon());
    }
}
