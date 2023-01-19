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
import io.smileyjoe.applist.`object`.AppDetail
import io.smileyjoe.applist.databinding.ActivitySaveAppBinding
import io.smileyjoe.applist.extensions.Compat.getParcelableCompat
import io.smileyjoe.applist.util.Notify
import io.smileyjoe.applist.util.ThemeUtil


class SaveAppActivity : BaseActivity() {

    companion object {
        const val EXTRA_APP_DETAIL: String = "app_details"

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, SaveAppActivity::class.java)
        }

        @JvmStatic
        fun getIntent(context: Context, appDetail: AppDetail): Intent {
            var intent = Intent(context, SaveAppActivity::class.java)
            intent.putExtra(EXTRA_APP_DETAIL, appDetail)
            return intent
        }
    }

    var fromShare: Boolean = false
    var appDetail: AppDetail = AppDetail()
    var progressDialog: ProgressDialog? = null
    lateinit var view: ActivitySaveAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(android.R.id.content).transitionName = "transition_fab"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 1000L
            startContainerColor = ThemeUtil.getColor(this@SaveAppActivity, R.attr.colorPrimaryContainer)
            endContainerColor = ThemeUtil.getColor(this@SaveAppActivity, R.attr.colorSurface)
            addListener(
                    onStart = { view.buttonSave.hide() },
                    onEnd = { view.buttonSave.show() }
            )
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 1000L
            startContainerColor = ThemeUtil.getColor(this@SaveAppActivity, R.attr.colorSurface)
            endContainerColor = ThemeUtil.getColor(this@SaveAppActivity, R.attr.colorPrimaryContainer)
            addListener(
                    onStart = { view.buttonSave.show() }
            )
        }

        super.onCreate(savedInstanceState)
        window.allowEnterTransitionOverlap = true
        view = ActivitySaveAppBinding.inflate(layoutInflater)
        setContentView(view.root)

        view.buttonSave.setOnClickListener { saveApp() }

        setSupportActionBar(view.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        handleShareIntent()

        if (!fromShare) {
            handleExtras()
        }
    }

    private fun handleShareIntent() {
        if (Intent.ACTION_SEND == intent.action
                && intent.type != null
                && "text/plain" == intent.type) {

            var sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)

            if (!sharedText.isNullOrEmpty()) {
                Uri.parse(sharedText).getQueryParameter("id")?.let { id ->
                    fromShare = true
                    view.inputPackage.editText?.setText(id)
                    view.inputPackage.isEnabled = false
                } ?: run {
                    Notify.error(this, getString(R.string.error_invalid_url, sharedText), Notify.FinishOnClick(this))
                }
            }
        }
    }

    private fun handleExtras() {
        intent.extras?.let { extras ->
            if (extras.containsKey(EXTRA_APP_DETAIL)) {
                appDetail = extras.getParcelableCompat(EXTRA_APP_DETAIL, AppDetail::class.java)
            }
        }

        appDetail?.let { app ->
            view.inputName.editText?.setText(app.name)
            view.inputPackage.editText?.setText(app.appPackage)
            view.inputPackage.isEnabled = false
            view.switchFavourite.isChecked = app.isFavourite
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.getParentActivityIntent(this)?.let { upIntent ->
                    if (fromShare) {
                        TaskStackBuilder.create(this)
                                .addNextIntentWithParentStack(upIntent)
                                .startActivities()
                    } else {
                        onBackPressed()
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSaveProgress() {
        progressDialog = progressDialog ?: ProgressDialog(this)
        progressDialog?.setMessage(getString(R.string.progress_saving))
        progressDialog?.show()
    }

    private fun hideProgress() {
        progressDialog?.show()
    }

    private fun saveApp() {
        removeFocus()
        var packageName = view.inputPackage.editText?.text.toString()
        var appName = view.inputName.editText?.text.toString()
        var showError = false

        if (packageName.isNullOrEmpty()) {
            showError = true
            view.inputPackage.error = getString(R.string.error_invalid_package)
        }

        if (appName.isNullOrEmpty()) {
            showError = true
            view.inputName.error = getString(R.string.error_invalid_app_name)
        }

        if (showError) {
            Notify.error(this, R.string.error_invalid_fields)
        } else {
            showSaveProgress()
            appDetail.apply {
                name = appName
                appPackage = packageName
                isFavourite = view.switchFavourite.isChecked
            }
            appDetail.save(this) { error, ref ->
                hideProgress()
                if (error == null) {
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Notify.error(this, R.string.error_generic)
                }
            }
        }
    }

    private fun removeFocus() {
        var imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.currentFocus?.applicationWindowToken, 0)
    }

}