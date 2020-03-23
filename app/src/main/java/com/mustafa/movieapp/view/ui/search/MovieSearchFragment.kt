package com.mustafa.movieapp.view.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentSearchBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.MovieSearchListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.RetryCallback
import com.mustafa.movieapp.view.ui.search.base.SearchFragmentBase
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import javax.inject.Inject

class MovieSearchFragment : SearchFragmentBase(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<MovieSearchViewModel> { viewModelFactory }

    var dataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentSearchBinding>()

    var movieAdapter by autoCleared<MovieSearchListAdapter>()

//    private lateinit var mSearchViewAdapter: SuggestionsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )
        return binding.root
    }

    /**
     * Created to be able to override in tests
     */
    private fun navController() = findNavController()


    private fun getVisibility() : Boolean {
        return viewModel.searchMovieListLiveData.value?.status == Status.SUCCESS
                && viewModel.searchMovieListLiveData.value?.data?.size == 0
    }

    override fun setSearchViewHint() {
        search_view.queryHint = "Search Movies"
    }

    override fun setBindingVariables() {
        with(binding) {
            resource = viewModel.searchMovieListLiveData.value
            visible = getVisibility()
            query = viewModel.queryMovieLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
            lifecycleOwner = this@MovieSearchFragment
        }
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
        viewModel.movieSuggestions.observe(
            viewLifecycleOwner,
            Observer {
                if (!it.isNullOrEmpty() && tabs.getTabAt(0)?.isSelected!!) {
                    showSuggestionViewAndHideRecentSearches()
                }
                movieAdapter.submitList(it)

                if (newText != null) {
                    if ((newText.isEmpty() || newText.isBlank()) && tabs.getTabAt(0)?.isSelected!!) {
                        hideSuggestionViewAndShowRecentSearches()
                        movieAdapter.submitList(null)
                    }
                }
            })
    }

    override fun setRecyclerViewAdapter() {
        movieAdapter = MovieSearchListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            navController().navigate(
                MovieSearchFragmentDirections.actionMovieSearchFragmentToMovieDetail(
                    it
                )
            )
        }

        binding.root.recyclerView_suggestion.adapter = movieAdapter

        recyclerView_suggestion.layoutManager = LinearLayoutManager(context)
    }


    override fun observeAndSetRecentQueries() {
        viewModel.getMovieRecentQueries().observe(viewLifecycleOwner, Observer { it ->
            if (!it.isNullOrEmpty()) {
                val queries = it.map { it.query }
                setListViewOfRecentQueries(queries)
            }
        })
    }

    override fun deleteAllRecentQueries() {
        viewModel.deleteAllMovieRecentQueries()
    }

    override fun setSuggestionsQuery(newText: String?) {
        viewModel.setMovieSuggestionsQuery(newText!!)
    }

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


