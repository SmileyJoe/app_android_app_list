package io.smileyjoe.applist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.smileyjoe.applist.BuildConfig;
import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.User;
import io.smileyjoe.applist.util.Notify;
import za.co.smileyjoedev.firebaseauth.google.Google;
import za.co.smileyjoedev.firebaseauth.google.GoogleLoginListener;
import za.co.smileyjoedev.lib.debug.Debug;

/**
 * Created by cody on 2017/04/14.
 */

public class SignInActivity extends BaseActivity implements GoogleLoginListener {

    private Google mGoogle;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mGoogle = new Google(this, findViewById(R.id.button_google_sign_in), BuildConfig.SERVER_CLIENT_ID, this);
    }

    @Override
    protected void checkSignIn() {
        checkSignIn(false);
    }

    private void checkSignIn(boolean showError) {
        if (User.getCurrent() != null) {
            startActivity(MainActivity.getIntent(getBaseContext()));
            finish();
        } else if (showError) {
            Notify.error(this, R.string.error_sign_in);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean consumed = mGoogle.onActivityResult(requestCode, resultCode, data);

        if (!consumed) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogle.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogle.onStop();
    }

    @Override
    public void onLogIn() {
        checkSignIn(true);
    }

    @Override
    public void onLogOut() {
        Debug.d("SignInThings", "OnLogOut");
    }

    @Override
    public void onLogInFail() {
        checkSignIn(true);
    }
}
