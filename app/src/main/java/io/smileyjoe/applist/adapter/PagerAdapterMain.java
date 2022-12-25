package io.smileyjoe.applist.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.smileyjoe.applist.fragment.AppListFragment;
import io.smileyjoe.applist.fragment.AppListFragment.Type;

/**
 * Created by cody on 2018/07/03.
 */

public class PagerAdapterMain extends FragmentStateAdapter {

    public interface Listener extends AppListFragment.Listener{}

    private final Type[] mTypes = new Type[]{Type.INSTALLED, Type.SAVED};
    private Context mContext;
    private Listener mListener;

    public PagerAdapterMain(FragmentActivity activity, Listener listener) {
        super(activity);
        mContext = activity.getBaseContext();
        mListener = listener;
    }

    @Override
    public Fragment createFragment(int position) {
        AppListFragment fragment = AppListFragment.newInstance(mTypes[position], position);
        fragment.setListener(mListener);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return mTypes.length;
    }
}
