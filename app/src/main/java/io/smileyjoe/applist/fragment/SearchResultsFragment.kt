package io.smileyjoe.applist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.smileyjoe.applist.databinding.FragmentSearchResultsBinding

class SearchResultsFragment : Fragment() {

    companion object {
        // the tag to use when adding the fragment //
        const val TAG = "SEARCH_RESULTS"
    }

    lateinit var binding : FragmentSearchResultsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    fun search(text: String){
        binding.textTest.text = text
    }

}