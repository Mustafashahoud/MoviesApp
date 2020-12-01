package com.mustafa.movieguideapp.view.ui.search.movies

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentMovieSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.LoadStateAdapter
import com.mustafa.movieguideapp.view.adapter.MoviesSearchAdapter
import com.mustafa.movieguideapp.view.ui.AutoDisposeFragment
import com.uber.autodispose.autoDispose
import javax.inject.Inject

class MovieSearchResultFragment : AutoDisposeFragment(R.layout.fragment_movie_search_result),
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MovieSearchViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentMovieSearchResultBinding>()
    private var pagingAdapter by autoCleared<MoviesSearchAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentMovieSearchResultBinding.bind(view)

        setRetrySetOnClickListener()

        initializeUI()

        val querySearch = getQuerySafeArgs()

        querySearch.let { query ->
            viewModel.searchMovies(query)
                .autoDispose(scopeProvider)
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

        binding.toolbarSearch.searchView.setOnSearchClickListener {
            findNavController().navigate(
                MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieSearchFragment()
            )
        }

        binding.toolbarSearch.arrowBack.setOnClickListener {
            findNavController().navigate(
                MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieSearchFragment()
            )
        }
    }

    private fun initAdapter() {
        pagingAdapter = MoviesSearchAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieDetail(
                    it
                )
            )
        }

        binding.recyclerViewSearchResultMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter =
                pagingAdapter.withLoadStateFooter(footer = LoadStateAdapter { pagingAdapter.retry() })
            this.setHasFixedSize(true)
        }

        pagingAdapter.addLoadStateListener { loadState -> binding.loadState = loadState }
    }

    private fun setRetrySetOnClickListener() {
        binding.retry.setOnClickListener { pagingAdapter.retry() }
    }

    private fun getQuerySafeArgs(): String {
        val params = MovieSearchResultFragmentArgs.fromBundle(requireArguments())
        return params.query
    }
}