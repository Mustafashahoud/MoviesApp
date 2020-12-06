package com.mustafa.movieguideapp.view.ui.person.celebrities

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentCelebritiesBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.getGridLayoutManagerWithSpanSizeOne
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.LoadStateAdapter
import com.mustafa.movieguideapp.view.adapter.PeopleAdapter
import com.mustafa.movieguideapp.view.ui.AutoDisposeFragment
import com.uber.autodispose.autoDispose
import javax.inject.Inject

class CelebritiesListFragment : AutoDisposeFragment(R.layout.fragment_celebrities), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel by viewModels<CelebritiesListViewModel> { viewModelFactory }
    private var binding by autoCleared<FragmentCelebritiesBinding>()
    private var pagingAdapter by autoCleared<PeopleAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCelebritiesBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner

        setRetrySetOnClickListener()
        initializeUI()
        subscribers()
    }


    private fun initializeUI() {
        intiToolbar(getString(R.string.fragment_celebrities))

        showBottomNavigationView()

        initAdapter()

        binding.toolbarSearch.searchIcon.setOnClickListener {
            findNavController().navigate(
                CelebritiesListFragmentDirections.actionCelebritiesToSearchCelebritiesFragment()
            )
        }
    }

    private fun setRetrySetOnClickListener() {
        binding.retry.setOnClickListener { pagingAdapter.retry() }
    }

    private fun initAdapter() {
        pagingAdapter = PeopleAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                CelebritiesListFragmentDirections.actionCelebritiesToCelebrity(it)
            )
        }

        binding.recyclerViewListCelebrities.apply {
            layoutManager = getGridLayoutManagerWithSpanSizeOne(pagingAdapter, 3)
            adapter = pagingAdapter.withLoadStateFooter(
                footer = LoadStateAdapter { pagingAdapter.retry() }
            )
            this.setHasFixedSize(true)
        }

        pagingAdapter.addLoadStateListener { loadState ->
            binding.loadState = loadState
        }
    }


    private fun subscribers() {
        viewModel.peopleStream
            .autoDispose(scopeProvider)
            .subscribe {
                pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
    }

    private fun intiToolbar(title: String) {
        binding.toolbarSearch.toolbarTitle.text = title
    }
}
