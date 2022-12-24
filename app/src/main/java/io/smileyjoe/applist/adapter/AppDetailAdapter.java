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
import io.smileyjoe.applist.viewholder.AppDetailViewHolder;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetailAdapter extends RecyclerView.Adapter<AppDetailViewHolder> {

    public interface Listener extends AppDetailViewHolder.Listener {
    }

    private List<AppDetail> mItems;
    private Listener mListener;
    private AppListFragment.Type mType;

    public AppDetailAdapter(List<AppDetail> items, AppListFragment.Type type, Listener listener) {
        setItems(items);
        mType = type;
        mListener = listener;
    }

    public void setItems(List<AppDetail> items) {
        Collections.sort(items, new AppDetailComparator());
        mItems = items;
    }

    @Override
    public AppDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppDetailViewHolder(parent, mType, mListener);
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
