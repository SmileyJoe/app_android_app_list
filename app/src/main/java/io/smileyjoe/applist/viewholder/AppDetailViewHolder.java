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
import io.smileyjoe.applist.view.ImageSelected;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetailViewHolder extends RecyclerView.ViewHolder {

    public interface Listener {
        void onClick(AppDetailViewHolder viewHolder, AppDetail appDetail);
    }

    private RowAppDetailsBinding mView;
    private Listener mSaveClick;
    private Listener mDeleteClick;
    private Listener mFavouriteSelected;
    private Listener mFavouriteDeselected;
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

    public void onFavouriteSelected(Listener listener){
        mFavouriteSelected = listener;
    }

    public void onFavouriteDeselected(Listener listener){
        mFavouriteDeselected = listener;
    }

    public void bind(AppDetail appDetail) {
        mView.textTitle.setText(appDetail.getName());
        mView.textPackage.setText(appDetail.getPackage());
        mView.getRoot().setOnClickListener(v -> openUrl(v, appDetail.getPlayStoreLink()));
        mView.buttonProgress.onEnabledClick(v -> mSaveClick.onClick(this, appDetail));
        mView.buttonProgress.onDisabledClick(v -> mDeleteClick.onClick(this, appDetail));
        mView.imageFavourite.onSelected(v -> mFavouriteSelected.onClick(this, appDetail));
        mView.imageFavourite.onDeselected(v -> mFavouriteDeselected.onClick(this, appDetail));

        if (appDetail.getIcon() != null) {
            mView.imageIcon.setVisibility(View.VISIBLE);
            mView.imageIcon.setImageDrawable(appDetail.getIcon());
        } else {
            mView.imageIcon.setVisibility(View.GONE);
        }

        updateState(appDetail);
    }

    public void updateState(AppDetail appDetail){
        if (appDetail.isSaved()) {
            mView.buttonProgress.setState(ButtonProgress.State.DISABLED);
            mView.imageFavourite.setVisibility(View.VISIBLE);
            mView.imageFavourite.setState(appDetail.isFavourite() ? ImageSelected.State.SELECTED : ImageSelected.State.DESELECTED);
        } else {
            mView.buttonProgress.setState(ButtonProgress.State.ENABLED);
            mView.imageFavourite.setVisibility(View.GONE);
        }

        if(mType == AppListFragment.Type.SAVED && appDetail.isInstalled()){
            mView.textInstalled.setVisibility(View.VISIBLE);
        } else {
            mView.textInstalled.setVisibility(View.GONE);
        }
    }

    public void setLoading(){
        mView.buttonProgress.setState(ButtonProgress.State.LOADING);
    }

    private void openUrl(View view, String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(browserIntent);
    }
}
