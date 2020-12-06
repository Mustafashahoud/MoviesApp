package com.mustafa.movieguideapp.view.ui.tv.tvlist

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentTvsBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.getGridLayoutManagerWithSpanSizeOne
import com.mustafa.movieguideapp.extension.visible
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.LoadStateAdapter
import com.mustafa.movieguideapp.view.adapter.TvsAdapter
import com.mustafa.movieguideapp.view.ui.AutoDisposeFragment
import com.mustafa.movieguideapp.view.ui.MainActivity
import com.uber.autodispose.autoDispose
import javax.inject.Inject

class TvListFragment : AutoDisposeFragment(R.layout.fragment_tvs), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private val viewModel by viewModels<TvListViewModel> { viewModelFactory }
    private var binding by autoCleared<FragmentTvsBinding>()
    private var pagingAdapter by autoCleared<TvsAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentTvsBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner

        setRetrySetOnClickListener()
        initializeUI()
        subscribers()

    }

    private fun initializeUI() {
        intiToolbar(getString(R.string.fragment_tvs))
        showBottomNavigationView()
        initAdapter()
        binding.toolbarSearch.searchIcon.setOnClickListener {
            findNavController().navigate(
                TvListFragmentDirections.actionTvsFragmentToTvSearchFragment()
            )
        }
    }

    private fun initAdapter() {
        pagingAdapter = TvsAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(TvListFragmentDirections.actionTvsToTvDetail(it))
        }

        binding.recyclerViewListTvs.apply {
            layoutManager = getGridLayoutManagerWithSpanSizeOne(pagingAdapter, 3)
            adapter =
                pagingAdapter.withLoadStateFooter(footer = LoadStateAdapter { pagingAdapter.retry() })
            setHasFixedSize(true)
        }

        pagingAdapter.addLoadStateListener { loadState -> binding.loadState = loadState }
    }

    private fun setRetrySetOnClickListener() {
        binding.retry.setOnClickListener { pagingAdapter.retry() }
    }


    private fun subscribers() {

        viewModel.tvsStream
            .autoDispose(scopeProvider)
            .subscribe {
                pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
    }

    private fun intiToolbar(title: String) {
        binding.toolbarSearch.toolbarTitle.text = title
    }

}
