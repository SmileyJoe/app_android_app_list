package za.co.smileyjoedev.firebaseauth.google;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import za.co.smileyjoedev.firebaseauth.BuildConfig;
import za.co.smileyjoedev.firebaseauth.R;

/**
 * Created by cody on 2017/04/09.
 */

public class Google implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, FirebaseAuth.AuthStateListener{

    private static final int RC_SIGN_IN = 100;
    private final GoogleApiClient mGoogleApiClient;
    private View mClickView;
    private FragmentActivity mActivity;
    private GoogleLoginListener mListener;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    public Google(FragmentActivity activity, View clickableView, String serverClientId, GoogleLoginListener listener){
        mActivity = activity;
        mListener = listener;
        mAuth = FirebaseAuth.getInstance();
        setupProgressDialog();
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientId)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(activity.getBaseContext())
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        clickableView.setOnClickListener(this);
    }

    private void setupProgressDialog(){
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage(mActivity.getString(R.string.progress_sign_in));
        mProgressDialog.setIndeterminate(true);
    }

    private void showProgress(){
        if(mProgressDialog != null && !mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
    }

    private void hideProgress(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        showProgress();
        signIn();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            if(mListener != null){
                mListener.onLogIn();
            }
        } else {
            if(mListener != null){
                mListener.onLogOut();
            }
        }
    }

    public void onStart() {
        mAuth.addAuthStateListener(this);
    }

    public void onStop() {
        mAuth.removeAuthStateListener(this);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i("Google", "Sign in result: " + result.toString());
        GoogleSignInAccount account = result.getSignInAccount();
        Log.i("Google", "Account: " + account.toString());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if(mListener != null){
                                mListener.onLogInFail();
                            }
                        }
                    }
                });
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            hideProgress();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            return true;
        } else {
            return false;
        }
    }
}
