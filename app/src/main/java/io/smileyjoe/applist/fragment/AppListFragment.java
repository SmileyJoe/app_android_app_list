package io.smileyjoe.applist.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.stream.Collectors;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.AppDetailAdapter;
import io.smileyjoe.applist.databinding.FragmentAppListBinding;
import io.smileyjoe.applist.enums.Page;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.Db;
import io.smileyjoe.applist.util.DbCompletionListener;
import io.smileyjoe.applist.util.Notify;
import io.smileyjoe.applist.util.PackageUtil;
import io.smileyjoe.applist.view.ButtonProgress;
import io.smileyjoe.applist.view.ImageSelected;
import io.smileyjoe.applist.viewholder.AppDetailViewHolder;

/**
 * Created by cody on 2018/07/03.
 */

public class AppListFragment extends Fragment {

    public interface Listener {
        void onLoadComplete(Page page, int position, int appCount);
    }

    private static final String EXTRA_PAGE = "page";
    private static final String EXTRA_POSITION = "position";
    private Page mPage;
    private AppDetailAdapter mAppDetailAdapter;
    private boolean mLoading = true;
    private int mPosition;
    private Listener mListener;
    private FragmentAppListBinding mView;

    public AppListFragment() {
    }

    public static AppListFragment newInstance(Page page, int position) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PAGE, page);
        args.putInt(EXTRA_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mPage = (Page) getArguments().getSerializable(EXTRA_PAGE);
        mPosition = getArguments().getInt(EXTRA_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = FragmentAppListBinding.inflate(getLayoutInflater(), container, false);

        setupAdapter();

        mView.recyclerAppDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        mView.recyclerAppDetails.setAdapter(mAppDetailAdapter);

        populateList();
        handleDisplayView();

        return mView.getRoot();
    }

    public void setupAdapter(){
        mAppDetailAdapter = new AppDetailAdapter(new ArrayList<AppDetail>(), mPage);
        mAppDetailAdapter.onSaveClick((viewHolder, appDetail) -> {
            if (appDetail.save(getActivity(), new SaveCompletionListener(getActivity(), viewHolder, appDetail))) {
                viewHolder.setLoading();
            }
        });
        mAppDetailAdapter.onDeleteClick((viewHolder, appDetail) -> {
            if (appDetail.delete(getActivity(), new DeleteCompletionListener(getActivity(), viewHolder, appDetail))) {
                viewHolder.setLoading();
            }
        });
        mAppDetailAdapter.onFavouriteSelected(((viewHolder, appDetail) -> {
            appDetail.isFavourite(true);
            appDetail.save(getActivity(), new OnFavouriteUpdate(getActivity(), viewHolder, appDetail));
        }));
        mAppDetailAdapter.onFavouriteDeselected(((viewHolder, appDetail) -> {
            appDetail.isFavourite(false);
            appDetail.save(getActivity(), new OnFavouriteUpdate(getActivity(), viewHolder, appDetail));
        }));
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void populateList() {
        Db.getDetailReference(getActivity()).addValueEventListener(new AppDetailsEventListener());
    }

    private void handleDisplayView() {
        if (mLoading) {
            mView.progressLoading.setVisibility(View.VISIBLE);
            mView.recyclerAppDetails.setVisibility(View.GONE);
            mView.textEmpty.setVisibility(View.GONE);
        } else if (mAppDetailAdapter.hasApps()) {
            mView.progressLoading.setVisibility(View.GONE);
            mView.recyclerAppDetails.setVisibility(View.VISIBLE);
            mView.textEmpty.setVisibility(View.GONE);
        } else {
            mView.progressLoading.setVisibility(View.GONE);
            mView.recyclerAppDetails.setVisibility(View.GONE);
            mView.textEmpty.setVisibility(View.VISIBLE);
        }
    }

    private class AppDetailsEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mAppDetailAdapter.update(mPage.getApps(getContext(), dataSnapshot));

            mLoading = false;
            handleDisplayView();

            if(mListener != null){
                mListener.onLoadComplete(mPage, mPosition, mAppDetailAdapter.getItemCount());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Notify.error(getActivity(), R.string.error_database_read_failed);
        }
    }

    private class DeleteCompletionListener extends DbCompletionListener {

        public DeleteCompletionListener(Activity activity, AppDetailViewHolder viewHolder, AppDetail appDetail) {
            super(activity, viewHolder, appDetail);
        }

        @Override
        protected void onSuccess() {
            if (mPage == Page.INSTALLED) {
                getAppDetail().setSaved(false);
                getViewHolder().updateState(getAppDetail());
            }
        }

        @Override
        protected void onFail() {
            super.onFail();
            getViewHolder().updateState(getAppDetail());
        }
    }

    private class SaveCompletionListener extends DbCompletionListener {

        public SaveCompletionListener(Activity activity, AppDetailViewHolder viewHolder, AppDetail appDetail) {
            super(activity, viewHolder, appDetail);
        }

        @Override
        protected void onSuccess() {
            getAppDetail().setSaved(true);
            getViewHolder().updateState(getAppDetail());
        }

        @Override
        protected void onFail() {
            super.onFail();
            getViewHolder().updateState(getAppDetail());
        }
    }

    private class OnFavouriteUpdate extends DbCompletionListener{

        public OnFavouriteUpdate(Activity activity, AppDetailViewHolder viewHolder, AppDetail appDetail) {
            super(activity, viewHolder, appDetail);
        }

        @Override
        protected void onSuccess() {
            getViewHolder().updateState(getAppDetail());
        }

        @Override
        protected void onFail() {
            super.onFail();
            getAppDetail().isFavourite(!getAppDetail().isFavourite());
            getViewHolder().updateState(getAppDetail());
        }
    }
}
