package io.smileyjoe.applist.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.smileyjoe.applist.enums.Page;
import io.smileyjoe.applist.fragment.AppListFragment;

public class PagerAdapterMain extends FragmentStateAdapter {

    public interface Listener extends AppListFragment.Listener {
    }

    private Listener mListener;

    public PagerAdapterMain(FragmentActivity activity, Listener listener) {
        super(activity);
        mListener = listener;
    }

    @Override
    public Fragment createFragment(int position) {
        AppListFragment fragment = AppListFragment.newInstance(Page.fromPosition(position), position);
        fragment.setListener(mListener);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return Page.values().length;
    }
}
