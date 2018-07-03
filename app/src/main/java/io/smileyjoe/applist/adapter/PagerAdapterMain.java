package io.smileyjoe.applist.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.smileyjoe.applist.fragment.AppListFragment;

/**
 * Created by cody on 2018/07/03.
 */

public class PagerAdapterMain extends FragmentPagerAdapter {

    public PagerAdapterMain(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return AppListFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
        }
        return null;
    }

}
