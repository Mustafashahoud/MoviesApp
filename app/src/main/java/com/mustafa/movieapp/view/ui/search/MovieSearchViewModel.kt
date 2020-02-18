package com.mustafa.movieapp.view.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.MovieRecentQueries
import com.mustafa.movieapp.repository.DiscoverRepository
import com.mustafa.movieapp.utils.AbsentLiveData
import java.util.*
import javax.inject.Inject

class MovieSearchViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository
) : ViewModel() {


    private val searchMoviePageLiveData: MutableLiveData<Int> = MutableLiveData()
    private var moviesPageNumber = 1

    private val _movieQuery = MutableLiveData<String>()
    val queryMovieLiveData: LiveData<String> = _movieQuery
    val searchMovieListLiveData: LiveData<Resource<List<Movie>>> = Transformations
        .switchMap(searchMoviePageLiveData) {
            if (it == null || queryMovieLiveData.value == null) {
                AbsentLiveData.create()
            } else {
                discoverRepository.searchMovies(queryMovieLiveData.value!!, it)
            }
        }


    fun setSearchMovieQueryAndPage(query: String?, page: Int) {
        val input = query?.toLowerCase(Locale.getDefault())?.trim()
        if (input == queryMovieLiveData.value) {
            return
        }
        _movieQuery.value = input
        searchMoviePageLiveData.value = page
    }


    fun loadMore() {
        moviesPageNumber++
        searchMoviePageLiveData.value = moviesPageNumber
    }

    fun refresh() {
        searchMoviePageLiveData.value?.let {
            searchMoviePageLiveData.value = it
        }
    }

    fun resetPageNumber() {
        moviesPageNumber = 1
    }


    private val _movieSuggestionsQuery = MutableLiveData<String>()
    private val movieSuggestionsQuery: LiveData<String> = _movieSuggestionsQuery
    val movieSuggestions: LiveData<List<Movie>> = Transformations
        .switchMap(movieSuggestionsQuery) {
            if (it.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                discoverRepository.getMovieSuggestionsFromRoom(movieSuggestionsQuery.value!!)
            }
        }

    fun setMovieSuggestionsQuery(newText: String) {
        _movieSuggestionsQuery.value = newText
    }


    fun getMovieRecentQueries(): LiveData<List<MovieRecentQueries>> =
        discoverRepository.getMovieRecentQueries()

    fun deleteAllMovieRecentQueries() {
        discoverRepository.deleteAllMovieRecentQueries()
    }
}