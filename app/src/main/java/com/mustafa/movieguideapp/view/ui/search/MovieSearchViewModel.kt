package com.mustafa.movieguideapp.view.ui.search

import androidx.lifecycle.*
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@OpenForTesting
class MovieSearchViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    dispatcherIO: CoroutineDispatcher
) : ViewModelBase(dispatcherIO) {

    private val searchMoviePageLiveData: MutableLiveData<Int> = MutableLiveData()
    private var moviesPageNumber = 1
    private val _movieQuery = MutableLiveData<String>()

    val queryMovieLiveData: LiveData<String> = _movieQuery

    val searchMovieListLiveData = searchMoviePageLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            if (pageNumber == null || queryMovieLiveData.value.isNullOrEmpty()) {
                AbsentLiveData.create()
            } else {
                discoverRepository.searchMovies(queryMovieLiveData.value!!, pageNumber).asLiveData()
            }
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

    private val _movieSuggestionsQuery = MutableLiveData<String>()
    private val movieSuggestionsQuery: LiveData<String> = _movieSuggestionsQuery

    val movieSuggestions = movieSuggestionsQuery
        .switchMap {
            launchOnViewModelScope {
                discoverRepository.getMovieSuggestionsFromRoom(movieSuggestionsQuery.value!!)
            }
        }

    fun setMovieSuggestionsQuery(newText: String?) {
        _movieSuggestionsQuery.value = newText
    }


    val tvRecentQueries = launchOnViewModelScope {
        discoverRepository.getMovieRecentQueries()
    }


    fun deleteAllMovieRecentQueries() {
        viewModelScope.launch {
            discoverRepository.deleteAllMovieRecentQueries()
        }
    }
}