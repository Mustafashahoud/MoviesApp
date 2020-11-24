package com.mustafa.movieguideapp.view.ui.search.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentSearchBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.MoviesSearchAdapter
import com.mustafa.movieguideapp.view.ui.search.base.SearchFragmentBase
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieSearchFragment : SearchFragmentBase(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    private val viewModel by viewModels<MovieSearchViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentSearchBinding>()
    private var movieAdapter by autoCleared<MoviesSearchAdapter>()

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )
        return binding.root
    }

    override fun setSearchViewHint() {
        search_view.queryHint = "Search Movies"
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


    override fun observeSuggestions(newText: String?) {
        newText?.let { text ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getSuggestions(text).collectLatest {
                    if (tabs.getTabAt(0)?.isSelected!!) {
                        showSuggestionViewAndHideRecentSearches()
                    }
                    movieAdapter.submitData(it)

                    if ((text.isEmpty() || text.isBlank()) && tabs.getTabAt(0)?.isSelected!!) {
                        hideSuggestionViewAndShowRecentSearches()
                        movieAdapter.submitData(PagingData.empty())
                    }
                }
            }
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

        binding.recyclerViewSuggestion.adapter = movieAdapter
        binding.recyclerViewSuggestion.layoutManager = LinearLayoutManager(requireContext())
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

    override fun setSuggestionsQuery(newText: String?) {}

}

//    private fun set

//    fun setSelectableItemBackground(view: View) {
//        val typedValue = TypedValue()
//
//        // I used getActivity() as if you were calling from a fragment.
//        // You just want to call getTheme() on the current activity, however you can get it
//        activity?.theme?.resolveAttribute(
//            android.R.attr.selectableItemBackground,
//            typedValue,
//            true
//        )
//
//        // it's probably a good idea to check if the color wasn't specified as a resource
//        if (typedValue.resourceId != 0) {
//            view.setBackgroundResource(typedValue.resourceId)
//        } else {
//            // this should work whether there was a resource id or not
//            view.setBackgroundColor(typedValue.data)
//        }
//
//    }


//    override fun onSuggestionSelect(position: Int): Boolean {
//        val cursor = search_view.suggestionsAdapter.getItem(position) as Cursor
//        val query = cursor.getString(1)
//        search_view.setQuery(query, false)
//        search_view.clearFocus()
//        return true
//    }
//
//    override fun onSuggestionClick(position: Int): Boolean {
//        val cursor = search_view.suggestionsAdapter.getItem(position) as Cursor
//        val query = cursor.getString(1)
//        search_view.setQuery(query, false)
//        search_view.clearFocus()
//        return true
//    }

//
//    private fun initSearchViewForSuggestions() {
//        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val searchableInfo = searchManager.getSearchableInfo(activity!!.componentName)
//        search_view.setSearchableInfo(searchableInfo)
//
//        mSearchViewAdapter = SuggestionsAdapter(
//            activity,
//            dataBindingComponent,
//            R.layout.suggestion_search_item,
//            null,
//            columns,
//            null,
//            -1000
//        )
//        search_view.suggestionsAdapter = mSearchViewAdapter
//
//    }
//
//
//    private fun convertToCursor(movies: List<Movie>): MatrixCursor? {
//
//        val cursor = MatrixCursor(columns)
//        var i = 0
//        for (movie in movies) {
//            val temp = ArrayList<String?>()
//
//            i = i + 1
//
//            temp.add(i.toString())
//
//            temp.add(movie.title)
//
//            if (movie.poster_path == null) {
//                temp.add("")
//            } else {
//                temp.add(movie.poster_path)
//            }
//
//            temp.add(movie.vote_average.toString())
//            temp.add(movie.genre_ids.toString())
//
//            if (movie.release_date == null) {
//                temp.add("")
//            } else {
//                temp.add(StringUtils.formatReleaseDate(movie.release_date))
//            }
//
//            cursor.addRow(temp)
//        }
//        return cursor
//    }


