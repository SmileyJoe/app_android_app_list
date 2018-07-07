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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.AppDetailAdapter;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.Notify;
import io.smileyjoe.applist.util.PackageUtil;
import io.smileyjoe.applist.view.ButtonProgress;

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
        recyclerAppDetails.setAdapter(new AppDetailAdapter(getAppDetailList(), new AdapterListener()));
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

    private class AdapterListener implements AppDetailAdapter.Listener{

        @Override
        public void onSaveClick(ButtonProgress buttonProgress, AppDetail appDetail) {
            if(appDetail.save(getActivity(), new SaveCompletionListener(buttonProgress, appDetail))){
                buttonProgress.setState(ButtonProgress.State.LOADING);
            }
        }

        @Override
        public void onDeleteClick(ButtonProgress buttonProgress, AppDetail appDetail) {
            if(appDetail.delete(getActivity(), new DeleteCompletionListener(buttonProgress, appDetail))){
                buttonProgress.setState(ButtonProgress.State.LOADING);
            }
        }
    }

    private abstract class CompletionListener implements DatabaseReference.CompletionListener{
        private ButtonProgress mButtonProgress;
        private AppDetail mAppDetail;
        private DatabaseError mDatabaseError;
        private DatabaseReference mDatabaseReference;

        protected abstract void onSuccess();

        public CompletionListener(ButtonProgress buttonProgress, AppDetail appDetail) {
            mButtonProgress = buttonProgress;
            mAppDetail = appDetail;
        }

        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            if(databaseError == null){
                onSuccess();
            } else {
                onFail();
            }
        }

        protected void onFail(){
            Notify.error(getActivity(), R.string.error_generic);
        }

        public ButtonProgress getButtonProgress() {
            return mButtonProgress;
        }

        public AppDetail getAppDetail() {
            return mAppDetail;
        }

        public DatabaseError getDatabaseError() {
            return mDatabaseError;
        }

        public DatabaseReference getDatabaseReference() {
            return mDatabaseReference;
        }
    }

    private class DeleteCompletionListener extends CompletionListener{

        public DeleteCompletionListener(ButtonProgress buttonProgress, AppDetail appDetail) {
            super(buttonProgress, appDetail);
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

    private class SaveCompletionListener extends CompletionListener{

        public SaveCompletionListener(ButtonProgress buttonProgress, AppDetail appDetail) {
            super(buttonProgress, appDetail);
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
