package io.smileyjoe.applist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import io.smileyjoe.applist.BuildConfig;
import io.smileyjoe.applist.R;
import io.smileyjoe.applist.databinding.ActivitySignInBinding;
import io.smileyjoe.applist.object.User;
import io.smileyjoe.applist.util.Notify;
import za.co.smileyjoedev.firebaseauth.google.GoogleAuth;

public class SignInActivity extends BaseActivity {

    private static final String EXTRA_RETURN_INTENT = "return_intent";

    private GoogleAuth mGoogleAuth;
    private Intent mReturnIntent;
    private ActivitySignInBinding mView;

    public static Intent getIntent(Context context, Intent returnIntent) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.putExtra(EXTRA_RETURN_INTENT, returnIntent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We only have to use this activity if there is no current user //
        if (User.getCurrent() != null) {
            return;
        }

        mView = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(mView.getRoot());
        handleExtras();
        mGoogleAuth = GoogleAuth.Builder.with(this)
                .on(mView.buttonGoogleSignIn)
                .serverClientId(BuildConfig.SERVER_CLIENT_ID)
                .onFail(() -> checkSignIn(true))
                .onLogIn(() -> checkSignIn(true))
                .onLogout(() -> Log.d("GoogleAuth", "Logout"))
                .build();
    }

    private void handleExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(EXTRA_RETURN_INTENT)) {
                mReturnIntent = extras.getParcelable(EXTRA_RETURN_INTENT);
            }
        }
    }

    @Override
    protected void checkSignIn() {
        checkSignIn(false);
    }

    private void checkSignIn(boolean showError) {
        if (User.getCurrent() != null) {
            if (mReturnIntent == null) {
                startActivity(MainActivity.getIntent(getBaseContext()));
            } else {
                startActivity(mReturnIntent);
            }

            finish();
        } else if (showError) {
            Notify.error(this, R.string.error_sign_in);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean consumed = mGoogleAuth.onActivityResult(requestCode, resultCode, data);

        if (!consumed) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleAuth.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleAuth.onStop();
    }
}
