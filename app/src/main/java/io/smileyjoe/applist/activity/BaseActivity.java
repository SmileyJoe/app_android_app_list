package io.smileyjoe.applist.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.smileyjoe.applist.object.User;

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
