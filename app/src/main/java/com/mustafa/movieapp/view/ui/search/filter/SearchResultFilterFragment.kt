package com.mustafa.movieapp.view.ui.search.filter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.COUNTRIES
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.GENRES
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.KEYWORDS
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.LANGUAGES
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.RATINGS
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.RUNTIME
import com.mustafa.movieapp.view.ui.search.MovieSearchFragment.Companion.YEARS
import kotlinx.android.synthetic.main.fragment_search_result_filter.*
import kotlinx.android.synthetic.main.fragment_search_result_filter.view.*
import timber.log.Timber
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

    private var filtersMap: HashMap<String, ArrayList<String>>? = null
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
        Timber.d("Hell..Yeahh...onCreateView()")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("Hell..Yeahh...onViewCreated()")
        initializeUI()
        subscribers()

        renderSortByTextView(sort_by_popularity)

        with(binding) {
            lifecycleOwner = this@SearchResultFilterFragment
            totalFilterResult = viewModel.totalFilterResult
        }

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

    }

    private fun subscribers() {
        viewModel.searchMovieListFilterLiveData.observe(viewLifecycleOwner, Observer {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
        viewModel.totalFilterResult.observe(viewLifecycleOwner, Observer {
//            binding.
        })
    }

    private fun initializeUI() {
        adapter = MovieSearchListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            navController().navigate(
                SearchResultFilterFragmentDirections.actionSearchFragmentResultFilterToMovieDetail(
                    it
                )
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

        filtered_movies_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1
                    && viewModel.searchMovieListFilterLiveData.value?.status != Status.LOADING
                    && dy > 0
                ) {
                    if (viewModel.searchMovieListFilterLiveData.value?.hasNextPage!!) {
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
            if (it.isNotEmpty()) {
               return it
            } else {
                return null
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

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Hell..Yeahh...onDestroy()")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("Hell..Yeahh...onStop()")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Hell..Yeahh...onResume()")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("Hell..Yeahh...onPause()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("Hell..Yeahh...onDestroyView()")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.d("Hell..Yeahh...onDetach()")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("Hell..Yeahh...onStart()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Hell..Yeahh...onCreate()")
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        Timber.d("Hell..Yeahh...onAttachFragment()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("Hell..Yeahh...onAttach()")
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.sort_popularity -> {
                if (sort_by_text_view.text == sort_by_popularity) return false
                viewModel.resetFilterValues()
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
            R.id.sort_vote-> {
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
            R.id.sort_release-> {
                if (sort_by_text_view.text == sort_by_release_date) return false
                viewModel.resetFilterValues()
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


