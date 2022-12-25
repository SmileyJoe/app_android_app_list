package io.smileyjoe.applist.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    public interface Listener {
        void onLoadComplete(Type type, int position, int appCount);
    }

    public enum Type {
        INSTALLED(R.string.fragment_title_installed_apps),
        SAVED(R.string.fragment_title_saved_apps);

        private int mFragmentTitleResId;

        Type(int fragmentTitleResId) {
            mFragmentTitleResId = fragmentTitleResId;
        }

        public String getFragmentTitle(Context context) {
            return context.getString(mFragmentTitleResId);
        }
    }

    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_POSITION = "position";
    private Type mType;
    private AppDetailAdapter mAppDetailAdapter;
    private TextView mTextEmpty;
    private ProgressBar mProgressLoading;
    private RecyclerView mRecyclerAppDetails;
    private boolean mLoading = true;
    private int mPosition;
    private Listener mListener;

    public AppListFragment() {
    }

    public static AppListFragment newInstance(Type type, int position) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TYPE, type);
        args.putInt(EXTRA_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mType = (Type) getArguments().getSerializable(EXTRA_TYPE);
        mPosition = getArguments().getInt(EXTRA_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_list, container, false);

        mTextEmpty = (TextView) rootView.findViewById(R.id.text_empty);
        mProgressLoading = (ProgressBar) rootView.findViewById(R.id.progress_loading);

        mAppDetailAdapter = new AppDetailAdapter(new ArrayList<AppDetail>(), mType, new AdapterListener());

        mRecyclerAppDetails = (RecyclerView) rootView.findViewById(R.id.recycler_app_details);
        mRecyclerAppDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAppDetails.setAdapter(mAppDetailAdapter);
//        mRecyclerAppDetails.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        populateList();
        handleDisplayView();

        return rootView;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void populateList() {
        Db.getDetailReference(getActivity()).addValueEventListener(new AppDetailsEventListener());
    }

    private void handleDisplayView() {
        if (mLoading) {
            mProgressLoading.setVisibility(View.VISIBLE);
            mRecyclerAppDetails.setVisibility(View.GONE);
            mTextEmpty.setVisibility(View.GONE);
        } else if (mAppDetailAdapter.hasApps()) {
            mProgressLoading.setVisibility(View.GONE);
            mRecyclerAppDetails.setVisibility(View.VISIBLE);
            mTextEmpty.setVisibility(View.GONE);
        } else {
            mProgressLoading.setVisibility(View.GONE);
            mRecyclerAppDetails.setVisibility(View.GONE);
            mTextEmpty.setVisibility(View.VISIBLE);
        }
    }

    private class AppDetailsEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<AppDetail> apps = new ArrayList<>();

            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                apps.add(new AppDetail(itemSnapshot));
            }

            switch (mType) {
                case INSTALLED:
                    if (mAppDetailAdapter.hasApps()) {
                        mAppDetailAdapter.onSavedUpdated(apps);
                    } else {
                        mAppDetailAdapter.update(PackageUtil.getInstalledApplications(getContext().getPackageManager(), apps));
                    }
                    break;
                case SAVED:
                    mAppDetailAdapter.update(PackageUtil.checkInstalled(getContext().getPackageManager(), apps));
                    break;
            }

            mLoading = false;
            handleDisplayView();

            if(mListener != null){
                mListener.onLoadComplete(mType, mPosition, mAppDetailAdapter.getItemCount());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Notify.error(getActivity(), R.string.error_database_read_failed);
        }
    }

    private class AdapterListener implements AppDetailAdapter.Listener {

        @Override
        public void onSaveClick(ButtonProgress buttonProgress, AppDetail appDetail) {
            if (appDetail.save(getActivity(), new SaveCompletionListener(getActivity(), buttonProgress, appDetail))) {
                buttonProgress.setState(ButtonProgress.State.LOADING);
            }
        }

        @Override
        public void onDeleteClick(ButtonProgress buttonProgress, AppDetail appDetail) {
            if (appDetail.delete(getActivity(), new DeleteCompletionListener(getActivity(), buttonProgress, appDetail))) {
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
            if (mType == Type.INSTALLED) {
                getAppDetail().setSaved(false);
                getButtonProgress().setState(ButtonProgress.State.ENABLED);
            }
        }

        @Override
        protected void onFail() {
            super.onFail();
            getButtonProgress().setState(ButtonProgress.State.DISABLED);
        }
    }

    private class SaveCompletionListener extends DbCompletionListener {

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
