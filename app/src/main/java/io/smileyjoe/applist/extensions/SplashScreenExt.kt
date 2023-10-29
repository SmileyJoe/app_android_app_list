package io.smileyjoe.applist.extensions

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import android.window.SplashScreen
import android.window.SplashScreenView
import androidx.core.animation.doOnEnd
import io.smileyjoe.applist.extensions.SplashScreenExt.exitAfterAnim
import io.smileyjoe.applist.extensions.SplashScreenExt.getRemainingDuration
import io.smileyjoe.applist.extensions.SplashScreenExt.removeOnPreDrawListener
import java.time.Duration
import java.time.Instant

/**
 * Helper functions for dealing with the splash screen
 * </p>
 * - [exitAfterAnim]
 * - [getRemainingDuration]
 * - [removeOnPreDrawListener]
 *
 * @see [Docs](https://developer.android.com/develop/ui/views/launch/splash-screen)
 */
object SplashScreenExt {

    /**
     * Animate the splashscreen away and then remove it
     */
    fun SplashScreen.exitAfterAnim() {
        setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.ALPHA,
                0.5f
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 500L
            slideUp.startDelay = splashScreenView.getRemainingDuration()
            slideUp.doOnEnd { splashScreenView.remove() }

            slideUp.start()
        }
    }

    /**
     * Get the time in milliseconds until the icon animation will end
     *
     * @return milliseconds until the icon animation has ended
     */
    private fun SplashScreenView.getRemainingDuration(): Long {
        val animationDuration = iconAnimationDuration
        val animationStart = iconAnimationStart

        if (animationDuration != null && animationStart != null) {
            return (animationDuration - Duration.between(animationStart, Instant.now()))
                .toMillis()
                .coerceAtLeast(0L)
        } else {
            return 0L
        }
    }

    /**
     * Remove the predraw listener if the content is loaded
     */
    fun Activity.removeOnPreDrawListener(loaded: () -> Boolean) {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (loaded()) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
                    } else {
                        return false
                    }
                }
            }
        )
    }

}