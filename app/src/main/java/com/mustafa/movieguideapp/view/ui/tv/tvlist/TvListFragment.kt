package com.mustafa.movieguideapp.view.ui.tv.tvlist

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentTvsBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.visible
import com.mustafa.movieguideapp.models.Status
import com.mustafa.movieguideapp.utils.InfinitePager
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.TvListAdapter
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import com.mustafa.movieguideapp.view.ui.common.RetryCallback
import com.mustafa.movieguideapp.view.ui.main.MainActivity
import javax.inject.Inject

class TvListFragment : Fragment(R.layout.fragment_tvs), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel by viewModels<TvListViewModel> { viewModelFactory }
    private var binding by autoCleared<FragmentTvsBinding>()
    private var tvsAdapter by autoCleared<TvListAdapter>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentTvsBinding.bind(view)

        with(binding) {
            lifecycleOwner = this@TvListFragment
            searchResult = viewModel.tvListLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }

        initializeUI()
        subscribers()

    }

    private fun initializeUI() {
        intiToolbar(getString(R.string.fragment_tvs))
        showBottomNavigationView()
        tvsAdapter = TvListAdapter(dataBindingComponent, appExecutors) {
            findNavController().navigate(
                TvListFragmentDirections.actionTvsToTvDetail(
                    it
                )
            )
        }
        with(binding.recyclerViewListTvs) {
            adapter = tvsAdapter
            layoutManager = GridLayoutManager(context, 3)
            addOnScrollListener(object : InfinitePager(tvsAdapter) {
                override fun loadMoreCondition(): Boolean {
                    viewModel.tvListLiveData.value?.let { resource ->
                        return resource.hasNextPage && resource.status != Status.LOADING
                    }
                    return false
                }

                override fun loadMore() {
                    viewModel.loadMore()
                }
            })
        }


        binding.toolbarSearch.searchIcon.setOnClickListener {
            findNavController().navigate(
                TvListFragmentDirections.actionTvsFragmentToTvSearchFragment()
            )
        }
    }

    private fun subscribers() {
        viewModel.tvListLiveData.observe(viewLifecycleOwner) {
            if (!it.data.isNullOrEmpty()) {
                tvsAdapter.submitList(it.data)
            }
        }
    }

    private fun intiToolbar(title: String) {
        binding.toolbarSearch.toolbarTitle.text = title
    }

    private fun showBottomNavigationView() {
        (activity as MainActivity).binding.bottomNavigation.visible()
    }

}
