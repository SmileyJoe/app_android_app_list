package io.smileyjoe.library.recycler

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

interface AlphabetLayoutAdapter<T : AlphabetLayoutItem> {

    val adapter: RecyclerView.Adapter<*>
    val displayItems: List<T>
    var externalScroll: Boolean

    fun getSectionPosition(section: Char): Int? {
        var position: Int? = null
        run breakable@{
            displayItems.forEachIndexed { index, item ->
                if (item.section == section) {
                    position = index
                    Log.d("AlphabetThings", "Found $position")
                    return@breakable
                }
            }
        }

        return position
    }

    fun getItem(position: Int): T? =
        if (position in displayItems.indices) {
            displayItems[position]
        } else {
            null
        }

}