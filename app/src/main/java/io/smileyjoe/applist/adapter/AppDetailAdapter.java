package io.smileyjoe.applist.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.comparator.AppDetailComparator;
import io.smileyjoe.applist.fragment.AppListFragment;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.view.ImageSelected;
import io.smileyjoe.applist.viewholder.AppDetailViewHolder;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetailAdapter extends RecyclerView.Adapter<AppDetailViewHolder> {

    private List<AppDetail> mItems;
    private AppDetailViewHolder.Listener mSaveClick;
    private AppDetailViewHolder.Listener mDeleteClick;
    private AppDetailViewHolder.Listener mFavouriteSelected;
    private AppDetailViewHolder.Listener mFavouriteDeselected;
    private AppListFragment.Type mType;

    public AppDetailAdapter(List<AppDetail> items, AppListFragment.Type type) {
        setItems(items);
        mType = type;
    }

    public void setItems(List<AppDetail> items) {
        Collections.sort(items, new AppDetailComparator());
        mItems = items;
    }

    public void onSaveClick(AppDetailViewHolder.Listener listener){
        mSaveClick = listener;
    }

    public void onDeleteClick(AppDetailViewHolder.Listener listener){
        mDeleteClick = listener;
    }

    public void onFavouriteSelected(AppDetailViewHolder.Listener listener){
        mFavouriteSelected = listener;
    }

    public void onFavouriteDeselected(AppDetailViewHolder.Listener listener){
        mFavouriteDeselected = listener;
    }

    @Override
    public AppDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AppDetailViewHolder holder = new AppDetailViewHolder(parent, mType);
        holder.onSaveClick(mSaveClick);
        holder.onDeleteClick(mDeleteClick);
        holder.onFavouriteSelected(mFavouriteSelected);
        holder.onFavouriteDeselected(mFavouriteDeselected);
        return holder;
    }

    @Override
    public void onBindViewHolder(AppDetailViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void update(List<AppDetail> appDetails) {
        setItems(appDetails);
        notifyDataSetChanged();
    }

    public boolean hasApps() {
        return mItems != null && !mItems.isEmpty();
    }

    public void onSavedUpdated(List<AppDetail> savedApps) {
        for (AppDetail app : mItems) {
            app.onSavedUpdated(savedApps);
        }

        notifyDataSetChanged();
    }

    public AppDetail getItem(int position) {
        return mItems.get(position);
    }
}
