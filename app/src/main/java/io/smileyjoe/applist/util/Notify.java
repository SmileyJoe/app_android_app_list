package io.smileyjoe.applist.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;

import io.smileyjoe.applist.R;

/**
 * Created by cody on 2017/05/14.
 */

public class Notify {

    public static void confirm(Activity activity, int messageResId, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_title_confirm)
                .setPositiveButton(R.string.button_yes, listener)
                .setNegativeButton(R.string.button_no, null)
                .setMessage(messageResId)
                .create();

        dialog.show();
    }

    public static void error(Activity activity, int messageResId) {
        error(activity, messageResId, null);
    }

    public static void error(Activity activity, int messageResId, DialogInterface.OnClickListener listener) {
        error(activity, activity.getString(messageResId), listener);
    }

    public static void error(Activity activity, String message) {
        error(activity, message, null);
    }

    public static void error(Activity activity, String message, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_title_error)
                .setPositiveButton(R.string.button_ok, listener)
                .setMessage(message)
                .create();

        dialog.show();
    }

    public static void success(CoordinatorLayout coordinatorLayout, int messageId) {
        if (coordinatorLayout != null) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, coordinatorLayout.getContext().getString(messageId), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public static class FinishOnClick implements DialogInterface.OnClickListener {
        private Activity mActivity;

        public FinishOnClick(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (mActivity != null) {
                        mActivity.finish();
                    }
                    break;
            }
        }
    }

}
