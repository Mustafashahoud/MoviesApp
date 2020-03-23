package com.mustafa.movieapp.view.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentSearchBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.TvSearchListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.RetryCallback
import com.mustafa.movieapp.view.ui.search.base.SearchFragmentBase
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import javax.inject.Inject


class TvSearchFragment : SearchFragmentBase(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    val viewModel by viewModels<TvSearchViewModel> { viewModelFactory }
    var dataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentSearchBinding>()
    var tvAdapter by autoCleared<TvSearchListAdapter>()


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

    fun navController() = findNavController()


    private fun getVisibility() : Boolean {
        return viewModel.searchTvListLiveData.value?.status == Status.SUCCESS
                && viewModel.searchTvListLiveData.value?.data?.size == 0
    }

    override fun setSearchViewHint() {
        search_view.queryHint = "Search Series"
    }

    override fun setBindingVariables() {
        with(binding) {
            resource = viewModel.searchTvListLiveData.value
            visible = getVisibility()
            query = viewModel.queryTvLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
            lifecycleOwner = this@TvSearchFragment
        }
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
            Observer {
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
            appExecutors,
            dataBindingComponent
        ) {
            navController().navigate(
                TvSearchFragmentDirections.actionTvSearchFragmentToTvDetail(
                    it
                )
            )
        }

        binding.root.recyclerView_suggestion.adapter = tvAdapter

        recyclerView_suggestion.layoutManager = LinearLayoutManager(context)
    }


    override fun observeAndSetRecentQueries() {
        viewModel.getTvRecentQueries().observe(viewLifecycleOwner, Observer { it ->
            if (!it.isNullOrEmpty()) {
                val queries = it.map { it.query }
                setListViewOfRecentQueries(queries)
            }
        })
    }

    override fun deleteAllRecentQueries() {
        viewModel.deleteAllTvRecentQueries()
    }

    override fun setSuggestionsQuery(newText: String?) {
        viewModel.setTvSuggestionsQuery(newText!!)
    }

}