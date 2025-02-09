package io.smileyjoe.applist.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.core.transition.addListener
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.ActivitySaveAppBinding
import io.smileyjoe.applist.extensions.Compat.getParcelableCompat
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.util.Notify
import io.smileyjoe.applist.util.ThemeUtil
import io.smileyjoe.applist.view.TagInputEditText

/**
 * Save or edit an app
 * </p>
 * This can also be opened from a share intent from the play store, as sort of a wishlist situation
 */
class SaveAppActivity : BaseActivity() {

    companion object {
        const val EXTRA_APP_DETAIL: String = "app_details"

        /**
         * Get an intent for this activity, pass in an [appDetail] to edit the details
         *
         * @param context current context
         * @param appDetail details to edit, null to create a new app
         * @return intent to start the activity with
         */
        fun getIntent(context: Context, appDetail: AppDetail? = null) =
            Intent(context, SaveAppActivity::class.java).apply {
                putExtra(EXTRA_APP_DETAIL, appDetail)
            }
    }

    var fromShare: Boolean = false
    var appDetail: AppDetail? = null
    var progressDialog: ProgressDialog? = null
    val binding by lazy {
        ActivitySaveAppBinding.inflate(layoutInflater)
    }

    /**
     * The activity expands in from the from the fab, and minimizes into the fab on close,
     * handle all those things here.
     */
    private fun handleTransitionAnimation() {
        findViewById<View>(android.R.id.content).transitionName = "transition_fab"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 1000L
            startContainerColor =
                ThemeUtil.getColor(this@SaveAppActivity, R.attr.colorPrimaryContainer)
            endContainerColor = ThemeUtil.getColor(this@SaveAppActivity, R.attr.colorSurface)
            addListener(
                onStart = { binding.buttonSave.hide() },
                onEnd = { binding.buttonSave.show() }
            )
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 1000L
            startContainerColor = ThemeUtil.getColor(this@SaveAppActivity, R.attr.colorSurface)
            endContainerColor =
                ThemeUtil.getColor(this@SaveAppActivity, R.attr.colorPrimaryContainer)
            addListener(
                onStart = { binding.buttonSave.show() }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        handleTransitionAnimation()
        super.onCreate(savedInstanceState)
        window.allowEnterTransitionOverlap = true
        setContentView(binding.root)

        binding.buttonSave.setOnClickListener { saveApp() }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        handleShareIntent()

        if (!fromShare) {
            handleExtras()
        }
    }

    /**
     * The user can share an app from the play store to save it, even if it isn't installed,
     * as a sort of wishlist. If the activity is opened from the share intent, there is some
     * information we can get, like the package name.
     */
    private fun handleShareIntent() {
        // check if we are coming from a share intent //
        if (Intent.ACTION_SEND == intent.action
            && intent.type != null
            && "text/plain" == intent.type
        ) {

            // get the text that was shared, this will just be the url //
            val sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)

            // if we have text, get the id, which will be the app package //
            if (!sharedText.isNullOrEmpty()) {
                Uri.parse(sharedText).getQueryParameter("id")?.let { id ->
                    fromShare = true
                    binding.inputPackage.editText?.setText(id)
                    binding.inputPackage.isEnabled = false
                } ?: run {
                    // text was probably not shared from the play store //
                    Notify.error(
                        this,
                        getString(R.string.error_invalid_url, sharedText),
                        Notify.FinishOnClick(this)
                    )
                }
            }
        }
    }

    /**
     * Check if an app was passed in to be edited, populate the inputs if one has
     */
    private fun handleExtras() {
        intent.extras?.let { extras ->
            if (extras.containsKey(EXTRA_APP_DETAIL)) {
                appDetail = extras.getParcelableCompat(EXTRA_APP_DETAIL, AppDetail::class.java)
            }
        }

        appDetail?.let { app ->
            binding.apply {
                inputName.editText?.setText(app.name)
                inputPackage.editText?.setText(app.appPackage)
                inputPackage.isEnabled = app.appPackage.isNullOrEmpty()
                switchFavourite.isChecked = app.isFavourite
                inputNote.editText?.setText(app.notes)
                (inputTag.editText as TagInputEditText).tags = app.tags
            }
        }
    }

    /**
     * Handle the home button being clicked.
     * </p>
     * This activity can be opened from a share intent, in which case there will be
     * no parent activity in the stack, if that is the case, create the activity and show it
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // get the parent activity set in the manifest //
                NavUtils.getParentActivityIntent(this)?.let { upIntent ->
                    // if this is from a share, that will need to be created and started //
                    if (fromShare) {
                        TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities()
                    } else {
                        // if not, we can just simulate a back press //
                        onBackPressed()
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Show a saving dialog
     */
    private fun showSaveProgress() {
        progressDialog = progressDialog ?: ProgressDialog(this)
        progressDialog!!.apply {
            setMessage(getString(R.string.progress_saving))
            show()
        }
    }

    /**
     * Hide the progress if it is showing
     */
    private fun hideProgress() {
        progressDialog?.show()
    }

    /**
     * Save the app to firebase
     */
    private fun saveApp() {
        // remove the focus so the keyboard closes //
        removeFocus()
        val packageName = binding.inputPackage.editText?.text.toString()
        val appName = binding.inputName.editText?.text.toString()
        var showError = false

        // make sure certain data is entered //
        if (packageName.isNullOrEmpty()) {
            showError = true
            binding.inputPackage.error = getString(R.string.error_invalid_package)
        }

        if (appName.isNullOrEmpty()) {
            showError = true
            binding.inputName.error = getString(R.string.error_invalid_app_name)
        }

        // if not everything required is entered, show an error dialog //
        if (showError) {
            Notify.error(this, R.string.error_invalid_fields)
        } else {
            showSaveProgress()

            // if there is no details, instantiate it //
            appDetail = appDetail ?: AppDetail()

            // we now know it is not null //
            appDetail!!.apply {
                name = appName
                appPackage = packageName
                isFavourite = binding.switchFavourite.isChecked
                notes = binding.inputNote.editText?.text.toString()
                tags = (binding.inputTag.editText as TagInputEditText).tags
            }
            appDetail!!.db.save(this) { error, ref ->
                hideProgress()
                if (error == null) {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(EXTRA_APP_DETAIL, appDetail)
                    })
                    finish()
                } else {
                    Notify.error(this, R.string.error_generic)
                }
            }
        }
    }

    /**
     * Remove the focus of whatever input is in focus, this will close the keyboard
     */
    private fun removeFocus() {
        var imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.currentFocus?.applicationWindowToken, 0)
    }

}