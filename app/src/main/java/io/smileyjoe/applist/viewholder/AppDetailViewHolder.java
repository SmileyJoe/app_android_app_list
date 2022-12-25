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
        void onSaveClick(ButtonProgress buttonProgress, AppDetail appDetail);

        void onDeleteClick(ButtonProgress buttonProgress, AppDetail appDetail);
    }

    private RowAppDetailsBinding mView;
    private Listener mListener;
    private AppListFragment.Type mType;

    public AppDetailViewHolder(ViewGroup parent, AppListFragment.Type type, Listener listener) {
        this(RowAppDetailsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), type, listener);
    }

    private AppDetailViewHolder(RowAppDetailsBinding view, AppListFragment.Type type, Listener listener) {
        super(view.getRoot());
        mView = view;
        mListener = listener;
        mType = type;
    }

    public void bind(AppDetail appDetail) {
        mView.textTitle.setText(appDetail.getName());
        mView.textPackage.setText(appDetail.getPackage());
        mView.getRoot().setOnClickListener(v -> openUrl(v, appDetail.getPlayStoreLink()));
        mView.buttonProgress.onEnabledClick(v -> mListener.onSaveClick(mView.buttonProgress, appDetail));
        mView.buttonProgress.onDisabledClick(v -> mListener.onDeleteClick(mView.buttonProgress, appDetail));

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
