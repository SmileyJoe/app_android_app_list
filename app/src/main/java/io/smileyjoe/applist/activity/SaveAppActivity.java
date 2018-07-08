package io.smileyjoe.applist.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.DbCompletionListener;
import io.smileyjoe.applist.util.Notify;

/**
 * Created by cody on 2018/07/08.
 */

public class SaveAppActivity extends BaseActivity {

    private TextInputLayout mInputPackage;
    private TextInputLayout mInputName;
    private boolean mFromShare = false;
    private ProgressDialog mProgressDialog;

    public static Intent getIntent(Context context) {
        return new Intent(context, SaveAppActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_app);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mInputPackage = (TextInputLayout) findViewById(R.id.input_package);
        mInputName = (TextInputLayout) findViewById(R.id.input_name);

        handleSendIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_app, menu);
        return true;
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
            case R.id.action_save:
                saveApp();
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
        String packageName = mInputPackage.getEditText().getText().toString();
        String appName = mInputName.getEditText().getText().toString();
        boolean showError = false;

        if (TextUtils.isEmpty(packageName)) {
            showError = true;
            mInputPackage.setError(getString(R.string.error_invalid_package));
        }

        if (TextUtils.isEmpty(appName)) {
            showError = true;
            mInputName.setError(getString(R.string.error_invalid_app_name));
        }

        if (showError) {
            Notify.error(this, R.string.error_invalid_fields);
        } else {
            showSaveProgress();
            AppDetail appDetail = new AppDetail();
            appDetail.setName(appName);
            appDetail.setPackage(packageName);
            appDetail.save(this, new OnSaveListener(this, appDetail));
        }
    }

    private void removeFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getApplicationWindowToken(), 0);
    }

    private void handleSendIntent() {
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
                        mInputPackage.getEditText().setText(id);
                        mInputPackage.setEnabled(false);
                    } else {
                        Notify.error(this, getString(R.string.error_invalid_url, sharedText), new Notify.FinishOnClick(this));
                    }
                }
            }
        }
    }

    private class OnSaveListener extends DbCompletionListener {

        public OnSaveListener(Activity activity, AppDetail appDetail) {
            super(activity, appDetail);
        }

        @Override
        protected void onSuccess() {
            hideProgress();
            setResult(RESULT_OK);
            finish();
        }
    }
}
