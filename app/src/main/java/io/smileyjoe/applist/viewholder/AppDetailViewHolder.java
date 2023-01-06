package io.smileyjoe.applist.viewholder;

import android.content.Intent;
import android.net.Uri;
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

    public interface Listener {
        void onUpdate(AppDetail appDetail);
    }

    private RowAppDetailsBinding mView;
    private Listener mSaveListener;
    private Listener mDeleteListener;
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

    public void bind(AppDetail appDetail) {
        mView.textTitle.setText(appDetail.getName());
        mView.textPackage.setText(appDetail.getPackage());
        mView.getRoot().setOnClickListener(v -> openUrl(v, appDetail.getPlayStoreLink()));
        mView.buttonProgress.onEnabledClick(v -> save(appDetail));
        mView.buttonProgress.onDisabledClick(v -> mDeleteListener.onUpdate(appDetail));
        mView.imageFavourite.onSelected(v -> favourite(appDetail, true));
        mView.imageFavourite.onDeselected(v -> favourite(appDetail, false));

        if (appDetail.getIcon() != null) {
            mView.imageIcon.setVisibility(View.VISIBLE);
            mView.imageIcon.setImageDrawable(appDetail.getIcon());
        } else {
            Icon.load(mView.imageIcon, appDetail.getPackage());
        }

        updateState(appDetail);
    }

    private void save(AppDetail appDetail) {
        mView.buttonProgress.setState(ButtonProgress.State.LOADING);
        appDetail.setSaved(true);
        mSaveListener.onUpdate(appDetail);
    }

    private void favourite(AppDetail appDetail, boolean isFavourite) {
        appDetail.isFavourite(isFavourite);
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

    private void openUrl(View view, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(browserIntent);
    }
}
