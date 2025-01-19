package io.smileyjoe.applist.util

import android.content.Intent
import android.net.Uri

/**
 * Util class to get Android intents for certain actions.
 *
 * This isn't for getting intents of internal Activities
 */
object IntentUtil {

    /**
     * Get a share intent that will share the provided [content]
     *
     * @param content string to share
     * @return intent
     */
    fun share(content: String): Intent =
        Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, content)
                type = "text/plain"
            }, null
        )

    /**
     * Get an intent to open the provided [link]
     *
     * @param link to open
     * @return intent
     */
    fun open(link: String): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse(link))

}