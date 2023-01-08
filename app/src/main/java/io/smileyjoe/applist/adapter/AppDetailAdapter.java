package io.smileyjoe.applist.adapter;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import io.smileyjoe.applist.comparator.AppDetailComparator;
import io.smileyjoe.applist.enums.Page;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.viewholder.AppDetailViewHolder;

public class AppDetailAdapter extends RecyclerView.Adapter<AppDetailViewHolder> {

    private List<AppDetail> mItems;
    private AppDetailViewHolder.Listener mSaveListener;
    private AppDetailViewHolder.Listener mDeleteListener;
    private AppDetailViewHolder.ItemSelectedListener mItemSelectedListener;
    private Page mPage;

    public AppDetailAdapter(List<AppDetail> items, Page page) {
        setItems(items);
        mPage = page;
    }

    public void setItems(List<AppDetail> items) {
        Collections.sort(items, new AppDetailComparator());
        mItems = items;
    }

    public void onSave(AppDetailViewHolder.Listener listener) {
        mSaveListener = listener;
    }

    public void onDelete(AppDetailViewHolder.Listener listener) {
        mDeleteListener = listener;
    }

    public void onItemSelected(AppDetailViewHolder.ItemSelectedListener listener){
        mItemSelectedListener = listener;
    }

    @Override
    public AppDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AppDetailViewHolder holder = new AppDetailViewHolder(parent, mPage);
        holder.onSave(mSaveListener);
        holder.onDelete(mDeleteListener);
        holder.onItemSelected(mItemSelectedListener);
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

    public AppDetail getItem(int position) {
        return mItems.get(position);
    }
}
