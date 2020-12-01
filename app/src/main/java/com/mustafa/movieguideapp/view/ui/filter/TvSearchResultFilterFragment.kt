package com.mustafa.movieguideapp.view.ui.filter

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
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.FilteredTvsAdapter
import com.mustafa.movieguideapp.view.adapter.LoadStateAdapter
import javax.inject.Inject

class TvSearchResultFilterFragment :
    SearchResultFilterFragmentBase(R.layout.fragment_search_result_filter), Injectable,
    androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TvSearchFilterViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var pagingAdapter by autoCleared<FilteredTvsAdapter>()

    override fun getFilterMap(): HashMap<String, ArrayList<String>>? {
        @Suppress("UNCHECKED_CAST")
        return arguments?.getSerializable("key") as HashMap<String, ArrayList<String>>?
    }

    override fun setBindingVariables() {
        with(binding) {
            lifecycleOwner = this@TvSearchResultFilterFragment.viewLifecycleOwner
            totalFilterResult = viewModel.totalTvsCount
            selectedFilters = setSelectedFilters()
        }
    }

    override fun observeSubscribers() {
        viewModel.searchTvListFilterLiveData.observe(viewLifecycleOwner) {
            pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)

        }
    }

    override fun setRecyclerViewAdapter() {
        initAdapter()
    }

    private fun initAdapter() {
        pagingAdapter = FilteredTvsAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                TvSearchResultFilterFragmentDirections.actionTvSearchFragmentResultFilterToTvDetail(
                    it
                )
            )
        }

        binding.filteredItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = pagingAdapter.withLoadStateFooter(
                footer = LoadStateAdapter { pagingAdapter.retry() }
            )
            this.setHasFixedSize(true)
        }

        pagingAdapter.addLoadStateListener { loadState ->
            binding.filteredItemsRecyclerView.isVisible = loadState.refresh is LoadState.NotLoading
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

    override fun initUI() {
        setRetrySetOnClickListener()
    }

    private fun setRetrySetOnClickListener() {
        binding.retry.setOnClickListener { pagingAdapter.retry() }
    }

    override fun navigateFromSearchResultFilterFragmentToSearchFragment() {
        findNavController().navigate(
            TvSearchResultFilterFragmentDirections.actionTvSearchFragmentResultFilterToTvSearchFragment()
        )
    }

    override fun resetAndLoadFiltersSortedBy(order: String) {
        viewModel.resetFilterValues()
        viewModel.setFilters(
            getFilterData(),
            1
        )
    }
}