package io.smileyjoe.applist.interfaces

import android.util.Log
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

interface FabActivity {

    val fab : ExtendedFloatingActionButton

    fun showFab() {
        fab.show()
    }

    fun hideFab(){
        fab.hide()
    }

}