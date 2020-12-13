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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentCelebritiesSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.hideKeyboard
import com.mustafa.movieguideapp.models.Status
import com.mustafa.movieguideapp.utils.Constants.Companion.VOICE_REQUEST_CODE
import com.mustafa.movieguideapp.utils.InfinitePager
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.PeopleSearchListAdapter
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import com.mustafa.movieguideapp.view.ui.common.RetryCallback
import javax.inject.Inject

class SearchCelebritiesResultFragment : Fragment(R.layout.fragment_celebrities_search_result),
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<SearchCelebritiesResultViewModel> { viewModelFactory }
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentCelebritiesSearchResultBinding>()
    private var adapter by autoCleared<PeopleSearchListAdapter>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCelebritiesSearchResultBinding.bind(view)
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            searchResult = viewModel.searchPeopleListLiveData
            query = viewModel.queryPersonLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }

        initializeUI()
        subscribers()
        viewModel.setSearchPeopleQueryAndPage(getQuerySafeArgs(), 1)


    }

    private fun subscribers() {
        viewModel.searchPeopleListLiveData.observe(viewLifecycleOwner) {
            binding.searchResult = viewModel.searchPeopleListLiveData
            if (!it.data.isNullOrEmpty()) {
                adapter.submitList(it.data)
            }
        }
    }


    private fun getQuerySafeArgs(): String {
        val params =
            SearchCelebritiesResultFragmentArgs.fromBundle(
                requireArguments()
            )
        return params.query
    }

    private fun initializeUI() {

        adapter = PeopleSearchListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            findNavController().navigate(
                SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToCelebrityDetail(
                    it
                )
            )
        }

        hideKeyboard()
        binding.apply {
            recyclerViewSearchResultPeople.adapter = adapter
            recyclerViewSearchResultPeople.layoutManager = LinearLayoutManager(context)
            recyclerViewSearchResultPeople.addOnScrollListener(object : InfinitePager(adapter) {
                override fun loadMoreCondition(): Boolean {
                    viewModel.searchPeopleListLiveData.value?.let { resource ->
                        return resource.hasNextPage && resource.status != Status.LOADING
                    }
                    return false
                }

                override fun loadMore() {
                    viewModel.loadMore()
                }
            })
        }


        binding.toolbarSearchResult.searchView.setOnSearchClickListener {
            findNavController().navigate(SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToSearchCelebritiesFragment())
        }

        binding.toolbarSearchResult.arrowBack.setOnClickListener {
            findNavController().navigate(SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToSearchCelebritiesFragment())
        }
    }

    /**
     * Receiving Voice Query
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            VOICE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK && data != null) {
                val voiceQuery = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                hideKeyboard()
                binding.toolbarSearchResult.searchView.setQuery(voiceQuery?.let { it[0] }, true)
            }
        }
    }
}