package io.smileyjoe.applist.fragment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.PackageUtil;
import za.co.smileyjoedev.lib.debug.Debug;

/**
 * Created by cody on 2018/07/03.
 */

public class AppListFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public AppListFragment() {
    }

    public static AppListFragment newInstance(int sectionNumber) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        List<AppDetail> appDetails = PackageUtil.getInstalledApplications(context.getPackageManager());

        Debug.d(appDetails);
    }

}
