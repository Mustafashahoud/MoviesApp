package com.mustafa.movieapp.view.ui.movies.movielist

import androidx.lifecycle.*
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.repository.DiscoverRepository
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.AbsentLiveData
import java.util.*
import javax.inject.Inject

@OpenForTesting
class MovieListViewModel @Inject constructor(
        private val discoverRepository: DiscoverRepository
) : ViewModel() {

    private val TAG = MovieListViewModel::class.java.simpleName
    var query: String? = null
    private val moviePageLiveData: MutableLiveData<Int> = MutableLiveData()
    private val searchMoviePageLiveData: MutableLiveData<Int> = MutableLiveData()

    val movieListLiveData: LiveData<Resource<List<Movie>>> = Transformations
            .switchMap(moviePageLiveData) {
                if (it == null) {
                    AbsentLiveData.create()
                } else {
                    discoverRepository.loadMovies(it)
                }
            }

    val searchMovieListLiveData: LiveData<Resource<List<Movie>>> = Transformations
            .switchMap(searchMoviePageLiveData) {
                if (it == null || query == null) {
                    AbsentLiveData.create()
                } else {
                    discoverRepository.searchMovies(query!!, it)
                }
            }


    init {
        moviePageLiveData.value = 1
    }

    final fun setMoviePage(page: Int) {
        moviePageLiveData.value = page
    }

    fun setSearchMovieQueryAndPage(query: String?, page: Int) {
        val input = query?.toLowerCase(Locale.getDefault())?.trim()
//        if (input == this.query) {
//            return
//        }
       this.query = input
        searchMoviePageLiveData.value = page
    }

    fun setSearchMoviePage(page: Int) {
        setSearchMovieQueryAndPage(query, page)
    }
}
