package io.smileyjoe.applist.util;

import android.app.Activity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.view.ButtonProgress;

/**
 * Created by cody on 2018/07/07.
 */

public abstract class DbCompletionListener implements DatabaseReference.CompletionListener {

    private Activity mActivity;
    private ButtonProgress mButtonProgress;
    private AppDetail mAppDetail;
    private DatabaseError mDatabaseError;
    private DatabaseReference mDatabaseReference;

    protected abstract void onSuccess();

    public DbCompletionListener(Activity activity, ButtonProgress buttonProgress, AppDetail appDetail) {
        mActivity = activity;
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
        Notify.error(mActivity, R.string.error_generic);
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
