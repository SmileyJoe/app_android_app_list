package io.smileyjoe.applist.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.AppDetailAdapter;
import io.smileyjoe.applist.util.PackageUtil;

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
        View rootView = inflater.inflate(R.layout.fragment_app_list, container, false);

        RecyclerView recyclerAppDetails = (RecyclerView) rootView.findViewById(R.id.recycler_app_details);
        recyclerAppDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAppDetails.setAdapter(new AppDetailAdapter(PackageUtil.getInstalledApplications(getContext().getPackageManager())));
        return rootView;
    }
}
