package io.smileyjoe.applist.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.smileyjoe.applist.BuildConfig
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.ActivitySignInBinding
import io.smileyjoe.applist.extensions.Compat.getParcelableCompat
import io.smileyjoe.applist.extensions.SplashScreenExt.exitAfterAnim
import io.smileyjoe.applist.`object`.User
import io.smileyjoe.applist.util.Notify
import za.co.smileyjoedev.firebaseauth.google.GoogleAuth

class SignInActivity : BaseActivity() {

    companion object {
        /**
         * The intent that should be loaded after the user has signed in
         */
        private const val EXTRA_RETURN_INTENT: String = "return_intent"

        /**
         * Get the intent for sign in
         * </p>
         * This activity will always clear the top so it's the only activity available
         * as if the user is not signed in they can't use the app at all
         *
         * @param context current context
         * @param returnIntent the intent to start when the user signs in defaults to [MainActivity] if not provided
         * @return the intent for this activity
         */
        fun getIntent(context: Context, returnIntent: Intent? = null): Intent {
            var intent = Intent(context, SignInActivity::class.java)
            intent.putExtra(EXTRA_RETURN_INTENT, returnIntent)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    // setup in [onCreate] once the binding can be set //
    private lateinit var googleAuth: GoogleAuth

    // see [EXTRA_RETURN_INTENT] //
    private var returnIntent: Intent? = null

    // the activity ui //
    private val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        handleExtras()
        // setup the google auth
        googleAuth = GoogleAuth.Builder.with(this)
            .on(binding.buttonGoogleSignIn)
            .serverClientId(BuildConfig.SERVER_CLIENT_ID)
            .onFail { checkSignIn(true, false) }
            .onLogIn { checkSignIn(true, false) }
            .onLogout { Log.d("GoogleAuth", "Logout") }
            .build()
        // close the splash screen //
        splashScreen.exitAfterAnim()
    }

    /**
     * Get all intent data
     *
     * @see [EXTRA_RETURN_INTENT]
     */
    private fun handleExtras() {
        intent.extras?.let { extras ->
            returnIntent = extras.getParcelableCompat(EXTRA_RETURN_INTENT, Intent::class.java)
        }
    }

    /**
     * We are in the sign in activity, so overide the [BaseActivity.checkSignIn], whithout
     * this the screens would just loop
     */
    override fun checkSignIn() {
        checkSignIn(false)
    }

    /**
     * Confirm if the user has signed in and load the next activity
     *
     * @param showError whether to show a login error or not
     * @param fromSplash has the user just tried to login, or has the screen just loaded
     */
    private fun checkSignIn(showError: Boolean, fromSplash: Boolean = true) {
        if (User.current != null) {
            if (returnIntent == null) {
                startActivity(MainActivity.getIntent(baseContext, fromSplash))
            } else {
                startActivity(returnIntent)
            }
            finish()
        } else if (showError) {
            Notify.error(this, R.string.error_sign_in)
        }
    }

    /**
     * Get the result from login. This is deprecated, but the [GoogleAuth] class
     * still uses it, so it will be used until that is updated.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var consumed: Boolean = googleAuth.onActivityResult(requestCode, resultCode, data)

        if (!consumed) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Let the auth package know the activity has started
     */
    override fun onStart() {
        super.onStart()
        googleAuth.onStart()
    }

    /**
     * Let the auth package know the activity has stopped
     */
    override fun onStop() {
        super.onStop()
        googleAuth.onStop()
    }

}