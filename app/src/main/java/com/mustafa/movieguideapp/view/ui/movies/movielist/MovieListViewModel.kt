package com.mustafa.movieguideapp.view.ui.movies.movielist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ViewModelBase
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@OpenForTesting
class MovieListViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private var pageNumber = 1
    private val moviePageLiveData: MutableLiveData<Int> = MutableLiveData()

    @Inject
    lateinit var appExecutors: AppExecutors

    val movieListLiveData = moviePageLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            discoverRepository.loadMovies(pageNumber).asLiveData()
        }
    }

    init {
        moviePageLiveData.value = 1
    }

    fun loadMore() {
        pageNumber++
        moviePageLiveData.value = pageNumber
    }

    fun refresh() {
        moviePageLiveData.value?.let {
            moviePageLiveData.value = it
        }
    }

}
