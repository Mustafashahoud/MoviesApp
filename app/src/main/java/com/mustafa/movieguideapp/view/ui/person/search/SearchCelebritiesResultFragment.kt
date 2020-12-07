package com.mustafa.movieguideapp.view.ui.person.search

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentCelebritiesSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.hideKeyboard
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.LoadStateAdapter
import com.mustafa.movieguideapp.view.adapter.PeopleSearchAdapter
import com.mustafa.movieguideapp.view.ui.AutoDisposeFragment
import com.uber.autodispose.autoDispose
import javax.inject.Inject

class SearchCelebritiesResultFragment :
    AutoDisposeFragment(R.layout.fragment_celebrities_search_result),
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    private val viewModel by viewModels<SearchCelebritiesResultViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentCelebritiesSearchResultBinding>()
    private var pagingAdapter by autoCleared<PeopleSearchAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentCelebritiesSearchResultBinding.bind(view)

        setRetrySetOnClickListener()
        initializeUI()

        val querySearch = getQuerySafeArgs()

        querySearch.let { query ->
            viewModel.searchPeople(query)
                .autoDispose(scopeProvider)
                .subscribe {
                    pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            query = querySearch
            itemCount = pagingAdapter.itemCount
        }
    }

    private fun initializeUI() {
        initAdapter()
        hideKeyboard()
        binding.toolbarSearch.searchView.setOnSearchClickListener {
            findNavController().navigate(
                SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToSearchCelebritiesFragment()
            )
        }

        binding.toolbarSearch.arrowBack.setOnClickListener {
            findNavController().navigate(
                SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToSearchCelebritiesFragment()
            )
        }
    }

    private fun initAdapter() {
        pagingAdapter = PeopleSearchAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToCelebrityDetail(
                    it
                )
            )
        }

        binding.recyclerViewSearchResultPeople.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pagingAdapter.withLoadStateFooter(
                footer = LoadStateAdapter { pagingAdapter.retry() }
            )
            this.setHasFixedSize(true)
        }

        pagingAdapter.addLoadStateListener { loadState -> binding.loadState = loadState}
    }

    private fun setRetrySetOnClickListener() {
        binding.retry.setOnClickListener { pagingAdapter.retry() }
    }


    private fun getQuerySafeArgs(): String {
        val params = SearchCelebritiesResultFragmentArgs.fromBundle(requireArguments())
        return params.query
    }
}