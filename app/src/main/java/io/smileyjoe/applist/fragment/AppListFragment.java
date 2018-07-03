package io.smileyjoe.applist.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.AppDetailAdapter;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.PackageUtil;

/**
 * Created by cody on 2018/07/03.
 */

public class AppListFragment extends Fragment {

    public enum Type{
        INSTALLED(R.string.fragment_title_installed_apps);

        private int mFragmentTitleResId;

        Type(int fragmentTitleResId) {
            mFragmentTitleResId = fragmentTitleResId;
        }

        public String getFragmentTitle(Context context){
            return context.getString(mFragmentTitleResId);
        }
    }

    private static final String EXTRA_TYPE = "type";
    private Type mType;

    public AppListFragment() {
    }

    public static AppListFragment newInstance(Type type) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mType = (Type) getArguments().getSerializable(EXTRA_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_list, container, false);

        RecyclerView recyclerAppDetails = (RecyclerView) rootView.findViewById(R.id.recycler_app_details);
        recyclerAppDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAppDetails.setAdapter(new AppDetailAdapter(getAppDetailList()));
        recyclerAppDetails.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        return rootView;
    }

    private List<AppDetail> getAppDetailList(){
        switch (mType){
            case INSTALLED:
                return PackageUtil.getInstalledApplications(getContext().getPackageManager());
        }

        return new ArrayList<>();
    }
}
