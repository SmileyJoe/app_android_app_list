package io.smileyjoe.applist.util

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import io.smileyjoe.applist.R

/**
 * All the ways the user will be notified of something happening
 */
object Notify {

    /**
     * Ask the user to confirm something
     * </p>
     * There is only a callback for a positive click, on negative click the dialog
     * just closes and nothing happens.
     *
     * @param activity that is showing the dialog
     * @param message the message explaining what is being confirmed
     * @param listener callback for if the user clicks the positive button
     */
    fun confirm(
        activity: Activity,
        @StringRes message: Int,
        listener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(activity).apply {
            setTitle(R.string.dialog_title_confirm)
            setPositiveButton(R.string.button_yes, listener)
            setNegativeButton(R.string.button_no, null)
            setMessage(message)
        }.create().show()
    }

    /**
     * Convenience function for [error]
     */
    fun error(
        activity: Activity,
        @StringRes message: Int,
        listener: DialogInterface.OnClickListener? = null
    ) =
        error(activity, activity.getString(message), listener)

    /**
     * Alert the user to something going wrong
     *
     * @param activity that the error is showing on
     * @param message the error message
     * @param listener callback for when the user acknowledges the error
     */
    fun error(
        activity: Activity,
        message: String,
        listener: DialogInterface.OnClickListener? = null
    ) {
        AlertDialog.Builder(activity).apply {
            setTitle(R.string.dialog_title_error)
            setPositiveButton(R.string.button_ok, listener)
            setMessage(message)
        }.create().show()
    }

    /**
     * Show the user that an action was successful
     *
     * @param view that the snackbar is anchored to
     * @param message message to show
     */
    fun success(view: View?, @StringRes message: Int) {
        if (view != null) {
            Snackbar.make(view, view.context.getString(message), Snackbar.LENGTH_LONG).show()
        }
    }

    /**
     * A lot of the time we want to finish the current activity after showing a dialog,
     * this will do that.
     *
     * @param activity to finish on positive click
     */
    class FinishOnClick(var activity: Activity?) : DialogInterface.OnClickListener {
        override fun onClick(dialogInterface: DialogInterface?, i: Int) {
            when (i) {
                DialogInterface.BUTTON_POSITIVE -> activity?.finish()
            }
        }
    }

}