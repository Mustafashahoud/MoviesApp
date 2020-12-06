package com.mustafa.movieguideapp.view.ui.search.tvs

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.repository.tvs.TvsRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import io.reactivex.Flowable
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenForTesting
class TvSearchViewModel @Inject constructor(
    private val repository: TvsRepository
) : ViewModel() {

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flowable<PagingData<Tv>>? = null


    fun searchTvs(queryString: String): Flowable<PagingData<Tv>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString

        // save the query
        saveQuery(currentQueryValue!!)

        val newResult = repository
            .searchTvs(queryString)
            .map { pagingData -> pagingData.filter { it.poster_path != null } }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }


    val querySuggestionLiveDta = MutableLiveData<String>()

    fun getSuggestions(): LiveData<PagingData<Tv>> {
        return querySuggestionLiveDta.switchMap { suggestionQuery ->
            repository.getTvSuggestions(suggestionQuery).map { pagingData ->
                pagingData.filter { it.poster_path != null }
            }.cachedIn(viewModelScope)
        }
    }

    fun setSuggestionQuery(query: String) {
        querySuggestionLiveDta.value = query
    }

    private final val isRecentQueriesChange = MutableLiveData<Boolean>()
    val tvRecentQueries: LiveData<List<String>> = isRecentQueriesChange.switchMap {
        if (it == true) {
            liveData(viewModelScope.coroutineContext) {
                val tvRecentQueries = repository.getTvRecentQueries()
                emit(tvRecentQueries)
            }
        } else {
            AbsentLiveData()
        }
    }

    fun notifyRecentQueriesCleared() = isRecentQueriesChange.postValue(false)
    fun notifyRecentQueriesChanged() = isRecentQueriesChange.postValue(true)


    private fun saveQuery(currentQueryValue: String) {
        viewModelScope.launch {
            repository.saveQuery(currentQueryValue)
        }
    }


    fun deleteAllTvRecentQueries() {
        viewModelScope.launch {
            repository.deleteAllTvRecentQueries()
        }
    }
}