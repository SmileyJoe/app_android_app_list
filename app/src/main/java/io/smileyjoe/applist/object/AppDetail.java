package io.smileyjoe.applist.object;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.util.Db;
import io.smileyjoe.applist.util.Notify;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetail {

    private static final String DB_KEY_NAME = "name";
    private static final String DB_KEY_PACKAGE = "package";

    private String mName;
    private String mPackage;
    private String mFirebaseKey;
    private Intent mLaunchActivity;
    private Drawable mIcon;
    private boolean mSaved;

    public AppDetail(){}

    public AppDetail(DataSnapshot dataSnapshot){
        if(dataSnapshot != null){
            setFirebaseKey(dataSnapshot.getKey());
            setSaved(true);

            if(dataSnapshot.hasChild(DB_KEY_NAME)){
                setName(dataSnapshot.child(DB_KEY_NAME).getValue(String.class));
            }

            if(dataSnapshot.hasChild(DB_KEY_PACKAGE)){
                setPackage(dataSnapshot.child(DB_KEY_PACKAGE).getValue(String.class));
            }
        }
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPackage(String aPackage) {
        mPackage = aPackage;
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

    public String getName() {
        return mName;
    }

    public String getPackage() {
        return mPackage;
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

    private String getFirebaseKey(DatabaseReference databaseReference){
        if(TextUtils.isEmpty(mFirebaseKey)){
            setFirebaseKey(databaseReference.push().getKey());
        }

        return getFirebaseKey();
    }

    public String getPlayStoreLink(){
        return "http://play.google.com/store/apps/details?id=" + getPackage();
    }

    public boolean save(Activity activity, DatabaseReference.CompletionListener listener){
        DatabaseReference databaseReference = Db.getDetailReference(activity);

        if(databaseReference != null){
            HashMap<String, Object> data = new HashMap<>();
            data.put(DB_KEY_NAME, getName());
            data.put(DB_KEY_PACKAGE, getPackage());

            databaseReference
                    .child(getFirebaseKey(databaseReference))
                    .updateChildren(data, listener);

            return true;
        } else {
            return false;
        }
    }

    public boolean delete(Activity activity, DatabaseReference.CompletionListener listener){
        DatabaseReference databaseReference = Db.getDetailReference(activity);

        if(databaseReference != null){
            String firebaseKey = getFirebaseKey();
            if(!TextUtils.isEmpty(firebaseKey)){
                databaseReference.child(firebaseKey).removeValue(listener);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "AppDetail{" +
                "name='" + getName() + '\'' +
                ", package='" + getPackage() + '\'' +
                ", launchActivity=" + (getLaunchActivity() != null) +
                ", icon=" + (getIcon() != null) +
                '}';
    }
}
