package io.smileyjoe.applist.fragment

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
import io.smileyjoe.applist.adapter.SearchResultsAdapter
import io.smileyjoe.applist.databinding.FragmentSearchResultsBinding
import io.smileyjoe.applist.db.Db
import io.smileyjoe.applist.enums.Page
import io.smileyjoe.applist.interfaces.OnAppSelected
import io.smileyjoe.applist.objects.AppDetail
import io.smileyjoe.applist.util.Notify

class SearchResultsFragment(
    onAppSelected: OnAppSelected
) : Fragment() {

    companion object {
        // the tag to use when adding the fragment //
        const val TAG = "SEARCH_RESULTS"
    }

    lateinit var binding: FragmentSearchResultsBinding
    var allApps: List<AppDetail>? = null
    val dbReference by lazy {
        Db.getDetailReference(requireActivity())
    }
    val resultsAdapter = SearchResultsAdapter(
        onAppSelected = onAppSelected
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbReference?.addValueEventListener(AppDetailsEventListener())

        binding.recyclerSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = resultsAdapter
        }
    }

    fun search(text: String) {
        val filtered = allApps
            ?.filter {
                it.name?.contains(text, ignoreCase = true) ?: false
                        || it.notes?.contains(text, ignoreCase = true) ?: false
            }?.sort(text)

        resultsAdapter.apply {
            searchTerm = text
            items = filtered ?: ArrayList()
        }
    }

    private fun List<AppDetail>.sort(text: String? = null) =
        sortedWith(
            compareBy<AppDetail> { app ->
                text?.let {
                    if (app.name?.contains(it, ignoreCase = true) == true) {
                        SearchResultsAdapter.VIEW_TITLE
                    } else {
                        SearchResultsAdapter.VIEW_OTHER
                    }
                } ?: run {
                    SearchResultsAdapter.VIEW_TITLE
                }
            }.thenBy {
                it.name
            }
        )

    inner class AppDetailsEventListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val installed = Page.INSTALLED.getApps(requireContext(), snapshot)
            val saved = Page.SAVED.getApps(requireContext(), snapshot)
            allApps = (installed + saved).distinctBy { it.appPackage }
            dbReference?.removeEventListener(this)
            resultsAdapter.items = allApps?.sort() ?: ArrayList()
        }

        override fun onCancelled(error: DatabaseError) {
            Notify.error(requireActivity(), R.string.error_database_read_failed)
        }
    }

}