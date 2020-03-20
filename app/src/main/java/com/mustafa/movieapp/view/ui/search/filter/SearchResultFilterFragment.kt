package com.mustafa.movieapp.view.ui.search.filter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentSearchResultFilterBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.utils.StringUtils.Companion.getISOLanguage
import com.mustafa.movieapp.utils.StringUtils.Companion.getISORegion
import com.mustafa.movieapp.utils.StringUtils.Companion.getMovieGenresAsSeparatedString
import com.mustafa.movieapp.utils.StringUtils.Companion.mapKeywordsToSeparatedIds
import com.mustafa.movieapp.utils.StringUtils.Companion.mapRunTime
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.MovieSearchListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.RetryCallback
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.COUNTRIES
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.GENRES
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.KEYWORDS
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.LANGUAGES
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.RATINGS
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.RUNTIME
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.YEARS
import kotlinx.android.synthetic.main.fragment_search_result_filter.*
import kotlinx.android.synthetic.main.fragment_search_result_filter.view.*
import javax.inject.Inject

class SearchResultFilterFragment : Fragment(), Injectable, PopupMenu.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<MovieSearchFilterViewModel> { viewModelFactory }
    var dataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentSearchResultFilterBinding>()
    var adapter by autoCleared<MovieSearchListAdapter>()
    var filtersMap: HashMap<String, ArrayList<String>>? = null
    private var filtersData: FilterData? = null

    companion object {
        const val popularity = "popularity.desc"
        const val vote = "vote_average.desc"
        const val release = "release_date.desc"
        const val sort_by_popularity= "Popularity"
        const val sort_by_vote_count = "Vote Count"
        const val sort_by_release_date = "Release Date"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_result_filter,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeUI()
        subscribers()
        renderSortByTextView(sort_by_popularity)
        if (filtersData == null){
            filtersMap = getFilterMap()
            filtersData = FilterData(
                getRatingFilters(),
                getYearsAsIntegers(),
                getGenresAsSeparatedString(),
                getKeywordsAsSeparatedString(),
                getISOLanguageFilter(),
                getRunTimeFilter(),
                getISORegionFilter()
            )
            filtersData?.let {
                viewModel.loadFilteredMovies(
                    it.rating,
                    popularity,
                    it.year,
                    it.genres,
                    it.keywords,
                    it.language,
                    it.runtime,
                    it.region,
                    1
                )
            }
        }

        with(binding) {
            lifecycleOwner = this@SearchResultFilterFragment
            totalFilterResult = viewModel.totalFilterResult
            filterResult = viewModel.searchMovieListFilterLiveData
            selectedFilters = setSelectedFilters()
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }


    }

    private fun subscribers() {
        viewModel.searchMovieListFilterLiveData.observe(viewLifecycleOwner, Observer {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
    }

    private fun initializeUI() {
        adapter = MovieSearchListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            navController().navigate(
                SearchResultFilterFragmentDirections.actionMovieSearchFragmentResultFilterToMovieDetail(it)
            )
        }

//        adapter.setHasStableIds(true) // prevent blinking .. in Case notifyDataSetChanged()
        // To have a nice animation and avoid blinking in the RecyclerView:
        /**
         * 1-  adapter.setHasStableIds(true)
         * 2-  Use notifyItemRangeInserted(start, count)
         */
        binding.root.filtered_movies_recycler_view.adapter = adapter

        filtered_movies_recycler_view.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        nestedScrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if(v?.getChildAt(v.childCount - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight))
                        && scrollY > oldScrollY
                        && viewModel.searchMovieListFilterLiveData.value?.status != Status.LOADING) {
                        viewModel.loadMoreFilters()
                    }
                }
            }

        })

        sort_by_icon.setOnClickListener{
            PopupMenu(context, it).apply {
                setOnMenuItemClickListener(this@SearchResultFilterFragment)
                inflate(R.menu.navigation_drawer_menu)
                show()
            }
        }

        edit_filters.setOnClickListener {
            navController().navigate(
                SearchResultFilterFragmentDirections.actionMovieSearchFragmentResultFilterToMovieSearchFragment())
        }
    }

    /**
     * Filtering process:
     * 2- WITHOUT QUERY: if we do NOT have query we can use the service we will have a good result.
     */


    private fun getFilterMap(): HashMap<String, ArrayList<String>>? {
        @Suppress("UNCHECKED_CAST")
        return arguments?.getSerializable("key") as HashMap<String, ArrayList<String>>
    }


    private fun getKeywordsAsSeparatedString(): String? {
        return mapKeywordsToSeparatedIds(filtersMap?.get(KEYWORDS))
    }

    private fun getYearsAsIntegers(): Int? {
        filtersMap?.get(YEARS)?.map {
            it.toInt()
        }?.let {
            if (!it.isNullOrEmpty()) {
                    return it[0]
            }

        }
        return null
    }

    private fun getGenresAsSeparatedString(): String? {
        getMovieGenresAsSeparatedString(filtersMap?.get(GENRES)).let {
            return if (it.isNotEmpty()) {
                it
            } else {
                null
            }
        }
    }

    private fun getISOLanguageFilter(): String? {
        val language = filtersMap?.get(LANGUAGES)
        language?.let {
            if (it.isNotEmpty()) {
                return getISOLanguage(language[0])
            }
        }
        return null
    }

    private fun getISORegionFilter(): String? {
        val regions = filtersMap?.get(COUNTRIES)
        regions?.let {
            if (it.isNotEmpty()) {
                return getISORegion(regions[0])
            }
        }
        return null
    }

    private fun getRatingFilters(): Int? {
       filtersMap?.get(RATINGS)?.let {
            if (it.isNotEmpty()) {
                return it[0].toInt()
            }
        }
        return null
    }

    private fun getRunTimeFilter(): Int? {
        val runTimes = filtersMap?.get(RUNTIME)
        runTimes?.let {
            if (it.isNotEmpty()) {
                return mapRunTime(it[0])
            }
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    private fun renderSortByTextView(sortType: String) {
        sort_by_text_view.text = "Sort by $sortType"
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {

            R.id.sort_popularity -> {
                if (sort_by_text_view.text == sort_by_popularity) return false
                viewModel.loadFilteredMovies(
                    filtersData?.rating,
                    popularity,
                    filtersData?.year,
                    filtersData?.genres,
                    filtersData?.keywords,
                    filtersData?.language,
                    filtersData?.runtime,
                    filtersData?.region,
                    1
                )
                renderSortByTextView(sort_by_popularity)
                true
            }
            R.id.sort_vote -> {
                if (sort_by_text_view.text == sort_by_vote_count) return false
                viewModel.resetFilterValues()
                viewModel.loadFilteredMovies(
                    filtersData?.rating,
                    vote,
                    filtersData?.year,
                    filtersData?.genres,
                    filtersData?.keywords,
                    filtersData?.language,
                    filtersData?.runtime,
                    filtersData?.region,
                    1
                )
                renderSortByTextView(sort_by_vote_count)
                true
            }
            R.id.sort_release -> {
                if (sort_by_text_view.text == sort_by_release_date) return false
                viewModel.loadFilteredMovies(
                    filtersData?.rating,
                    release,
                    filtersData?.year,
                    filtersData?.genres,
                    filtersData?.keywords,
                    filtersData?.language,
                    filtersData?.runtime,
                    filtersData?.region,
                    1
                )
                renderSortByTextView(sort_by_release_date)
                true
            }
            else -> false
        }
    }

    private fun setSelectedFilters(): String {
        val stringBuilder = StringBuilder()
        val selectedFiltersList = ArrayList<String>()
        filtersMap?.values?.map {
            stringBuilder.append(it.joinToString())
            selectedFiltersList.add(it.joinToString())
        }
        return if (stringBuilder.isEmpty()) "No Filters were applied"
        else {
            val selectedFilters = ArrayList<String>()
            for (filter in selectedFiltersList) {
                if (filter.isNotBlank() || filter.isNotEmpty()) {
                    selectedFilters.add(filter)
                }
            }
            selectedFilters.joinToString()
        }
    }

    data class FilterData(
        var rating: Int? = null,
        var year: Int? = null,
        var genres: String? = null,
        var keywords: String? = null,
        var language: String? = null,
        var runtime: Int? = null,
        var region: String? = null
    )
}