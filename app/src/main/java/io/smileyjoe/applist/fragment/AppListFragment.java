package io.smileyjoe.applist.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Optional;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.AppDetailAdapter;
import io.smileyjoe.applist.databinding.FragmentAppListBinding;
import io.smileyjoe.applist.enums.Direction;
import io.smileyjoe.applist.enums.Page;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.Db;
import io.smileyjoe.applist.util.Notify;
import io.smileyjoe.applist.viewholder.AppDetailViewHolder;

public class AppListFragment extends Fragment {

    public interface Listener {
        void onLoadComplete(Page page, int position, int appCount);
    }

    public interface ScrollListener {
        void onScroll(Direction direction);
    }

    public interface ItemSelectedListener extends AppDetailViewHolder.ItemSelectedListener{}

    private static final String EXTRA_PAGE = "page";
    private static final String EXTRA_POSITION = "position";
    private Page mPage;
    private AppDetailAdapter mAppDetailAdapter;
    private boolean mLoading = true;
    private int mPosition;
    private Optional<Listener> mListener;
    private Optional<ScrollListener> mScrollListener;
    private ItemSelectedListener mItemSelectedListener;
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
        mView.recyclerAppDetails.setOnScrollChangeListener(this::dispatchScroll);

        populateList();
        handleDisplayView();

        return mView.getRoot();
    }

    private void dispatchScroll(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY){
        if (scrollY > oldScrollY) {
            mScrollListener.ifPresent(l -> l.onScroll(Direction.DOWN));
        } else if (scrollY < oldScrollY) {
            mScrollListener.ifPresent(l -> l.onScroll(Direction.UP));
        }
    }

    public void setupAdapter() {
        mAppDetailAdapter = new AppDetailAdapter(new ArrayList<AppDetail>(), mPage);
        mAppDetailAdapter.onSave(appDetail -> appDetail.save(getActivity()));
        mAppDetailAdapter.onDelete(appDetail -> appDetail.delete(getActivity()));
        mAppDetailAdapter.onItemSelected(mItemSelectedListener);
    }

    public void setListener(Listener listener) {
        mListener = Optional.of(listener);
    }

    public void setScrollListener(ScrollListener scrollListener) {
        mScrollListener = Optional.of(scrollListener);
    }

    public void onItemSelected(ItemSelectedListener listener){
        mItemSelectedListener = listener;
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

            mListener.ifPresent(listener -> listener.onLoadComplete(mPage, mPosition, mAppDetailAdapter.getItemCount()));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Notify.error(getActivity(), R.string.error_database_read_failed);
        }
    }
}
