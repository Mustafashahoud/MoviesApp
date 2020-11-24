package com.mustafa.movieguideapp.view.ui.search.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.repository.movies.MoviesRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenForTesting
class MovieSearchViewModel @Inject constructor(
    private val repository: MoviesRepository,
    dispatcherIO: CoroutineDispatcher
) : ViewModelBase(dispatcherIO) {

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<Movie>>? = null


    fun searchMovies(queryString: String): Flow<PagingData<Movie>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult = repository
            .searchMovies(queryString)
            .map { pagingData -> pagingData.filter { it.poster_path != null } }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    fun getSuggestions(queryString: String): Flow<PagingData<Movie>> {
        return repository.getMovieSuggestions(queryString).cachedIn(viewModelScope)
    }

    val movieRecentQueries: LiveData<List<String>> = liveData(viewModelScope.coroutineContext) {
        val movieRecentQueries = repository.getMovieRecentQueries()
        emit(movieRecentQueries)
    }

    fun deleteAllMovieRecentQueries() {
        viewModelScope.launch {
            repository.deleteAllMovieRecentQueries()
        }
    }
}