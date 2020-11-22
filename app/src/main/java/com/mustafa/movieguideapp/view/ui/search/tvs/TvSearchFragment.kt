package com.mustafa.movieguideapp.view.ui.search.tvs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentSearchBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.TvsSearchAdapter
import com.mustafa.movieguideapp.view.ui.search.base.SearchFragmentBase
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class TvSearchFragment : SearchFragmentBase(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TvSearchViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentSearchBinding>()
    private var tvAdapter by autoCleared<TvsSearchAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    @FlowPreview
    override fun observeSuggestions(newText: String?) {
        newText?.let { text ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getSuggestions(text).collectLatest {
                    if (tabs.getTabAt(0)?.isSelected!!) {
                        showSuggestionViewAndHideRecentSearches()
                    }
                    tvAdapter.submitData(it)

                    if ((text.isEmpty() || text.isBlank()) && tabs.getTabAt(0)?.isSelected!!) {
                        hideSuggestionViewAndShowRecentSearches()
                        tvAdapter.submitData(PagingData.empty())
                    }
                }
            }
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

        binding.recyclerViewSuggestion.adapter = tvAdapter
        binding.recyclerViewSuggestion.layoutManager = LinearLayoutManager(context)
    }


    override fun observeAndSetRecentQueries() {
        viewModel.tvRecentQueries.observe(viewLifecycleOwner, Observer { queries ->
            if (!queries.isNullOrEmpty()) {
                val queryList = queries.filter { it.isNotEmpty() }
                if (queryList.isNotEmpty()) setListViewOfRecentQueries(queryList)
            }
        })
    }

    override fun deleteAllRecentQueries() {
        viewModel.deleteAllTvRecentQueries()
    }

    override fun setSuggestionsQuery(newText: String?) {
//        viewModel.setTvSuggestionsQuery(newText)
    }

}