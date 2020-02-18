package com.mustafa.movieapp.view.ui.search.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.repository.DiscoverRepository
import com.mustafa.movieapp.utils.AbsentLiveData
import javax.inject.Inject

class MovieSearchFilterViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository
) : ViewModel() {

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

    val searchMovieListFilterLiveData: LiveData<Resource<List<Movie>>> = Transformations
        .switchMap(searchMovieFilterPageLiveData) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                discoverRepository.queryFilteredMovies(
                    rating,
                    sort,
                    year,
                    keyword,
                    genres,
                    language,
                    runtime,
                    region,
                    it
                )
            }
        }

    fun loadFilteredMovies(
        rating: Int?,
        sort: String?,
        year: Int?,
        genres: String?,
        keywords: String?,
        language: String?,
        runtime: Int?,
        region: String?,
        page: Int
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

    val totalFilterResult = discoverRepository.getTotalFilteredResults()

}