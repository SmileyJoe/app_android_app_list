package za.co.smileyjoedev.firebaseauth.google;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.InvalidParameterException;

import za.co.smileyjoedev.firebaseauth.R;

public class GoogleAuth {

    public interface LogInListener{
        void onLogIn();
    }

    public interface LogoutListener {
        void onLogOut();
    }

    public interface LogInFailListener {
        void onLogInFail();
    }

    private static final int REQUEST_SIGN_IN = 100;
    private Builder mBuilder;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private GoogleSignInClient mSignInClient;

    private GoogleAuth(Builder builder){
        mBuilder = builder;

        mAuth = FirebaseAuth.getInstance();
        setupProgressDialog();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(builder.serverClientId)
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(builder.activity, options);

        builder.clickableView.setOnClickListener(v -> {
            showProgress();
            signIn();
        });
    }

    private void signIn() {
        Intent intent = mSignInClient.getSignInIntent();
        mBuilder.activity.startActivityForResult(intent, REQUEST_SIGN_IN);
    }

    private void setupProgressDialog(){
        mProgressDialog = new ProgressDialog(mBuilder.activity);
        mProgressDialog.setMessage(mBuilder.activity.getString(R.string.progress_sign_in));
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

    public void onStart() {
        mAuth.addAuthStateListener(this::onAuthStateChanged);
    }

    public void onStop() {
        mAuth.removeAuthStateListener(this::onAuthStateChanged);
    }

    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            mBuilder.logInListener.onLogIn();
        } else {
            mBuilder.logoutListener.onLogOut();
        }
    }

    public void onFail(){
        mBuilder.logInFailListener.onLogInFail();
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGN_IN) {
            Task<GoogleSignInAccount> taskAccount = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (taskAccount.isSuccessful()) {
                GoogleSignInAccount account = taskAccount.getResult();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(mBuilder.activity, task -> {
                            hideProgress();
                            if (!task.isSuccessful()) {
                                onFail();
                            }
                        });
            } else {
                hideProgress();
                onFail();
            }
            return true;
        } else {
            return false;
        }
    }

    public static class Builder {
        private FragmentActivity activity;
        private View clickableView;
        private String serverClientId;
        private LogInListener logInListener;
        private LogoutListener logoutListener;
        private LogInFailListener logInFailListener;

        private Builder(FragmentActivity activity){
            this.activity = activity;
        }

        public static Builder with(FragmentActivity activity){
            return new Builder(activity);
        }

        public Builder on(View view){
            clickableView = view;
            return this;
        }

        public Builder serverClientId(String serverClientId){
            this.serverClientId = serverClientId;
            return this;
        }

        public Builder onLogIn(LogInListener listener){
            logInListener = listener;
            return this;
        }

        public Builder onLogout(LogoutListener listener){
            logoutListener = listener;
            return this;
        }

        public Builder onFail(LogInFailListener listener){
            logInFailListener = listener;
            return this;
        }

        private void validate() throws InvalidParameterException{
            String message = "";

            message = validate(message, clickableView, "Please add a view to click");
            message = validate(message, serverClientId, "Please add a server client id");
            message = validate(message, logInListener, "Please add a log in listener");
            message = validate(message, logoutListener, "Please add a logout listener");
            message = validate(message, logInFailListener, "Please add an error listener");

            if(!TextUtils.isEmpty(message)){
                throw new InvalidParameterException(message);
            }
        }

        private String validate(String message, Object object, String error){
            if(object == null){
                message += "\n- " + error;
            }

            return message;
        }

        public GoogleAuth build(){
            try{
                validate();
                return new GoogleAuth(this);
            } catch (InvalidParameterException e){
                throw e;
            }
        }
    }

}
