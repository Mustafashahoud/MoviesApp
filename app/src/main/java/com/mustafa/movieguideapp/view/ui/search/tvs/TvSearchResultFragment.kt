package com.mustafa.movieguideapp.view.ui.search.tvs

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentTvSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.hideKeyboard
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.LoadStateAdapter
import com.mustafa.movieguideapp.view.adapter.TvsSearchAdapter
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDispose
import javax.inject.Inject

class TvSearchResultFragment : Fragment(R.layout.fragment_tv_search_result), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TvSearchViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentTvSearchResultBinding>()
    private var pagingAdapter by autoCleared<TvsSearchAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentTvSearchResultBinding.bind(view)

        setRetrySetOnClickListener()

        initializeUI()

        val querySearch = getQuerySafeArgs()
        querySearch.let { query ->
            viewModel.searchTvs(query)
                .autoDispose(scope())
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
                TvSearchResultFragmentDirections.actionTvSearchFragmentResultToTvSearchFragment()
            )
        }

        binding.toolbarSearch.arrowBack.setOnClickListener {
            findNavController().navigate(
                TvSearchResultFragmentDirections.actionTvSearchFragmentResultToTvSearchFragment()
            )
        }
    }

    private fun initAdapter() {
        pagingAdapter = TvsSearchAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                TvSearchResultFragmentDirections.actionTvSearchFragmentResultToTvDetail(it)
            )
        }

        binding.recyclerViewSearchResultTvs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pagingAdapter.withLoadStateFooter(
                footer = LoadStateAdapter { pagingAdapter.retry() }
            )
            this.setHasFixedSize(true)
        }

        pagingAdapter.addLoadStateListener { loadState -> binding.loadState = loadState }
    }


    private fun setRetrySetOnClickListener() {
        binding.retry.setOnClickListener { pagingAdapter.retry() }
    }

    private fun getQuerySafeArgs(): String {
        val params = TvSearchResultFragmentArgs.fromBundle(requireArguments())
        return params.query
    }
}