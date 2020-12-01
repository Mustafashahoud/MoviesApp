package com.mustafa.movieguideapp.view.ui.search.tvs

import android.os.Bundle
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.TvsSearchAdapter
import com.mustafa.movieguideapp.view.ui.search.base.SearchFragmentBase
import javax.inject.Inject


class TvSearchFragment : SearchFragmentBase(R.layout.fragment_search), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TvSearchViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var tvAdapter by autoCleared<TvsSearchAdapter>()


    override fun setSearchViewHint() {
        searchBinding.toolbarSearch.searchView.queryHint = "Search Series"
    }

    override fun setFilterTabName(tab: TabLayout.Tab?) {
        tab?.text = getString(R.string.filter_series_tab_name)
    }

    override fun setBindingVariables() {/*Do nothing*/
    }

    override fun navigateFromSearchFragmentToSearchFragmentResultFilter(bundle: Bundle) {
        findNavController().navigate(
            R.id.action_tvSearchFragment_to_tvSearchFragmentResultFilter,
            bundle
        )
    }

    override fun navigateFromSearchFragmentToSearchFragmentResult(query: String) {
        findNavController().navigate(
            TvSearchFragmentDirections.actionTvSearchFragmentToTvSearchFragmentResult(
                query
            )
        )
    }

    override fun navigateFromSearchFragmentToListItemsFragment() {
        findNavController().navigate(
            TvSearchFragmentDirections.actionTvSearchFragmentToTvsFragment()
        )
    }

    override fun observeSuggestions() {
        viewModel.getSuggestions().observe(viewLifecycleOwner) {
            tvAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun setRecyclerViewAdapter() {
        tvAdapter = TvsSearchAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                TvSearchFragmentDirections.actionTvSearchFragmentToTvDetail(
                    it
                )
            )
        }

        searchBinding.recyclerViewSuggestion.adapter = tvAdapter
        searchBinding.recyclerViewSuggestion.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun observeAndSetRecentQueries() {
        viewModel.tvRecentQueries.observe(viewLifecycleOwner) { queries ->
            if (!queries.isNullOrEmpty()) {
                val queryList = queries.filter { it.isNotEmpty() }
                if (queryList.isNotEmpty()) setListViewOfRecentQueries(queryList)
            }
        }
    }

    override fun deleteAllRecentQueries() {
        viewModel.deleteAllTvRecentQueries()
    }

    override fun setSuggestionsQuery(newText: String?) {
        newText?.let { text ->
            if (searchBinding.tabs.getTabAt(0)?.isSelected!! && !(text.isEmpty() || text.isBlank())) {
                showSuggestionViewAndHideRecentSearches()
                viewModel.setSuggestionQuery(text)
            }
            if ((text.isEmpty() || text.isBlank()) && searchBinding.tabs.getTabAt(0)?.isSelected!!) {
                hideSuggestionViewAndShowRecentSearches()
                tvAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
            }
        }
    }

}