package io.smileyjoe.applist.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import io.smileyjoe.applist.R
import io.smileyjoe.applist.adapter.PagerAdapterAppList
import io.smileyjoe.applist.databinding.ActivityMainBinding
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.extensions.Extensions.addDistinct
import io.smileyjoe.applist.extensions.SplashScreenExt.exitAfterAnim
import io.smileyjoe.applist.extensions.SplashScreenExt.removeOnPreDrawListener
import io.smileyjoe.applist.fragment.AppDetailsFragment
import io.smileyjoe.applist.util.Notify

/**
 * Main activity, houses a view pager of fragments, one for each item in [Page]
 */
class MainActivity : BaseActivity() {

    companion object {
        /**
         * Check if this screen is loading from the splash screen, if it is we need
         * to do some extra things like exit the splash screen
         */
        private const val EXTRA_FROM_SPLASH = "from_splash"

        /**
         * Get the intent to start the activity
         *
         * @param context current context
         * @param fromSplash true if this is from the splash screen, defaults to true
         * @return the intent to start the activity
         */
        fun getIntent(context: Context, fromSplash: Boolean = true): Intent {
            var intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_FROM_SPLASH, fromSplash)
            return intent
        }
    }

    // activity UI //
    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // distinct list of all the tags in the lists //
    private val tags = mutableListOf<String>()

    // enabled filters //
    private val activeFilters = mutableListOf<String>()

    // only remove the splash screen if the activity has fully loaded, so keep track of that //
    private var loaded = false

    // listen to the backstack to show or hide the fab and bottom nav //
    private val onDetailsBackstackListener: OnBackStackChangedListener =
        OnBackStackChangedListener {
            supportFragmentManager.findFragmentByTag(AppDetailsFragment.TAG)?.let { _ ->
                // if the AppDetailsFragment is on the backstack, hide the fab and bottom nav //
                binding.fabAdd.hide()
                binding.bottomNavigation.isVisible = false
            } ?: run {
                // else show them and remove the listener //
                binding.fabAdd.show()
                binding.bottomNavigation.isVisible = true
                window.statusBarColor = Color.TRANSPARENT
                supportFragmentManager.removeOnBackStackChangedListener(onDetailsBackstackListener)
            }
        }

    // listen for the result from saving/editing an item //
    private val saveAppResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Notify.success(binding.layoutMain, R.string.success_app_saved)
            }
        }

    // update the bottom nav and title when the page changes //
    private val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            var nav = Page.fromPosition(position)
            binding.textTitle.setText(nav.getTitle(baseContext))
            binding.bottomNavigation.selectedItemId = nav.id
        }
    }

    // pager adapter for the view pager that houses the AppListFragments //
    private val pagerAdapterMain = PagerAdapterAppList(
        activity = this,
        // call back for when the page has loaded //
        onLoadComplete = { page, appCount, tags ->
            // installed is the last page, so when that is loaded, everything is loaded //
            if (page == Page.INSTALLED) loaded = true

            // set the app count badge on the bottom nav //
            binding.bottomNavigation.getOrCreateBadge(page.id).apply {
                isVisible = true
                number = appCount
            }

            // add the tags to the global distinct list //
            this@MainActivity.tags.addDistinct(tags)
            populateTags()
        },
        // show the details when an item is selected //
        onItemSelected = { appDetail ->
            supportFragmentManager.addOnBackStackChangedListener(onDetailsBackstackListener)

            supportFragmentManager.commit {
                addToBackStack(AppDetailsFragment.TAG)
                add(R.id.fragment_details, AppDetailsFragment(appDetail), AppDetailsFragment.TAG)
            }
        },
        getFilters = { activeFilters }
    )

    // inflate the chip used for tag filters //
    private val chipTag: Chip
        get() = layoutInflater.inflate(
            R.layout.inflate_chip_filter,
            binding.layoutTags,
            false
        ) as Chip

    override fun onCreate(savedInstanceState: Bundle?) {
        // handle any shared element animations //
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // populate the ui //
        binding.apply {
            pagerApps.apply {
                adapter = pagerAdapterMain
                offscreenPageLimit = Page.values().size
                registerOnPageChangeCallback(onPageChangeListener)
            }
            textTitle.text = Page.fromId(0).getTitle(baseContext)
            bottomNavigation.setOnItemSelectedListener { item ->
                binding.pagerApps.currentItem = Page.fromId(item.itemId).position
                true
            }
            fabAdd.setOnClickListener { view ->
                saveAppResult.launch(
                    SaveAppActivity.getIntent(baseContext),
                    ActivityOptionsCompat
                        .makeSceneTransitionAnimation(
                            this@MainActivity,
                            view,
                            "transition_fab"
                        )
                )
            }
        }

        // remove the splash screen if we are coming from there //
        intent.extras?.getBoolean(EXTRA_FROM_SPLASH, true)?.let { fromSplash ->
            if (fromSplash) {
                removeOnPreDrawListener { loaded }
                splashScreen.exitAfterAnim()
            }
        }
    }

    private fun populateTags() {
        with(binding.layoutTags) {
            removeAllViews()
            tags.forEach { tag ->
                addView(
                    chipTag.apply {
                        text = tag
                        isChecked = activeFilters.contains(tag)
                        setOnCheckedChangeListener(::onFilterClicked)
                    }
                )
            }
        }
    }

    private fun onFilterClicked(chip: CompoundButton, isChecked: Boolean) {
        val text = chip.text.toString()
        if (isChecked) {
            activeFilters.add(text)
        } else {
            activeFilters.remove(text)
        }
        pagerAdapterMain.refresh()
    }
}