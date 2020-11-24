package com.mustafa.movieguideapp.view.ui.person.search

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.mustafa.movieguideapp.view.ui.person.celebrities.CelebritiesListFragmentDirections
import kotlinx.android.synthetic.main.toolbar_search_result.*
import kotlinx.coroutines.Job
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
    private var pagingAdapter by autoCleared<PeopleSearchAdapter>()

    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentCelebritiesSearchResultBinding.bind(view)

        setRetrySetOnClickListener()
        initializeUI()

        getQuerySafeArgs().let { query ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchPeople(query).collectLatest { pagingData ->
                    pagingAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun initializeUI() {
        initAdapter()
        hideKeyboard()
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

    private fun initAdapter() {
        pagingAdapter = PeopleSearchAdapter(
            dataBindingComponent
        ) {
            CelebritiesListFragmentDirections.actionCelebritiesToCelebrity(it)
        }

        binding.recyclerViewSearchResultPeople.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pagingAdapter.withLoadStateFooter(
                footer = LoadStateAdapter { pagingAdapter.retry() }
            )
            this.setHasFixedSize(true)
        }

        pagingAdapter.addLoadStateListener { loadState ->
            binding.recyclerViewSearchResultPeople.isVisible =
                loadState.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
            binding.retry.isVisible = loadState.refresh is LoadState.Error
            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setRetrySetOnClickListener() {
        binding.retry.setOnClickListener { pagingAdapter.retry() }
    }


    private fun getQuerySafeArgs(): String {
        val params = SearchCelebritiesResultFragmentArgs.fromBundle(requireArguments())
        return params.query
    }
}