package com.mustafa.movieguideapp.view.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentSearchResultFilterBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.TvSearchListAdapter
import com.mustafa.movieguideapp.view.ui.common.RetryCallback
import javax.inject.Inject

class TvSearchResultFilterFragment : SearchResultFilterFragmentBase(), Injectable,
    androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TvSearchFilterViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentSearchResultFilterBinding>()
    private var adapter by autoCleared<TvSearchListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        return arguments?.getSerializable("key") as HashMap<String, ArrayList<String>>?
    }

    override fun setBindingVariables() {
        with(binding) {
            lifecycleOwner = this@TvSearchResultFilterFragment
            totalFilterResult = viewModel.totalTvsCount
            selectedFilters = setSelectedFilters()
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }
    }

    override fun observeSubscribers() {
        viewModel.searchTvListFilterLiveData.observe(viewLifecycleOwner, Observer {
            binding.resource = viewModel.searchTvListFilterLiveData.value
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
    }

    override fun setRecyclerViewAdapter() {
        adapter = TvSearchListAdapter(dataBindingComponent) {
            findNavController().navigate(
                TvSearchResultFilterFragmentDirections.actionTvSearchFragmentResultFilterToTvDetail(
                    it
                )
            )
        }

        binding.filteredItemsRecyclerView.adapter = adapter
        binding.filteredItemsRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    override fun loadMoreFilters() {
        viewModel.loadMoreFilters()
    }

    override fun isLoading(): Boolean {
        return viewModel.searchTvListFilterLiveData.value is Resource.Loading
    }

    override fun navigateFromSearchResultFilterFragmentToSearchFragment() {
        findNavController().navigate(
            TvSearchResultFilterFragmentDirections.actionTvSearchFragmentResultFilterToTvSearchFragment()
        )
    }


    override fun hasNextPage(): Boolean {
        viewModel.searchTvListFilterLiveData.value?.let {
            return it.hasNextPage
        }
        return false
    }


    override fun resetAndLoadFiltersSortedBy(order: String) {
        viewModel.resetFilterValues()
        viewModel.setFilters(
            getFilterData(),
            1
        )
    }
}