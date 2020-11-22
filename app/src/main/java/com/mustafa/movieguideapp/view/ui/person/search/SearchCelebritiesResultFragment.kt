package com.mustafa.movieguideapp.view.ui.person.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentCelebritiesSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.hideKeyboard
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.PeopleAdapter
import kotlinx.android.synthetic.main.toolbar_search_result.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchCelebritiesResultFragment : Fragment(R.layout.fragment_celebrities_search_result),
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    private val viewModel by viewModels<SearchCelebritiesResultViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentCelebritiesSearchResultBinding>()
    private var adapter by autoCleared<PeopleAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeUI()

        getQuerySafeArgs()?.let { query ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchPeople(query).collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }


    private fun getQuerySafeArgs(): String? {
        val params = SearchCelebritiesResultFragmentArgs.fromBundle(requireArguments())
        return params.query
    }

    private fun initializeUI() {

        adapter = PeopleAdapter(dataBindingComponent) {
            findNavController().navigate(
                SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToCelebrityDetail(
                    it
                )
            )
        }

        hideKeyboard()
        binding.recyclerViewSearchResultPeople.adapter = adapter
        binding.recyclerViewSearchResultPeople.layoutManager = LinearLayoutManager(context)

        search_view.setOnSearchClickListener {
            findNavController().navigate(
                SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToSearchCelebritiesFragment()
            )
        }

        arrow_back.setOnClickListener {
            findNavController().navigate(
                SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToSearchCelebritiesFragment()
            )
        }
    }

    /**
     * Receiving Voice Query
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            10 -> if (resultCode == Activity.RESULT_OK && data != null) {
                val voiceQuery = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                hideKeyboard()
                search_view.setQuery(voiceQuery?.let { it[0] }, true)
            }
        }
    }
}