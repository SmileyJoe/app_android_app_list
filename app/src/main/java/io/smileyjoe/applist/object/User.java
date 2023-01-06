package io.smileyjoe.applist.object;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {

    private String mId;
    private String mName;
    private String mEmail;
    private Uri mProfileImage;

    public static User getCurrent() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            User user = new User();
            user.setId(firebaseUser.getUid());
            user.setEmail(firebaseUser.getEmail());
            user.setName(firebaseUser.getDisplayName());
            user.setProfileImage(firebaseUser.getPhotoUrl());
            return user;
        } else {
            return null;
        }
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public Uri getProfileImage() {
        return mProfileImage;
    }

    public void setProfileImage(Uri profileImage) {
        mProfileImage = profileImage;
    }
}
