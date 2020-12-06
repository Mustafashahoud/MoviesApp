package com.mustafa.movieguideapp.view.ui.search.movies

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.repository.movies.MoviesRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
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

        // save the query
        saveQuery(currentQueryValue!!)


        val newResult = repository
            .searchMovies(queryString)
            .map { pagingData -> pagingData.filter { it.poster_path != null } }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    private fun saveQuery(currentQueryValue: String) {
        viewModelScope.launch {
            repository.saveQuery(currentQueryValue)
        }
    }

    val querySuggestionLiveDta = MutableLiveData<String>()
    fun getSuggestions(): LiveData<PagingData<Movie>> {
        return querySuggestionLiveDta.switchMap { suggestionQuery ->
            repository.getMovieSuggestions(suggestionQuery).map { pagingData ->
                pagingData.filter { it.poster_path != null }
            }.cachedIn(viewModelScope)
        }
    }

    fun setSuggestionQuery(query: String) {
        querySuggestionLiveDta.value = query
    }

    private final val isRecentQueriesChange = MutableLiveData<Boolean>()
    val movieRecentQueries: LiveData<List<String>> = isRecentQueriesChange.switchMap {
        if (it == true) {
            liveData(viewModelScope.coroutineContext) {
                val movieRecentQueries = repository.getMovieRecentQueries()
                emit(movieRecentQueries)
            }
        } else {
            AbsentLiveData()
        }
    }

    fun notifyRecentQueriesCleared() = isRecentQueriesChange.postValue(false)
    fun notifyRecentQueriesChanged() = isRecentQueriesChange.postValue(true)


    fun deleteAllMovieRecentQueries() {
        viewModelScope.launch {
            repository.deleteAllMovieRecentQueries()
        }
    }

}