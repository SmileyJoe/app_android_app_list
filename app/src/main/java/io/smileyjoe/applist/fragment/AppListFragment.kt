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
import io.smileyjoe.applist.enums.Direction
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.util.BundleUtil
import io.smileyjoe.applist.util.Db
import io.smileyjoe.applist.util.Notify
import io.smileyjoe.applist.viewholder.AppDetailViewHolder

class AppListFragment : Fragment() {

    fun interface Listener {
        fun onLoadComplete(page: Page, appCount: Int)
    }

    fun interface ScrollListener {
        fun onScroll(direction: Direction)
    }

    fun interface ItemSelectedListener : AppDetailViewHolder.ItemSelectedListener

    companion object {
        private const val EXTRA_PAGE: String = "page"

        @JvmStatic
        fun newInstance(page: Page): AppListFragment {
            var fragment = AppListFragment()
            var args = Bundle()
            args.putSerializable(EXTRA_PAGE, page)
            fragment.arguments = args
            return fragment
        }
    }

    var isLoading: Boolean = true
    lateinit var page: Page
    lateinit var appDetailAdapter: AppDetailAdapter
    lateinit var view: FragmentAppListBinding
    var listener: Listener? = null
    var scrollListener: ScrollListener? = null
    var itemSelectedListener: ItemSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        page = BundleUtil.getSerializable(arguments, EXTRA_PAGE, Page::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = FragmentAppListBinding.inflate(layoutInflater, container, false)

        setupAdapter()

        view.recyclerAppDetails.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appDetailAdapter
            setOnScrollChangeListener(this@AppListFragment::dispatchScroll)
        }

        populateList()
        handleDisplayView()

        return view.root
    }

    private fun setupAdapter() {
        appDetailAdapter = AppDetailAdapter(ArrayList(), page).apply {
            saveListener = AppDetailViewHolder.Listener { app -> app.save(requireActivity()) }
            deleteListener = AppDetailViewHolder.Listener { app -> app.delete(requireActivity()) }
            itemSelectedListener = this@AppListFragment.itemSelectedListener
        }
    }

    private fun dispatchScroll(view: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        if (scrollY > oldScrollY) {
            scrollListener?.onScroll(Direction.DOWN)
        } else if (scrollY < oldScrollY) {
            scrollListener?.onScroll(Direction.UP)
        }
    }

    private fun populateList() {
        Db.getDetailReference(requireActivity())?.addValueEventListener(AppDetailsEventListener())
    }

    private fun handleDisplayView() {
        var progressVisibility = View.GONE
        var recyclerVisibility = View.GONE
        var textVisibility = View.GONE

        if (isLoading) {
            progressVisibility = View.VISIBLE
        } else if (appDetailAdapter.hasApps()) {
            recyclerVisibility = View.VISIBLE
        } else {
            textVisibility = View.VISIBLE
        }

        view.apply {
            progressLoading.visibility = progressVisibility
            recyclerAppDetails.visibility = recyclerVisibility
            textEmpty.visibility = textVisibility
        }
    }

    inner class AppDetailsEventListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            appDetailAdapter.update(page.getApps(requireContext(), snapshot))
            isLoading = false
            handleDisplayView()
            listener?.onLoadComplete(page, appDetailAdapter.itemCount)
        }

        override fun onCancelled(error: DatabaseError) {
            Notify.error(requireActivity(), R.string.error_database_read_failed)
        }
    }
}