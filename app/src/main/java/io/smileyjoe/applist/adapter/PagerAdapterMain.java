package io.smileyjoe.applist.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.smileyjoe.applist.fragment.AppListFragment;
import io.smileyjoe.applist.fragment.AppListFragment.Type;

/**
 * Created by cody on 2018/07/03.
 */

public class PagerAdapterMain extends FragmentPagerAdapter {

    public interface Listener extends AppListFragment.Listener{}

    private final Type[] mTypes = new Type[]{Type.INSTALLED, Type.SAVED};
    private Context mContext;
    private Listener mListener;

    public PagerAdapterMain(Context context, FragmentManager fm, Listener listener) {
        super(fm);
        mContext = context;
        mListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        AppListFragment fragment = AppListFragment.newInstance(mTypes[position], position);
        fragment.setListener(mListener);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTypes.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTypes[position].getFragmentTitle(mContext);
    }

}
