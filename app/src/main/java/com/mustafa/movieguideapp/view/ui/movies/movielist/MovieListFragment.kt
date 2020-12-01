package com.mustafa.movieguideapp.view.ui.movies.movielist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentMoviesBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.getGridLayoutManagerWithSpanSizeOne
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.LoadStateAdapter
import com.mustafa.movieguideapp.view.adapter.MoviesAdapter
import com.mustafa.movieguideapp.view.ui.AutoDisposeFragment
import com.uber.autodispose.autoDispose
import javax.inject.Inject

class MovieListFragment : AutoDisposeFragment(R.layout.fragment_movies), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MovieListViewModel> { viewModelFactory }

    // public var for testing purpose
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var binding by autoCleared<FragmentMoviesBinding>()

    private var pagingAdapter by autoCleared<MoviesAdapter>()

    private val TAG = "MovieListFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentMoviesBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner

        setRetrySetOnClickListener()
        initializeUI()
        subscribers()
    }

    private fun initializeUI() {
        intiToolbar(getString(R.string.fragment_movies))
        initAdapter()
    }

    private fun subscribers() {
        viewModel.moviesStream
            .autoDispose(scopeProvider)
            .subscribe {
                pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
    }

    private fun initAdapter() {
        pagingAdapter = MoviesAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                MovieListFragmentDirections.actionMoviesFragmentToMovieDetail(it)
            )
        }

        binding.recyclerViewListMovies.apply {
            layoutManager = getGridLayoutManagerWithSpanSizeOne(pagingAdapter, 3)
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

    private fun intiToolbar(title: String) {
        binding.toolbarSearch.toolbarTitle.text = title

        binding.toolbarSearch.searchIcon.setOnClickListener {
            findNavController().navigate(
                MovieListFragmentDirections.actionMoviesFragmentToMovieSearchFragment()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

}


// without Databinding
//    pagingAdapter.addLoadStateListener { loadState ->
//        binding.loadState = loadState
//            binding.recyclerViewListMovies.isVisible = loadState.refresh is LoadState.NotLoading
//            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
//            binding.retry.isVisible = loadState.refresh is LoadState.Error
// Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
//            val errorState =
//                loadState.refresh as? LoadState.Error
//                loadState.append as? LoadState.Error
//                ?: loadState.prepend as? LoadState.Error
//            errorState?.let {
//                Toast.makeText(
//                    requireContext(),
//                    "\uD83D\uDE28 Wooops ${it.error}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//    }

