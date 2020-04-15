package com.mustafa.movieapp.view.ui.search.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentSearchResultFilterBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.MovieSearchListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.RetryCallback
import kotlinx.android.synthetic.main.fragment_search_result_filter.*
import kotlinx.android.synthetic.main.fragment_search_result_filter.view.*
import javax.inject.Inject

class MovieSearchResultFilterFragment : SearchResultFilterFragmentBase(), Injectable,
    PopupMenu.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<MovieSearchFilterViewModel> { viewModelFactory }
    var dataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentSearchResultFilterBinding>()
    private var adapter by autoCleared<MovieSearchListAdapter>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        filtersMap = getFilterMap()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_result_filter,
            container,
            false
        )
        return binding.root
    }

    override fun getFilterMap(): HashMap<String, ArrayList<String>>? {
        @Suppress("UNCHECKED_CAST")
        return arguments?.getSerializable("key") as HashMap<String, ArrayList<String>>
    }

    override fun setBindingVariables() {
        with(binding) {
            lifecycleOwner = this@MovieSearchResultFilterFragment
            totalFilterResult = viewModel.totalFilterResult
            selectedFilters = setSelectedFilters()
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }
    }

    override fun observeSubscribers() {
        viewModel.searchMovieListFilterLiveData.observe(viewLifecycleOwner, Observer {
            binding.resource = viewModel.searchMovieListFilterLiveData.value
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
    }

    override fun setRecyclerViewAdapter() {
        adapter = MovieSearchListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            findNavController().navigate(
                MovieSearchResultFilterFragmentDirections.actionMovieSearchFragmentResultFilterToMovieDetail(
                    it
                )
            )
        }

        binding.root.filtered_items_recycler_view.adapter = adapter

        filtered_items_recycler_view.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    override fun loadMoreFilters() {
        viewModel.loadMoreFilters()
    }

    override fun isLoading(): Boolean {
        return viewModel.searchMovieListFilterLiveData.value?.status == Status.LOADING
    }

    override fun navigateFromSearchResultFilterFragmentToSearchFragment() {
        findNavController().navigate(
            MovieSearchResultFilterFragmentDirections.actionMovieSearchFragmentResultFilterToMovieSearchFragment()
        )
    }

    override fun resetAndLoadFiltersSortedBy(order: String) {
        viewModel.resetFilterValues()
        viewModel.setFilters(
            filtersData?.rating,
            order,
            filtersData?.year,
            filtersData?.genres,
            filtersData?.keywords,
            filtersData?.language,
            filtersData?.runtime,
            filtersData?.region,
            1
        )
    }
}