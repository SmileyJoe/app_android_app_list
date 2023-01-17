package io.smileyjoe.applist.util

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import io.smileyjoe.applist.R

object Notify {

    fun confirm(activity: Activity, @StringRes message: Int, listener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(activity).apply {
            setTitle(R.string.dialog_title_confirm)
            setPositiveButton(R.string.button_yes, listener)
            setNegativeButton(R.string.button_no, null)
            setMessage(message)
        }.create().show()
    }

    fun error(activity: Activity, @StringRes message: Int) {
        error(activity, message, null)
    }

    fun error(activity: Activity, @StringRes message: Int, listener: DialogInterface.OnClickListener?) {
        error(activity, activity.getString(message), listener)
    }

    fun error(activity: Activity, message: String) {
        error(activity, message, null)
    }

    fun error(activity: Activity, message: String, listener: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(activity).apply {
            setTitle(R.string.dialog_title_error)
            setPositiveButton(R.string.button_ok, listener)
            setMessage(message)
        }.create().show()
    }

    fun success(view: View?, @StringRes message: Int) {
        if (view != null) {
            Snackbar.make(view, view.context.getString(message), Snackbar.LENGTH_LONG).show()
        }
    }

    class FinishOnClick(var activity: Activity?) : DialogInterface.OnClickListener {
        override fun onClick(dialogInterface: DialogInterface?, i: Int) {
            when (i) {
                DialogInterface.BUTTON_POSITIVE -> activity?.finish()
            }
        }
    }

}