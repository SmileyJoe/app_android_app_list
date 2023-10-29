package io.smileyjoe.applist.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.smileyjoe.applist.`object`.User

/**
 * Base activity for all activities
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // every screen requires the user to be logged in, so we always check that //
        checkSignIn()
    }

    /**
     * Check that the user is signed in, if they are not, send them to the sign in activity
     * and [finish] the current activity.
     */
    protected open fun checkSignIn() {
        if (User.current == null) {
            startActivity(SignInActivity.getIntent(baseContext, intent))
            finish()
        }
    }

}