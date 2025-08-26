package io.smileyjoe.library.utils

import android.content.Context
import java.util.Locale

enum class Language(
    private val locale: Locale,
    private val firstLetter: Char,
    private val lastLetter: Char
) {
    ENGLISH(Locale.ENGLISH, 'a', 'z');

    companion object {
        fun from(locale: Locale): Language =
            values()
                .firstOrNull { it.locale.language.equals(locale.language) }
                ?: ENGLISH

        fun from(context: Context) =
            from(context.resources.configuration.locales[0] ?: Locale.ENGLISH)
    }

    val alphabet: List<Char> = (firstLetter..lastLetter).toList()

    val alphabetUpper: List<Char> = alphabet.map { it.uppercaseChar() }

}