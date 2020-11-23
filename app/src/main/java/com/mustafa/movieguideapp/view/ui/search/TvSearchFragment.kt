package com.mustafa.movieguideapp.view.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentSearchBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.TvSearchListAdapter
import com.mustafa.movieguideapp.view.ui.search.base.SearchFragmentBase
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import javax.inject.Inject


class TvSearchFragment : SearchFragmentBase(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TvSearchViewModel> { viewModelFactory }
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentSearchBinding>()
    private var tvAdapter by autoCleared<TvSearchListAdapter>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )

        return binding.root
    }

    override fun setSearchViewHint() {
        search_view.queryHint = "Search Series"
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

    override fun observeSuggestions(newText: String?) {
        viewModel.tvSuggestions.observe(
            viewLifecycleOwner,
            {
                if (!it.isNullOrEmpty() && tabs.getTabAt(0)?.isSelected!!) {
                    showSuggestionViewAndHideRecentSearches()
                }
                tvAdapter.submitList(it)

                if (newText != null) {
                    if ((newText.isEmpty() || newText.isBlank()) && tabs.getTabAt(0)?.isSelected!!) {
                        hideSuggestionViewAndShowRecentSearches()
                        tvAdapter.submitList(null)
                    }
                }
            })
    }

    override fun setRecyclerViewAdapter() {
        tvAdapter = TvSearchListAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                TvSearchFragmentDirections.actionTvSearchFragmentToTvDetail(
                    it
                )
            )
        }

        binding.recyclerViewSuggestion.adapter = tvAdapter
        binding.recyclerViewSuggestion.layoutManager = LinearLayoutManager(context)
    }


    override fun observeAndSetRecentQueries() {
        viewModel.tvRecentQueries.observe(viewLifecycleOwner, { it ->
            if (!it.isNullOrEmpty()) {
                val queries = it.mapNotNull { it.query }.filter { it.isNotEmpty() }
                if (queries.isNotEmpty()) setListViewOfRecentQueries(queries)
            }
        })
    }

    override fun deleteAllRecentQueries() {
        viewModel.deleteAllTvRecentQueries()
    }

    override fun setSuggestionsQuery(newText: String?) {
        viewModel.setTvSuggestionsQuery(newText)
    }

}