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

    private final Type[] mTypes = new Type[]{Type.INSTALLED, Type.SAVED};
    private Context mContext;

    public PagerAdapterMain(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return AppListFragment.newInstance(mTypes[position]);
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
