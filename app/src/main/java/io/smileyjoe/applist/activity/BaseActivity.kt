package io.smileyjoe.applist.activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.smileyjoe.applist.`object`.User

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkSignIn()
    }

    protected open fun checkSignIn() {
        if (User.getCurrent() == null) {
            startActivity(SignInActivity.getIntent(baseContext, intent))
            finish()
        }
    }

    protected fun <T> getParcelable(extras: Bundle, tag: String, clazz: Class<T>): T {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return extras.getParcelable(tag, clazz)!!
        } else {
            return extras.getParcelable(tag)!!
        }
    }

}