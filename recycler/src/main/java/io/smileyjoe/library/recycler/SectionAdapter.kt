package io.smileyjoe.library.recycler

import androidx.recyclerview.widget.RecyclerView

interface SectionAdapter<T : SectionItem> {

    val adapter: RecyclerView.Adapter<*>
    val displayItems: List<T>?
    var externalScroll: Boolean

    fun getItem(position: Int): T?

    fun getSectionPosition(section: Char): Int? {
        var position: Int? = null
        run breakable@{
            displayItems?.forEachIndexed { index, item ->
                if (item.section == section) {
                    position = index
                    return@breakable
                }
            }
        }

        return position
    }

    fun getAllSections(): List<Char>? =
        displayItems?.mapNotNull {
            it.section
        }?.distinct()

}