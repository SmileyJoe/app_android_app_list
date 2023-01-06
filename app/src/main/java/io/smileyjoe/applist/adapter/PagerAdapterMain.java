package io.smileyjoe.applist.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.smileyjoe.applist.enums.Page;
import io.smileyjoe.applist.fragment.AppListFragment;

/**
 * Created by cody on 2018/07/03.
 */

public class PagerAdapterMain extends FragmentStateAdapter {

    public interface Listener extends AppListFragment.Listener{}

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
