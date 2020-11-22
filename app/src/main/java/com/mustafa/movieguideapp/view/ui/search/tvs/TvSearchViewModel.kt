package com.mustafa.movieguideapp.view.ui.search.tvs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.repository.tvs.TvsRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenForTesting
class TvSearchViewModel @Inject constructor(
    private val repository: TvsRepository
) : ViewModel() {

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<Tv>>? = null


    fun searchTvs(queryString: String): Flow<PagingData<Tv>> {
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

    @FlowPreview
    fun getSuggestions(queryString: String): Flow<PagingData<Tv>> {
        return repository.getTvSuggestions(queryString).cachedIn(viewModelScope).debounce(1000L)
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