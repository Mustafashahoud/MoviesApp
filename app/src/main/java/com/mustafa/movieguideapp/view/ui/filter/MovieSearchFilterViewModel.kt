package com.mustafa.movieguideapp.view.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.repository.movies.MoviesRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@OpenForTesting
class MovieSearchFilterViewModel @Inject constructor(
    private val repository: MoviesRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private var pageFiltersNumber = 1

    private val _totalMoviesCount = MutableLiveData<String>()
    val totalMoviesCount: LiveData<String> get() = _totalMoviesCount

    private var filterData = FilterData()


    private val searchMovieFilterPageLiveData: MutableLiveData<Int> = MutableLiveData()

    val searchMovieListFilterLiveData = searchMovieFilterPageLiveData.switchMap {
        launchOnViewModelScope {
            repository.loadFilteredMovies(
                filterData = filterData,
                page = it
            ) { totalCount ->
                _totalMoviesCount.postValue(totalCount.toString())
            }.asLiveData()
        }
    }


    fun setFilters(
        filterData: FilterData,
        page: Int?
    ) {
        this.filterData = filterData
        searchMovieFilterPageLiveData.value = page
    }

    fun loadMoreFilters() {
        pageFiltersNumber++
        searchMovieFilterPageLiveData.value = pageFiltersNumber
    }

    fun resetFilterValues() {
        filterData = FilterData()
        this.pageFiltersNumber = 1
    }

    fun refresh() {
        searchMovieFilterPageLiveData.value?.let {
            searchMovieFilterPageLiveData.value = it
        }
    }

}