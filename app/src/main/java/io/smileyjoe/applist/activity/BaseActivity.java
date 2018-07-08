package io.smileyjoe.applist.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.smileyjoe.applist.object.User;

/**
 * Created by cody on 2018/07/05.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSignIn();
    }

    protected void checkSignIn() {
        if (User.getCurrent() == null) {
            startActivity(SignInActivity.getIntent(getBaseContext(), getIntent()));
            finish();
        }
    }
}
