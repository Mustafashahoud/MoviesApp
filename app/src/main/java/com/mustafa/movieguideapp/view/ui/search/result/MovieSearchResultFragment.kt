package com.mustafa.movieguideapp.view.ui.search.result

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
import com.mustafa.movieguideapp.databinding.FragmentMovieSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.hideKeyboard
import com.mustafa.movieguideapp.models.Status
import com.mustafa.movieguideapp.utils.InfinitePager
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.MovieSearchListAdapter
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import com.mustafa.movieguideapp.view.ui.common.RetryCallback
import com.mustafa.movieguideapp.view.ui.search.MovieSearchViewModel
import javax.inject.Inject

class MovieSearchResultFragment : Fragment(R.layout.fragment_movie_search_result), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<MovieSearchViewModel> { viewModelFactory }
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentMovieSearchResultBinding>()
    private var moviesAdapter by autoCleared<MovieSearchListAdapter>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentMovieSearchResultBinding.bind(view)

        initializeUI()
        subscribers()

        with(binding) {
            lifecycleOwner = this@MovieSearchResultFragment.viewLifecycleOwner
            searchResult = viewModel.searchMovieListLiveData
            query = viewModel.queryMovieLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }

        viewModel.setSearchMovieQueryAndPage(getQuerySafeArgs(), 1)


    }

    private fun subscribers() {
        viewModel.searchMovieListLiveData.observe(viewLifecycleOwner) {
            if (!it.data.isNullOrEmpty()) {
                moviesAdapter.submitList(it.data)
            }
        }
    }


    private fun getQuerySafeArgs(): String {
        val params =
            MovieSearchResultFragmentArgs.fromBundle(
                requireArguments()
            )
        return params.query
    }

    private fun initializeUI() {
        moviesAdapter = MovieSearchListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            findNavController().navigate(
                MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieDetail(
                    it
                )
            )
        }

        hideKeyboard()
        with(binding.recyclerViewSearchResultMovies) {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : InfinitePager(moviesAdapter) {
                override fun loadMoreCondition(): Boolean {
                    viewModel.searchMovieListLiveData.value?.let { resource ->
                        return resource.hasNextPage && resource.status != Status.LOADING
                    }
                    return false
                }

                override fun loadMore() {
                    viewModel.loadMore()
                }
            })
        }



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

}