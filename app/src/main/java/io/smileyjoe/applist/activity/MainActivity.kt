package io.smileyjoe.applist.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationBarView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.adapter.PagerAdapterMain
import io.smileyjoe.applist.databinding.ActivityMainBinding
import io.smileyjoe.applist.enums.Direction
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.fragment.AppDetailsBottomSheet
import io.smileyjoe.applist.util.Notify

class MainActivity : BaseActivity() {

    companion object {
        private const val ACTIVITY_SAVE_APP: Int = 1

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    lateinit var view: ActivityMainBinding

    var onFragmentLoadComplete = PagerAdapterMain.Listener { page, appCount ->
        view.bottomNavigation.getOrCreateBadge(page.id).apply {
            isVisible = true
            number = appCount
        }
    }

    var onItemSelected = PagerAdapterMain.ItemSelectedListener { appDetail ->
        AppDetailsBottomSheet(appDetail).show(supportFragmentManager, "TAG")
    }

    var onFragmentScroll = PagerAdapterMain.ScrollListener { direction ->
        when (direction) {
            Direction.UP -> {
                if (!view.fabAdd.isShown) {
                    view.fabAdd.show()
                }
            }
            Direction.DOWN -> {
                if (view.fabAdd.isShown) {
                    view.fabAdd.hide()
                }
            }
        }
    }

    var onNavSelected = NavigationBarView.OnItemSelectedListener { item ->
        view.pagerApps.currentItem = Page.fromId(item.itemId).position
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        var pagerAdapterMain = PagerAdapterMain(this).apply {
            listener = this@MainActivity.onFragmentLoadComplete
            itemSelectedListener = this@MainActivity.onItemSelected
            scrollListener = this@MainActivity.onFragmentScroll
        }

        view.apply {
            pagerApps.adapter = pagerAdapterMain
            pagerApps.offscreenPageLimit = Page.values().size
            pagerApps.registerOnPageChangeCallback(OnPageChangeListener())
            textTitle.text = Page.fromId(0).getTitle(baseContext)
            bottomNavigation.setOnItemSelectedListener(this@MainActivity.onNavSelected)
            fabAdd.setOnClickListener { startActivity(SaveAppActivity.getIntent(baseContext)) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ACTIVITY_SAVE_APP -> {
                if (resultCode == RESULT_OK) {
                    Notify.success(view.layoutMain, R.string.success_app_saved)
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    inner class OnPageChangeListener : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            var nav = Page.fromPosition(position)
            view.textTitle.setText(nav.getTitle(baseContext))
            view.bottomNavigation.selectedItemId = nav.id
        }
    }
}