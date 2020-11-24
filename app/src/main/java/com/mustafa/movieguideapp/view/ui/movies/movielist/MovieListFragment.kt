package com.mustafa.movieguideapp.view.ui.movies.movielist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentMoviesBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.getGridLayoutManagerWithSpanSizeOne
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.LoadStateAdapter
import com.mustafa.movieguideapp.view.adapter.MoviesAdapter
import kotlinx.android.synthetic.main.toolbar_search.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieListFragment : Fragment(R.layout.fragment_movies), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MovieListViewModel> { viewModelFactory }

    // public var for testing purpose
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var binding by autoCleared<FragmentMoviesBinding>()

    private var pagingAdapter by autoCleared<MoviesAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMoviesBinding.bind(view)

        setRetrySetOnClickListener()
        initializeUI()
        subscribers()
    }

    private fun initializeUI() {

        intiToolbar(getString(R.string.fragment_movies))
        initAdapter()
    }

    private fun subscribers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.moviesStream.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }

    private fun initAdapter() {
        pagingAdapter = MoviesAdapter(
            dataBindingComponent
        ) {
            MovieListFragmentDirections.actionMoviesFragmentToMovieDetail(it)
        }

        binding.recyclerViewListMovies.apply {
            layoutManager = getGridLayoutManagerWithSpanSizeOne(pagingAdapter, 3)
            adapter = pagingAdapter.withLoadStateFooter(
                footer = LoadStateAdapter { pagingAdapter.retry() }
            )
            this.setHasFixedSize(true)
        }

        pagingAdapter.addLoadStateListener { loadState ->
            binding.recyclerViewListMovies.isVisible = loadState.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
            binding.retry.isVisible = loadState.refresh is LoadState.Error
            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setRetrySetOnClickListener() {
        binding.retry.setOnClickListener { pagingAdapter.retry() }
    }

    /**
     * Init the toolbar
     * @param title
     */
    private fun intiToolbar(title: String) {
        binding.toolbarSearch.toolbar_title.text = title

        binding.toolbarSearch.search_icon.setOnClickListener {
            findNavController().navigate(
                MovieListFragmentDirections.actionMoviesFragmentToMovieSearchFragment()
            )
        }
    }

}