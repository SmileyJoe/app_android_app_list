package io.smileyjoe.applist.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.smileyjoe.applist.BuildConfig
import io.smileyjoe.applist.R
import io.smileyjoe.applist.`object`.User
import io.smileyjoe.applist.databinding.ActivitySignInBinding
import io.smileyjoe.applist.util.Notify
import za.co.smileyjoedev.firebaseauth.google.GoogleAuth

class SignInActivity : BaseActivity() {

    companion object {
        const val EXTRA_RETURN_INTENT: String = "return_intent"

        @JvmStatic
        fun getIntent(context: Context, returnIntent: Intent): Intent {
            var intent = Intent(context, SignInActivity::class.java)
            intent.putExtra(EXTRA_RETURN_INTENT, returnIntent)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    private lateinit var googleAuth: GoogleAuth
    private var returnIntent: Intent? = null
    private lateinit var view: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // We only have to use this activity if there is no current user //
        if (User.getCurrent() != null) {
            return
        }

        view = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(view.root)
        handleExtras()
        googleAuth = GoogleAuth.Builder.with(this)
                .on(view.buttonGoogleSignIn)
                .serverClientId(BuildConfig.SERVER_CLIENT_ID)
                .onFail { checkSignIn(true) }
                .onLogIn { checkSignIn(true) }
                .onLogout { Log.d("GoogleAuth", "Logout") }
                .build()
    }

    private fun handleExtras() {
        intent.extras?.let { extras ->
            if (extras.containsKey(EXTRA_RETURN_INTENT)) {
                returnIntent = getParcelable(extras, EXTRA_RETURN_INTENT, Intent::class.java)
            }
        }
    }

    override fun checkSignIn() {
        checkSignIn(false)
    }

    private fun checkSignIn(showError: Boolean) {
        if (User.getCurrent() != null) {
            if (returnIntent == null) {
                startActivity(MainActivity.getIntent(baseContext))
            } else {
                startActivity(returnIntent)
            }
            finish()
        } else if (showError) {
            Notify.error(this, R.string.error_sign_in)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var consumed: Boolean = googleAuth.onActivityResult(requestCode, resultCode, data)

        if (!consumed) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStart() {
        super.onStart()
        googleAuth.onStart()
    }

    override fun onStop() {
        super.onStop()
        googleAuth.onStop()
    }

}