package io.smileyjoe.applist.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Optional;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.databinding.ActivitySaveAppBinding;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.Notify;

public class SaveAppActivity extends BaseActivity {

    private static final String EXTRA_APP_DETAIL = "app_details";

    private boolean mFromShare = false;
    private ProgressDialog mProgressDialog;
    private ActivitySaveAppBinding mView;
    private AppDetail mAppDetail;

    public static Intent getIntent(Context context) {
        return new Intent(context, SaveAppActivity.class);
    }

    public static Intent getIntent(Context context, AppDetail appDetail) {
        Intent intent = new Intent(context, SaveAppActivity.class);
        intent.putExtra(EXTRA_APP_DETAIL, appDetail);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = ActivitySaveAppBinding.inflate(getLayoutInflater());
        setContentView(mView.getRoot());

        mView.buttonSave.setOnClickListener(v -> saveApp());

        setSupportActionBar(mView.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boolean fromSend = handleSendIntent();

        if(!fromSend){
            handleExtras();
        }
    }

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EXTRA_APP_DETAIL)){
                mAppDetail = extras.getParcelable(EXTRA_APP_DETAIL);
            }
        }

        if(mAppDetail != null){
            mView.inputName.getEditText().setText(mAppDetail.getName());
            mView.inputPackage.getEditText().setText(mAppDetail.getPackage());
            mView.inputPackage.setEnabled(false);
            mView.switchFavourite.setChecked(mAppDetail.isFavourite());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (mFromShare) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSaveProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }

        mProgressDialog.setMessage(getString(R.string.progress_saving));
        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.hide();
        }
    }

    private void saveApp() {
        removeFocus();
        String packageName = mView.inputPackage.getEditText().getText().toString();
        String appName = mView.inputName.getEditText().getText().toString();
        boolean showError = false;

        if (TextUtils.isEmpty(packageName)) {
            showError = true;
            mView.inputPackage.setError(getString(R.string.error_invalid_package));
        }

        if (TextUtils.isEmpty(appName)) {
            showError = true;
            mView.inputName.setError(getString(R.string.error_invalid_app_name));
        }

        if (showError) {
            Notify.error(this, R.string.error_invalid_fields);
        } else {
            showSaveProgress();
            if(mAppDetail == null){
                mAppDetail = new AppDetail();
            }
            mAppDetail.setName(appName);
            mAppDetail.setPackage(packageName);
            mAppDetail.isFavourite(mView.switchFavourite.isChecked());
            mAppDetail.save(this, Optional.of((error, ref) -> {
                hideProgress();
                if(error == null){
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Notify.error(this, R.string.error_generic);
                }
            }));
        }
    }

    private void removeFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getApplicationWindowToken(), 0);
    }

    private boolean handleSendIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

                if (!TextUtils.isEmpty(sharedText)) {
                    Uri uri = Uri.parse(sharedText);
                    String id = uri.getQueryParameter("id");

                    if (!TextUtils.isEmpty(id)) {
                        mFromShare = true;
                        mView.inputPackage.getEditText().setText(id);
                        mView.inputPackage.setEnabled(false);
                        return true;
                    } else {
                        Notify.error(this, getString(R.string.error_invalid_url, sharedText), new Notify.FinishOnClick(this));
                    }
                }
            }
        }

        return false;
    }
}
