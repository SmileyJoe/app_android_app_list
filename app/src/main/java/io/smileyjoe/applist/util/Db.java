package io.smileyjoe.applist.util;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.smileyjoe.applist.BuildConfig;
import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.User;

/**
 * Created by cody on 2018/07/07.
 */

public class Db {

    private static final String DB_KEY_APP_DETAIL = BuildConfig.DEBUG ? "app-debug" : "app";

    private Db() {
    }

    private static DatabaseReference getReference(Activity activity) {
        User user = User.getCurrent();

        if (user != null) {
            return FirebaseDatabase.getInstance().getReference().child(user.getId());
        } else {
            Notify.error(activity, R.string.error_not_signed_in);
            return null;
        }
    }

    public static DatabaseReference getDetailReference(Activity activity) {
        DatabaseReference databaseReference = getReference(activity);

        if (databaseReference != null) {
            return databaseReference.child(DB_KEY_APP_DETAIL);
        } else {
            return null;
        }
    }

}
