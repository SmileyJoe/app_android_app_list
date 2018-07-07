package io.smileyjoe.applist.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.AppDetailAdapter;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.Db;
import io.smileyjoe.applist.util.DbCompletionListener;
import io.smileyjoe.applist.util.Notify;
import io.smileyjoe.applist.util.PackageUtil;
import io.smileyjoe.applist.view.ButtonProgress;

/**
 * Created by cody on 2018/07/03.
 */

public class AppListFragment extends Fragment {

    public enum Type{
        INSTALLED(R.string.fragment_title_installed_apps),
        SAVED(R.string.fragment_title_saved_apps);

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
    private AppDetailAdapter mAppDetailAdapter;

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

        mAppDetailAdapter = new AppDetailAdapter(new ArrayList<AppDetail>(), new AdapterListener());

        RecyclerView recyclerAppDetails = (RecyclerView) rootView.findViewById(R.id.recycler_app_details);
        recyclerAppDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAppDetails.setAdapter(mAppDetailAdapter);
        recyclerAppDetails.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        populateList();

        return rootView;
    }

    private List<AppDetail> populateList(){
        switch (mType){
            case INSTALLED:
                mAppDetailAdapter.update(PackageUtil.getInstalledApplications(getContext().getPackageManager()));
                break;
            case SAVED:
                Db.getDetailReference(getActivity()).addValueEventListener(new AppDetailsEventListener());
                break;
        }

        return new ArrayList<>();
    }

    private class AppDetailsEventListener implements ValueEventListener{
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<AppDetail> apps = new ArrayList<>();

            for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                apps.add(new AppDetail(itemSnapshot));
            }

            mAppDetailAdapter.update(apps);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Notify.error(getActivity(), R.string.error_database_read_failed);
        }
    }

    private class AdapterListener implements AppDetailAdapter.Listener{

        @Override
        public void onSaveClick(ButtonProgress buttonProgress, AppDetail appDetail) {
            if(appDetail.save(getActivity(), new SaveCompletionListener(getActivity(), buttonProgress, appDetail))){
                buttonProgress.setState(ButtonProgress.State.LOADING);
            }
        }

        @Override
        public void onDeleteClick(ButtonProgress buttonProgress, AppDetail appDetail) {
            if(appDetail.delete(getActivity(), new DeleteCompletionListener(getActivity(), buttonProgress, appDetail))){
                buttonProgress.setState(ButtonProgress.State.LOADING);
            }
        }
    }

    private class DeleteCompletionListener extends DbCompletionListener {

        public DeleteCompletionListener(Activity activity, ButtonProgress buttonProgress, AppDetail appDetail) {
            super(activity, buttonProgress, appDetail);
        }

        @Override
        protected void onSuccess() {
            getAppDetail().setSaved(false);
            getButtonProgress().setState(ButtonProgress.State.ENABLED);
        }

        @Override
        protected void onFail() {
            super.onFail();
            getButtonProgress().setState(ButtonProgress.State.DISABLED);
        }
    }

    private class SaveCompletionListener extends DbCompletionListener{

        public SaveCompletionListener(Activity activity, ButtonProgress buttonProgress, AppDetail appDetail) {
            super(activity, buttonProgress, appDetail);
        }

        @Override
        protected void onSuccess() {
            getAppDetail().setSaved(true);
            getButtonProgress().setState(ButtonProgress.State.DISABLED);
        }

        @Override
        protected void onFail() {
            super.onFail();
            getButtonProgress().setState(ButtonProgress.State.ENABLED);
        }
    }
}
