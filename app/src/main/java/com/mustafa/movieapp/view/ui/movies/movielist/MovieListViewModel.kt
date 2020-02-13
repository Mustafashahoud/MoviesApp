package com.mustafa.movieapp.view.ui.movies.movielist

import androidx.lifecycle.*
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.repository.DiscoverRepository
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.AbsentLiveData
import com.mustafa.movieapp.view.ui.common.AppExecutors
import javax.inject.Inject

@OpenForTesting
class MovieListViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository
) : ViewModel() {

    private var pageNumber = 1
    private val moviePageLiveData: MutableLiveData<Int> = MutableLiveData()

    @Inject
    lateinit var appExecutors: AppExecutors

    val movieListLiveData: LiveData<Resource<List<Movie>>> = Transformations
        .switchMap(moviePageLiveData) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                discoverRepository.loadMovies(it)
            }
        }

    init {
        moviePageLiveData.value = 1
    }

    final fun setMoviePage(page: Int) {
        moviePageLiveData.value = page
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
