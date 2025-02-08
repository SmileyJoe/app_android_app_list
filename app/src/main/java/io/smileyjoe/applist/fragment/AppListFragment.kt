package io.smileyjoe.applist.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.smileyjoe.applist.R
import io.smileyjoe.applist.adapter.AppDetailAdapter
import io.smileyjoe.applist.databinding.FragmentAppListBinding
import io.smileyjoe.applist.db.Db
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.extensions.Compat.getSerializableCompat
import io.smileyjoe.applist.util.Notify
import io.smileyjoe.applist.viewholder.AppDetailViewHolder

/**
 * Fragment containing a list of apps, filtered by the [Page] details
 */
class AppListFragment : Fragment() {

    /**
     * Callback for when the page is loaded
     */
    fun interface OnLoadComplete {
        /**
         * The page is loaded
         *
         * @param page the page that loaded
         * @param appCount how many apps are in the list
         */
        fun onLoadComplete(page: Page, appCount: Int)
    }

    /**
     * The user selected an item in the list
     *
     * @see [AppDetailViewHolder.OnItemSelected]
     */
    fun interface OnItemSelected : AppDetailViewHolder.OnItemSelected

    companion object {
        private const val EXTRA_PAGE: String = "page"

        /**
         * Get an instance of the fragment set to the specified [Page]
         *
         * @param page page details to show
         * @return instance of the fragment
         */
        fun newInstance(page: Page): AppListFragment =
            AppListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_PAGE, page)
                }
            }
    }

    var isLoading: Boolean = true
    lateinit var page: Page
    lateinit var appDetailAdapter: AppDetailAdapter
    lateinit var binding: FragmentAppListBinding
    var onLoadComplete: OnLoadComplete? = null
    var onItemSelected: OnItemSelected? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        page = requireArguments().getSerializableCompat(EXTRA_PAGE, Page::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppListBinding.inflate(layoutInflater, container, false)

        setupAdapter()

        binding.recyclerAppDetails.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appDetailAdapter
        }

        populateList()
        handleDisplayView()

        return binding.root
    }

    /**
     * Setup the adapter
     */
    private fun setupAdapter() {
        appDetailAdapter = AppDetailAdapter(
            page = page,
            saveListener = { app -> app.db.save(requireActivity()) },
            deleteListener = { app -> app.db.delete(requireActivity()) },
            onItemSelected = this@AppListFragment.onItemSelected
        )
    }

    /**
     * Add a listener to the db, this will fire once for the first load, and then will make
     * sure the list is kept up to date, eg, if an installed app is saved, it will immediately
     * be added to the saved list.
     */
    private fun populateList() {
        Db.getDetailReference(requireActivity())?.addValueEventListener(AppDetailsEventListener())
    }

    /**
     * Setup which view to display based on the information we have. Either the list will show,
     * or the loader, or an error message.
     */
    private fun handleDisplayView() {
        // only one shows at a time, so set them all to GONE to start //
        var progressVisibility = View.GONE
        var recyclerVisibility = View.GONE
        var textVisibility = View.GONE

        // set only the needed view to visible //
        if (isLoading) {
            progressVisibility = View.VISIBLE
        } else if (appDetailAdapter.hasApps()) {
            recyclerVisibility = View.VISIBLE
        } else {
            textVisibility = View.VISIBLE
        }

        // update all the visibilities //
        binding.apply {
            progressLoading.visibility = progressVisibility
            recyclerAppDetails.visibility = recyclerVisibility
            textEmpty.visibility = textVisibility
        }
    }

    /**
     * Db listener for any updates to the firebase objects.
     * </p>
     * Updates the adapter on any changes and notifies the calling class
     * that data has loaded
     */
    inner class AppDetailsEventListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            appDetailAdapter.items = page.getApps(requireContext(), snapshot)
            isLoading = false
            handleDisplayView()
            onLoadComplete?.onLoadComplete(page, appDetailAdapter.itemCount)
        }

        override fun onCancelled(error: DatabaseError) {
            Notify.error(requireActivity(), R.string.error_database_read_failed)
        }
    }
}