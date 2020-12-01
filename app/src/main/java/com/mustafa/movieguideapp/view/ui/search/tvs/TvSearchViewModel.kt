package com.mustafa.movieguideapp.view.ui.search.tvs

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.repository.tvs.TvsRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import io.reactivex.Flowable
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenForTesting
class TvSearchViewModel @Inject constructor(
    private val repository: TvsRepository
) : ViewModel() {

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flowable<PagingData<Tv>>? = null


    val querySuggestionLiveDta = MutableLiveData<String>()
    fun searchTvs(queryString: String): Flowable<PagingData<Tv>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult = repository
            .searchTvs(queryString)
            .map { pagingData -> pagingData.filter { it.poster_path != null } }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }


    fun setSuggestionQuery(query: String) {
        querySuggestionLiveDta.value = query
    }

    fun getSuggestions(): LiveData<PagingData<Tv>> {
        return querySuggestionLiveDta.switchMap {
            repository.getTvSuggestions(it).cachedIn(viewModelScope)
        }
    }

    val tvRecentQueries: LiveData<List<String>> = liveData(viewModelScope.coroutineContext) {
        val movieRecentQueries = repository.getTvRecentQueries()
        emit(movieRecentQueries)
    }

    fun deleteAllTvRecentQueries() {
        viewModelScope.launch {
            repository.deleteAllTvRecentQueries()
        }
    }
}