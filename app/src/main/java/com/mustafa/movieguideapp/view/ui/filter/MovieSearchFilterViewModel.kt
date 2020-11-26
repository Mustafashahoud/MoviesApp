package com.mustafa.movieguideapp.view.ui.filter

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.repository.movies.MoviesRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject

@OpenForTesting
class MovieSearchFilterViewModel @Inject constructor(
    private val repository: MoviesRepository,
) : ViewModel() {

    private val _totalMoviesCount = MutableLiveData<String>()
    val totalMoviesCount: LiveData<String> get() = _totalMoviesCount

    private var filterData = FilterData()

    private val searchMovieFilterPageLiveData: MutableLiveData<Int> = MutableLiveData()

    val searchMovieListFilterLiveData = searchMovieFilterPageLiveData.switchMap {
        repository.loadFilteredMovies(
            filterData = filterData,
        ) { totalCount ->
            _totalMoviesCount.postValue(totalCount.toString())
        }.cachedIn(viewModelScope)
    }


    fun setFilters(
        filterData: FilterData,
        page: Int?
    ) {
        this.filterData = filterData
        searchMovieFilterPageLiveData.value = page
    }


    fun resetFilterValues() {
        filterData = FilterData()
    }
}