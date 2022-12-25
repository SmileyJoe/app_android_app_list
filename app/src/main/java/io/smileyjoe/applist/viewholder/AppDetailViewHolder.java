package io.smileyjoe.applist.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.applist.databinding.RowAppDetailsBinding;
import io.smileyjoe.applist.fragment.AppListFragment;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.view.ButtonProgress;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetailViewHolder extends RecyclerView.ViewHolder {

    public interface Listener {
        void onClick(ButtonProgress buttonProgress, AppDetail appDetail);
    }

    private RowAppDetailsBinding mView;
    private Listener mSaveClick;
    private Listener mDeleteClick;
    private AppListFragment.Type mType;

    public AppDetailViewHolder(ViewGroup parent, AppListFragment.Type type) {
        this(RowAppDetailsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), type);
    }

    private AppDetailViewHolder(RowAppDetailsBinding view, AppListFragment.Type type) {
        super(view.getRoot());
        mView = view;
        mType = type;
    }

    public void onSaveClick(Listener listener){
        mSaveClick = listener;
    }

    public void onDeleteClick(Listener listener){
        mDeleteClick = listener;
    }

    public void bind(AppDetail appDetail) {
        mView.textTitle.setText(appDetail.getName());
        mView.textPackage.setText(appDetail.getPackage());
        mView.getRoot().setOnClickListener(v -> openUrl(v, appDetail.getPlayStoreLink()));
        mView.buttonProgress.onEnabledClick(v -> mSaveClick.onClick(mView.buttonProgress, appDetail));
        mView.buttonProgress.onDisabledClick(v -> mDeleteClick.onClick(mView.buttonProgress, appDetail));

        if (appDetail.getIcon() != null) {
            mView.imageIcon.setVisibility(View.VISIBLE);
            mView.imageIcon.setImageDrawable(appDetail.getIcon());
        } else {
            mView.imageIcon.setVisibility(View.GONE);
        }

        if (appDetail.isSaved()) {
            mView.buttonProgress.setState(ButtonProgress.State.DISABLED);
        } else {
            mView.buttonProgress.setState(ButtonProgress.State.ENABLED);
        }

        if(mType == AppListFragment.Type.SAVED && appDetail.isInstalled()){
            mView.textInstalled.setVisibility(View.VISIBLE);
        } else {
            mView.textInstalled.setVisibility(View.GONE);
        }
    }

    private void openUrl(View view, String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(browserIntent);
    }
}
