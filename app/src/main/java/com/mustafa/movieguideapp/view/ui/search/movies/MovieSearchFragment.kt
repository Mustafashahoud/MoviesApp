package com.mustafa.movieguideapp.view.ui.search.movies

import android.os.Bundle
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.MoviesSearchAdapter
import com.mustafa.movieguideapp.view.ui.search.base.SearchFragmentBase
import javax.inject.Inject

class MovieSearchFragment : SearchFragmentBase(R.layout.fragment_search), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    private val viewModel by viewModels<MovieSearchViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var movieAdapter by autoCleared<MoviesSearchAdapter>()


    override fun setSearchViewHint() {
        searchBinding.toolbarSearch.searchView.queryHint = "Search Movies"
    }

    override fun setFilterTabName(tab: TabLayout.Tab?) {
        tab?.text = getString(R.string.filter_movies_tab_name)
    }

    override fun setBindingVariables() {/*DO nothing*/
    }

    override fun navigateFromSearchFragmentToSearchFragmentResultFilter(bundle: Bundle) {
        findNavController().navigate(
            R.id.action_movieSearchFragment_to_movieSearchFragmentResultFilter,
            bundle
        )
    }

    override fun navigateFromSearchFragmentToSearchFragmentResult(query: String) {
        findNavController().navigate(
            MovieSearchFragmentDirections.actionMovieSearchFragmentToMovieSearchFragmentResult(
                query
            )
        )
    }

    override fun navigateFromSearchFragmentToListItemsFragment() {
        findNavController().navigate(
            MovieSearchFragmentDirections.actionMovieSearchFragmentToMoviesFragment()
        )
    }


    override fun observeSuggestions() {
        viewModel.getSuggestions().observe(viewLifecycleOwner) {
            movieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

    }

    override fun setRecyclerViewAdapter() {
        movieAdapter =
            MoviesSearchAdapter(
                dataBindingComponent
            ) {
                findNavController().navigate(
                    MovieSearchFragmentDirections.actionMovieSearchFragmentToMovieDetail(
                        it
                    )
                )
            }

        searchBinding.recyclerViewSuggestion.adapter = movieAdapter
        searchBinding.recyclerViewSuggestion.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun observeAndSetRecentQueries() {
        viewModel.movieRecentQueries.observe(viewLifecycleOwner) { listQueries ->
            if (!listQueries.isNullOrEmpty()) {
                val queries = listQueries.filter { it.isNotEmpty() }
                if (queries.isNotEmpty()) setListViewOfRecentQueries(queries)
            }
        }
    }

    override fun deleteAllRecentQueries() {
        viewModel.deleteAllMovieRecentQueries()
    }

    override fun setSuggestionsQuery(newText: String?) {
        newText?.let { text ->
            if (searchBinding.tabs.getTabAt(0)?.isSelected!! && !(text.isEmpty() || text.isBlank())) {
                showSuggestionViewAndHideRecentSearches()
                viewModel.setSuggestionQuery(text)
            }

            if ((text.isEmpty() || text.isBlank()) && searchBinding.tabs.getTabAt(0)?.isSelected!!) {
                hideSuggestionViewAndShowRecentSearches()
                movieAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
            }
        }
    }

}



