package io.smileyjoe.applist.object;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.util.Db;
import io.smileyjoe.applist.util.Icon;
import io.smileyjoe.applist.util.Notify;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetail {

    private static final String DB_KEY_NAME = "name";
    private static final String DB_KEY_PACKAGE = "package";
    private static final String DB_KEY_IS_FAVOURITE = "is_favourite";

    private String mName;
    private String mPackage;
    private boolean mIsFavourite = false;
    private String mFirebaseKey;
    private Intent mLaunchActivity;
    private Drawable mIcon;
    private boolean mSaved = false;
    private boolean mInstalled = false;

    public AppDetail() {
    }

    public AppDetail(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            setFirebaseKey(dataSnapshot.getKey());
            setSaved(true);

            if (dataSnapshot.hasChild(DB_KEY_NAME)) {
                setName(dataSnapshot.child(DB_KEY_NAME).getValue(String.class));
            }

            if (dataSnapshot.hasChild(DB_KEY_PACKAGE)) {
                setPackage(dataSnapshot.child(DB_KEY_PACKAGE).getValue(String.class));
            }

            if(dataSnapshot.hasChild(DB_KEY_IS_FAVOURITE)){
                isFavourite(dataSnapshot.child(DB_KEY_IS_FAVOURITE).getValue(Boolean.class));
            }
        }
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPackage(String aPackage) {
        mPackage = aPackage;
    }

    public void isFavourite(boolean isFavourite) {
        mIsFavourite = isFavourite;
    }

    public void setLaunchActivity(Intent launchActivity) {
        mLaunchActivity = launchActivity;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public void setSaved(boolean saved) {
        mSaved = saved;
    }

    public void setFirebaseKey(String firebaseKey) {
        mFirebaseKey = firebaseKey;
    }

    public void setInstalled(boolean installed) {
        mInstalled = installed;
    }

    public String getName() {
        return mName;
    }

    public String getPackage() {
        return mPackage;
    }

    public boolean isFavourite() {
        return mIsFavourite;
    }

    public Intent getLaunchActivity() {
        return mLaunchActivity;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public boolean isSaved() {
        return mSaved;
    }

    public String getFirebaseKey() {
        return mFirebaseKey;
    }

    private String getFirebaseKey(DatabaseReference databaseReference) {
        if (TextUtils.isEmpty(mFirebaseKey)) {
            setFirebaseKey(databaseReference.push().getKey());
        }

        return getFirebaseKey();
    }

    public boolean isInstalled() {
        return mInstalled;
    }

    public String getPlayStoreLink() {
        return "http://play.google.com/store/apps/details?id=" + getPackage();
    }

    public boolean save(Activity activity) {
        return save(activity, Optional.empty());
    }

    public boolean save(Activity activity, @NonNull Optional<DatabaseReference.CompletionListener> listener) {
        DatabaseReference databaseReference = Db.getDetailReference(activity);

        if (databaseReference != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(DB_KEY_NAME, getName());
            data.put(DB_KEY_PACKAGE, getPackage());
            data.put(DB_KEY_IS_FAVOURITE, isFavourite());

            databaseReference
                    .child(getFirebaseKey(databaseReference))
                    .updateChildren(data, ((error, ref) -> {
                        if(error == null){
                            Icon.upload(getPackage(), getIcon());
                        } else {
                            Notify.error(activity, R.string.error_generic);
                        }

                        listener.ifPresent(l -> l.onComplete(error, ref));
                    }));

            return true;
        } else {
            return false;
        }
    }

    public boolean delete(Activity activity) {
        return delete(activity, null);
    }

    public boolean delete(Activity activity, DatabaseReference.CompletionListener listener) {
        DatabaseReference databaseReference = Db.getDetailReference(activity);

        if (databaseReference != null) {
            String firebaseKey = getFirebaseKey();
            if (!TextUtils.isEmpty(firebaseKey)) {
                databaseReference.child(firebaseKey).removeValue(listener);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean onSavedUpdated(List<AppDetail> savedApps) {
        return savedApps.stream()
            .filter(app -> getPackage().equals(app.getPackage()))
            .findFirst()
            .map(app -> {
                setSaved(true);
                isFavourite(app.isFavourite());
                setFirebaseKey(app.getFirebaseKey());
                return true;
            })
            .orElseGet(() -> {
                setSaved(false);
                setFirebaseKey(null);
                return false;
            });
    }

    public boolean onInstalledUpdated(List<AppDetail> installedApps){
        return installedApps.stream()
                .filter(app -> getPackage().equals(app.getPackage()))
                .findFirst()
                .map(app -> {
                    setInstalled(true);
                    return true;
                })
                .orElseGet(() -> {
                    setInstalled(false);
                    return false;
                });
    }

    @Override
    public String toString() {
        return "AppDetail{" +
                "mName='" + mName + '\'' +
                ", mPackage='" + mPackage + '\'' +
                ", mIsFavourite=" + mIsFavourite +
                ", mFirebaseKey='" + mFirebaseKey + '\'' +
                ", mLaunchActivity=" + mLaunchActivity +
                ", mIcon=" + mIcon +
                ", mSaved=" + mSaved +
                ", mInstalled=" + mInstalled +
                '}';
    }
}
