package com.mustafa.movieguideapp.view.ui.search.filter

import androidx.lifecycle.*
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@OpenForTesting
class MovieSearchFilterViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    // Filter variables
    ////////////////////////
    private var pageFiltersNumber = 1
    private var sort: String? = "popularity.desc"
    private var year: Int? = null
    private var keyword: String? = null
    private var genres: String? = null
    private var language: String? = null
    private var runtime: Int? = null
    private var region: String? = null
    private var rating: Int? = null
    ////////////////////////

    private val searchMovieFilterPageLiveData: MutableLiveData<Int> = MutableLiveData()

    val searchMovieListFilterLiveData = searchMovieFilterPageLiveData.switchMap {
        launchOnViewModelScope {
            discoverRepository.loadFilteredMovies(
                rating, sort, year, keyword, genres, language, runtime, region, it
            ).asLiveData()
        }
    }


    fun setFilters(
        rating: Int?,
        sort: String?,
        year: Int?,
        genres: String?,
        keywords: String?,
        language: String?,
        runtime: Int?,
        region: String?,
        page: Int?
    ) {
        this.sort = sort
        this.year = year
        this.language = language
        this.keyword = keywords
        this.runtime = runtime
        this.genres = genres
        this.region = region
        this.rating = rating
        searchMovieFilterPageLiveData.value = page
    }

    fun setPage(page: Int?) {
        searchMovieFilterPageLiveData.value = page
    }

    fun loadMoreFilters() {
        pageFiltersNumber++
        searchMovieFilterPageLiveData.value = pageFiltersNumber
    }

    fun resetFilterValues() {
        this.rating = null
        this.region = null
        this.genres = null
        this.keyword = null
        this.language = null
        this.runtime = null
        this.year = null
        this.pageFiltersNumber = 1
    }

    val totalFilterResult = searchMovieFilterPageLiveData.switchMap {
        it?.let {
            discoverRepository.getTotalFilteredResults()
        } ?: AbsentLiveData.create()
    }

    fun refresh() {
        searchMovieFilterPageLiveData.value?.let {
            searchMovieFilterPageLiveData.value = it
        }
    }

}