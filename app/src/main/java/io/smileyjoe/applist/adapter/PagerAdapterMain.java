package io.smileyjoe.applist.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.smileyjoe.applist.enums.Page;
import io.smileyjoe.applist.fragment.AppListFragment;

public class PagerAdapterMain extends FragmentStateAdapter {

    public interface Listener extends AppListFragment.Listener {
    }

    public interface ItemSelectedListener extends AppListFragment.ItemSelectedListener{
    }

    public interface ScrollListener extends AppListFragment.ScrollListener {}

    private Listener mListener;
    private ScrollListener mScrollListener;
    private ItemSelectedListener mItemSelectedListener;

    public PagerAdapterMain(FragmentActivity activity, Listener listener, ItemSelectedListener itemSelectedListener, ScrollListener scrollListener) {
        super(activity);
        mListener = listener;
        mItemSelectedListener = itemSelectedListener;
        mScrollListener = scrollListener;
    }

    @Override
    public Fragment createFragment(int position) {
        AppListFragment fragment = AppListFragment.newInstance(Page.fromPosition(position));
        fragment.setListener(mListener);
        fragment.setItemSelectedListener(mItemSelectedListener);
        fragment.setScrollListener(mScrollListener);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return Page.values().length;
    }
}
