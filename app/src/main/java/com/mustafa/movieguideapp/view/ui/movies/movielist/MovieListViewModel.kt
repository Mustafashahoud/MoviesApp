package com.mustafa.movieguideapp.view.ui.movies.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
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

    fun setMoviePage(page: Int) {
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
