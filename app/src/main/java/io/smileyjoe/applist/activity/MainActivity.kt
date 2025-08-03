package io.smileyjoe.applist.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.color.MaterialColors
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import io.smileyjoe.applist.R
import io.smileyjoe.applist.adapter.PagerAdapterAppList
import io.smileyjoe.applist.databinding.ActivityMainBinding
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.extensions.FragmentExt.add
import io.smileyjoe.applist.extensions.FragmentExt.clear
import io.smileyjoe.applist.extensions.SearchViewExt.close
import io.smileyjoe.applist.extensions.SearchViewExt.onClosing
import io.smileyjoe.applist.extensions.SearchViewExt.onOpening
import io.smileyjoe.applist.extensions.SplashScreenExt.exitAfterAnim
import io.smileyjoe.applist.extensions.SplashScreenExt.removeOnPreDrawListener
import io.smileyjoe.applist.fragment.AppDetailsFragment
import io.smileyjoe.applist.fragment.AppListFragment
import io.smileyjoe.applist.fragment.SearchResultsFragment
import io.smileyjoe.applist.interfaces.FabActivity
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.objects.Filter
import io.smileyjoe.applist.util.Notify
import io.smileyjoe.library.utils.Extensions.addDistinct
import io.smileyjoe.library.utils.Extensions.hide
import io.smileyjoe.library.utils.Extensions.show

/**
 * Main activity, houses a view pager of fragments, one for each item in [Page]
 */
class MainActivity : BaseActivity(), FabActivity {

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
        fun getIntent(context: Context, fromSplash: Boolean = true) =
            Intent(context, MainActivity::class.java).apply {
                putExtra(EXTRA_FROM_SPLASH, fromSplash)
            }
    }

    // activity UI //
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // distinct list of all the tags in the lists //
    private val tags = mutableListOf<String>()

    // enabled filters //
    private val filter = Filter()

    // only remove the splash screen if the activity has fully loaded, so keep track of that //
    private var loaded = false

    override val fab: ExtendedFloatingActionButton
        get() = binding.fabAdd

    // listen to the backstack to show or hide the fab and bottom nav //
    private val onDetailsBackstackListener: OnBackStackChangedListener =
        OnBackStackChangedListener {
            supportFragmentManager.findFragmentByTag(AppDetailsFragment.TAG)?.let { _ ->
                // if the AppDetailsFragment is on the backstack, hide the fab and bottom nav //
                hideFab()
                binding.bottomNavigation.hide()
            } ?: run {
                // else show them and remove the listener //
                showFab()
                binding.bottomNavigation.show()
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
            binding.searchView.hide()
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
            binding.layoutTags.tags = this@MainActivity.tags
        },
        // show the details when an item is selected //
        onItemSelected = { appDetail -> showApp(appDetail) },
        getFilter = { filter }
    )

    private var searchResultsFragment: SearchResultsFragment? = null
        get() =
            field ?: supportFragmentManager.findFragmentByTag(SearchResultsFragment.TAG)?.let {
                field = (it as SearchResultsFragment)
                field
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        // handle any shared element animations //
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        handleBackPressed()

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
        }

        setupFab()
        setupTags()
        setupSearchView()

        // remove the splash screen if we are coming from there //
        intent.extras?.getBoolean(EXTRA_FROM_SPLASH, true)?.let { fromSplash ->
            if (fromSplash) {
                removeOnPreDrawListener { loaded }
                splashScreen.exitAfterAnim()
            }
        }
    }

    private fun showApp(app: AppDetail) {
        binding.searchView.close {
            supportFragmentManager.addOnBackStackChangedListener(onDetailsBackstackListener)

            supportFragmentManager.commit {
                addToBackStack(AppDetailsFragment.TAG)
                add(
                    R.id.fragment_details,
                    AppDetailsFragment(app, tags),
                    AppDetailsFragment.TAG
                )
            }
        }
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback {
            if (binding.searchView.isShowing) {
                binding.searchView.hide()
            } else {
                finish()
            }
        }
    }

    private fun setupFab() = fab.apply {
        setOnClickListener { view ->
            saveAppResult.launch(
                SaveAppActivity.getIntent(
                    context = baseContext,
                    tags = tags
                ),
                ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                        this@MainActivity,
                        view,
                        "transition_fab"
                    )
            )
        }
    }

    private fun setupTags() = binding.layoutTags.apply {
        toggleView = binding.imageFilter
        detailsView = binding.textFilter
        clearView = binding.imageFilterClear
        onSelectedTagsChanged = {
            filter.tags = it
            pagerAdapterMain.refresh()
        }
    }

    private fun setupSearchView() = with(binding.searchView) {
        setupWithSearchBar(binding.searchBar)
        editText.doOnTextChanged { text, _, _, _ ->
            searchResultsFragment?.search(text.toString())
        }
        onOpening {
            binding.fragmentSearchResults.add(
                SearchResultsFragment { appDetail -> showApp(appDetail) },
                SearchResultsFragment.TAG
            )
            window.statusBarColor =
                MaterialColors.getColor(binding.root, R.attr.colorSurfaceContainerHigh)
            binding.bottomNavigation.hide()
        }
        onClosing {
            binding.fragmentSearchResults.clear<SearchResultsFragment>()
            searchResultsFragment = null
            window.statusBarColor = Color.TRANSPARENT
            binding.bottomNavigation.show()
        }
    }
}