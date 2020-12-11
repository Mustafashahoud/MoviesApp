package com.mustafa.movieguideapp.view.ui.search.filter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.FragmentSearchResultFilterBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.utils.FiltersConstants
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.RATINGS
import com.mustafa.movieguideapp.utils.StringUtils
import com.mustafa.movieguideapp.utils.autoCleared

abstract class SearchResultFilterFragmentBase(@LayoutRes layout: Int) : Fragment(layout),
    Injectable,
    PopupMenu.OnMenuItemClickListener {


    private var filtersMap: HashMap<String, ArrayList<String>>? = null
    private lateinit var filtersData: FilterData

    protected var binding by autoCleared<FragmentSearchResultFilterBinding>()

    companion object {
        const val popularity = "popularity.desc"
        const val vote = "vote_average.desc"
        const val release = "release_date.desc"
        const val sort_by_popularity = "Popularity"
        const val sort_by_vote_count = "Vote Count"
        const val sort_by_release_date = "Release Date"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchResultFilterBinding.bind(view)
        initializeUI()
        observeSubscribers()
        renderSortByTextView(sort_by_popularity)
        filtersMap = getFilterMap()
        filtersData =
            FilterData(
                getRatingFilters(),
                popularity,
                getYearsAsIntegers(),
                getGenresAsSeparatedString(),
                getKeywordsAsSeparatedString(),
                getISOLanguageFilter(),
                getRunTimeFilter(),
                getISORegionFilter()
            )
        resetAndLoadFiltersSortedBy()
        setBindingVariables()
    }


    private fun initializeUI() {
        setRecyclerViewAdapter()

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (v?.getChildAt(v.childCount - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight))
                    && scrollY > oldScrollY
                    && !isLoading()
                    && hasNextPage()
                ) {
                    loadMoreFilters()
                }
            }
        })

        binding.sortByIcon.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                setOnMenuItemClickListener(this@SearchResultFilterFragmentBase)
                inflate(R.menu.navigation_drawer_menu)
                show()
            }
        }

        binding.editFilters.setOnClickListener {
            navigateFromSearchResultFilterFragmentToSearchFragment()
        }
    }


    /**
     * Filtering process:
     * 2- WITHOUT QUERY: if we do NOT have query we can use the service we will have a good result.
     */


    private fun getKeywordsAsSeparatedString(): String {
        return StringUtils.mapKeywordsToSeparatedIds(filtersMap?.get(FiltersConstants.KEYWORDS))
    }

    private fun getYearsAsIntegers(): Int? {
        filtersMap?.get(FiltersConstants.YEARS)?.map {
            it.toInt()
        }?.let {
            if (!it.isNullOrEmpty()) {
                return it[0]
            }

        }
        return null
    }


    private fun getGenresAsSeparatedString(): String? {
        StringUtils.getMovieGenresAsSeparatedString(filtersMap?.get(FiltersConstants.GENRES)).let {
            return if (it.isNotEmpty()) {
                it
            } else {
                null
            }
        }
    }

    private fun getISOLanguageFilter(): String? {
        val language = filtersMap?.get(FiltersConstants.LANGUAGES)
        language?.let {
            if (it.isNotEmpty()) {
                return StringUtils.getISOLanguage(language[0])
            }
        }
        return null
    }

    private fun getISORegionFilter(): String? {
        val regions = filtersMap?.get(FiltersConstants.COUNTRIES)
        regions?.let {
            if (it.isNotEmpty()) {
                return StringUtils.getISORegion(regions[0])
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
        val runTimes = filtersMap?.get(FiltersConstants.RUNTIME)
        runTimes?.let {
            if (it.isNotEmpty()) {
                return StringUtils.mapRunTime(it[0])
            }
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    private fun renderSortByTextView(sortType: String) {
        binding.sortByTextView.text = "Sort by $sortType"
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {

            R.id.sort_popularity -> {
                if (binding.sortByTextView.text == sort_by_popularity) return false
                resetAndLoadFiltersSortedBy(popularity)
                renderSortByTextView(sort_by_popularity)
                true
            }
            R.id.sort_vote -> {
                if (binding.sortByTextView.text == sort_by_vote_count) return false

                resetAndLoadFiltersSortedBy(vote)
                renderSortByTextView(sort_by_vote_count)
                true
            }
            R.id.sort_release -> {
                if (binding.sortByTextView.text == sort_by_release_date) return false
                resetAndLoadFiltersSortedBy(release)
                renderSortByTextView(sort_by_release_date)
                true
            }
            else -> false
        }
    }

    protected fun setSelectedFilters(): String {
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

    protected fun getFilterData() = filtersData


    abstract fun resetAndLoadFiltersSortedBy(order: String = popularity)
    abstract fun getFilterMap(): HashMap<String, ArrayList<String>>?
    abstract fun setBindingVariables()
    abstract fun observeSubscribers()
    abstract fun setRecyclerViewAdapter()
    abstract fun loadMoreFilters()
    abstract fun isLoading(): Boolean
    abstract fun navigateFromSearchResultFilterFragmentToSearchFragment()
    abstract fun hasNextPage(): Boolean
}