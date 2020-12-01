package com.mustafa.movieguideapp.view.ui.search.movies

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.repository.movies.MoviesRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import io.reactivex.Flowable
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenForTesting
class MovieSearchViewModel @Inject constructor(
    private val repository: MoviesRepository,
) : ViewModel() {

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flowable<PagingData<Movie>>? = null


    fun searchMovies(queryString: String): Flowable<PagingData<Movie>> {
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

    val querySuggestionLiveDta = MutableLiveData<String>()
    fun getSuggestions(): LiveData<PagingData<Movie>> {
        return querySuggestionLiveDta.switchMap {
            repository.getMovieSuggestions(it).cachedIn(viewModelScope)
        }
    }

    fun setSuggestionQuery(query: String) {
        querySuggestionLiveDta.value = query
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