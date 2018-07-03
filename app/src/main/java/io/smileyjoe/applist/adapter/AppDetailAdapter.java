package io.smileyjoe.applist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.viewholder.AppDetailViewHolder;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetailAdapter extends RecyclerView.Adapter<AppDetailViewHolder>{

    private List<AppDetail> mItems;

    public AppDetailAdapter(List<AppDetail> items) {
        mItems = items;
    }

    @Override
    public AppDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppDetailViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_app_details, parent, false));
    }

    @Override
    public void onBindViewHolder(AppDetailViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public AppDetail getItem(int position){
        return mItems.get(position);
    }
}
