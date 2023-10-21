package io.smileyjoe.applist.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import io.smileyjoe.applist.R
import io.smileyjoe.applist.adapter.PagerAdapterMain
import io.smileyjoe.applist.databinding.ActivityMainBinding
import io.smileyjoe.applist.enums.Direction
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.extensions.SplashScreenExt.exitAfterAnim
import io.smileyjoe.applist.extensions.SplashScreenExt.removeOnPreDrawListener
import io.smileyjoe.applist.fragment.AppDetailsFragment
import io.smileyjoe.applist.util.Notify

class MainActivity : BaseActivity() {

    companion object {
        private const val EXTRA_FROM_SPLASH = "from_splash"

        fun getIntent(context: Context, fromSplash: Boolean = true): Intent {
            var intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_FROM_SPLASH, fromSplash)
            return intent
        }
    }

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    var loaded = false

    private val onFragmentLoadComplete = PagerAdapterMain.Listener { page, appCount ->
        if (page == Page.INSTALLED) loaded = true

        binding.bottomNavigation.getOrCreateBadge(page.id).apply {
            isVisible = true
            number = appCount
        }
    }

    private val onItemSelected = PagerAdapterMain.ItemSelectedListener { appDetail ->
        supportFragmentManager.addOnBackStackChangedListener(onDetailsBackstackListener)

        supportFragmentManager.commit {
            addToBackStack(AppDetailsFragment.TAG)
            add(R.id.fragment_details, AppDetailsFragment(appDetail), AppDetailsFragment.TAG)
        }
    }

    private val onNavSelected = NavigationBarView.OnItemSelectedListener { item ->
        binding.pagerApps.currentItem = Page.fromId(item.itemId).position
        true
    }

    private val onFabAddClick = View.OnClickListener { view ->
        var options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(this, view, "transition_fab")
        saveAppResult.launch(SaveAppActivity.getIntent(baseContext), options)
    }

    private val onDetailsBackstackListener: OnBackStackChangedListener =
        OnBackStackChangedListener {
            supportFragmentManager.findFragmentByTag(AppDetailsFragment.TAG)?.let { _ ->
                binding.fabAdd.hide()
                binding.bottomNavigation.isVisible = false
            } ?: run {
                binding.fabAdd.show()
                binding.bottomNavigation.isVisible = true
                supportFragmentManager.removeOnBackStackChangedListener(onDetailsBackstackListener)
            }
        }

    private val saveAppResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Notify.success(binding.layoutMain, R.string.success_app_saved)
            }
        }

    private val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            var nav = Page.fromPosition(position)
            binding.textTitle.setText(nav.getTitle(baseContext))
            binding.bottomNavigation.selectedItemId = nav.id
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var pagerAdapterMain = PagerAdapterMain(this).apply {
            listener = this@MainActivity.onFragmentLoadComplete
            itemSelectedListener = this@MainActivity.onItemSelected
        }

        binding.apply {
            pagerApps.adapter = pagerAdapterMain
            pagerApps.offscreenPageLimit = Page.values().size
            pagerApps.registerOnPageChangeCallback(onPageChangeListener)
            textTitle.text = Page.fromId(0).getTitle(baseContext)
            bottomNavigation.setOnItemSelectedListener(this@MainActivity.onNavSelected)
            fabAdd.setOnClickListener(this@MainActivity.onFabAddClick)
        }

        intent.extras?.getBoolean(EXTRA_FROM_SPLASH, true)?.let { fromSplash ->
            if (fromSplash) {
                removeOnPreDrawListener { loaded }
                splashScreen.exitAfterAnim()
            }
        }
    }
}