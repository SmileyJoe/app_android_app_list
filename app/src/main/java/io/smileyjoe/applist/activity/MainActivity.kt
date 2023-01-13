package io.smileyjoe.applist.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.badge.BadgeDrawable
import io.smileyjoe.applist.R
import io.smileyjoe.applist.`object`.AppDetail
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        var pagerAdapterMain = PagerAdapterMain(this, this::onFragmentLoadComplete, this::onItemSelected, this::onFragmentScroll)

        view.pagerApps.adapter = pagerAdapterMain
        view.pagerApps.registerOnPageChangeCallback(OnPageChangeListener())
        view.textTitle.setText(Page.fromId(0).getTitle(baseContext))
        view.bottomNavigation.setOnItemSelectedListener { item ->
            view.pagerApps.currentItem = Page.fromId(item.itemId).position
            true
        }
        view.fabAdd.setOnClickListener { startActivity(SaveAppActivity.getIntent(baseContext)) }
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

    fun onFragmentLoadComplete(page: Page, position: Int, appCount: Int) {
        view.bottomNavigation.getOrCreateBadge(page.id).apply {
            isVisible = true
            number = appCount
        }
    }

    fun onItemSelected(appDetail: AppDetail) {
        AppDetailsBottomSheet(appDetail).show(supportFragmentManager, "TAG")
    }

    fun onFragmentScroll(direction: Direction) {
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

    inner class OnPageChangeListener : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            var nav = Page.fromPosition(position)
            view.textTitle.setText(nav.getTitle(baseContext))
            view.bottomNavigation.selectedItemId = nav.id
        }
    }
}