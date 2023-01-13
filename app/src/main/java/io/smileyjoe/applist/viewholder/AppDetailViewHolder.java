package io.smileyjoe.applist.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.applist.databinding.RowAppDetailsBinding;
import io.smileyjoe.applist.enums.Page;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.Icon;
import io.smileyjoe.applist.view.ButtonProgress;
import io.smileyjoe.applist.view.ImageSelected;

public class AppDetailViewHolder extends RecyclerView.ViewHolder {

    public interface ItemSelectedListener{
        void onSelected(AppDetail appDetail);
    }

    public interface Listener {
        void onUpdate(AppDetail appDetail);
    }

    private RowAppDetailsBinding mView;
    private Listener mSaveListener;
    private Listener mDeleteListener;
    private ItemSelectedListener mItemSelectedListener;
    private Page mPage;

    public AppDetailViewHolder(ViewGroup parent, Page page) {
        this(RowAppDetailsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), page);
    }

    private AppDetailViewHolder(RowAppDetailsBinding view, Page page) {
        super(view.getRoot());
        mView = view;
        mPage = page;
    }

    public void onSave(Listener listener) {
        mSaveListener = listener;
    }

    public void onDelete(Listener listener) {
        mDeleteListener = listener;
    }

    public void onItemSelected(ItemSelectedListener listener){
        mItemSelectedListener = listener;
    }

    public void bind(AppDetail appDetail) {
        mView.textTitle.setText(appDetail.getName());
        mView.textPackage.setText(appDetail.getAppPackage());
        mView.getRoot().setOnClickListener(v -> mItemSelectedListener.onSelected(appDetail));
        mView.buttonProgress.onEnabledClick(v -> save(appDetail));
        mView.buttonProgress.onDisabledClick(v -> mDeleteListener.onUpdate(appDetail));
        mView.imageFavourite.onSelected(v -> favourite(appDetail, true));
        mView.imageFavourite.onDeselected(v -> favourite(appDetail, false));

        Icon.load(mView.imageIcon, appDetail);

        updateState(appDetail);
    }

    private void save(AppDetail appDetail) {
        mView.buttonProgress.setState(ButtonProgress.State.LOADING);
        appDetail.setSaved(true);
        mSaveListener.onUpdate(appDetail);
    }

    private void favourite(AppDetail appDetail, boolean isFavourite) {
        appDetail.setFavourite(isFavourite);
        mSaveListener.onUpdate(appDetail);
    }

    public void updateState(AppDetail appDetail) {
        if (appDetail.isSaved()) {
            mView.buttonProgress.setState(ButtonProgress.State.DISABLED);
            mView.imageFavourite.setVisibility(View.VISIBLE);
            mView.imageFavourite.setState(appDetail.isFavourite() ? ImageSelected.State.SELECTED : ImageSelected.State.DESELECTED);
        } else {
            mView.buttonProgress.setState(ButtonProgress.State.ENABLED);
            mView.imageFavourite.setVisibility(View.GONE);
        }

        if (mPage != Page.INSTALLED && appDetail.isInstalled()) {
            mView.textInstalled.setVisibility(View.VISIBLE);
        } else {
            mView.textInstalled.setVisibility(View.GONE);
        }
    }
}
