package io.smileyjoe.applist.util;

import android.app.Activity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.view.ButtonProgress;
import io.smileyjoe.applist.viewholder.AppDetailViewHolder;

/**
 * Created by cody on 2018/07/07.
 */

public abstract class DbCompletionListener implements DatabaseReference.CompletionListener {

    private Activity mActivity;
    private AppDetailViewHolder mViewHolder;
    private AppDetail mAppDetail;
    private DatabaseError mDatabaseError;
    private DatabaseReference mDatabaseReference;

    protected abstract void onSuccess();

    public DbCompletionListener(Activity activity, AppDetail appDetail) {
        this(activity, null, appDetail);
    }

    public DbCompletionListener(Activity activity, AppDetailViewHolder viewHolder, AppDetail appDetail) {
        mActivity = activity;
        mViewHolder = viewHolder;
        mAppDetail = appDetail;
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError == null) {
            onSuccess();
        } else {
            onFail();
        }
    }

    protected void onFail() {
        Notify.error(mActivity, R.string.error_generic);
    }

    public AppDetailViewHolder getViewHolder() {
        return mViewHolder;
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
