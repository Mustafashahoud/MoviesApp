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
class TvSearchViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private val searchTvPageLiveData: MutableLiveData<Int> = MutableLiveData()
    private var tvsPageNumber = 1
    private val _tvQuery = MutableLiveData<String>()
    val queryTvLiveData: LiveData<String> = _tvQuery


    val searchTvListLiveData = searchTvPageLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            if (pageNumber == null || queryTvLiveData.value.isNullOrEmpty()) {
                AbsentLiveData.create()
            } else {
                discoverRepository.searchTvs(queryTvLiveData.value!!, pageNumber).asLiveData()
            }
        }
    }


    fun refresh() {
        searchTvPageLiveData.value?.let {
            searchTvPageLiveData.value = it
        }
    }

    fun loadMore() {
        tvsPageNumber++
        searchTvPageLiveData.value = tvsPageNumber
    }

    fun setSearchTvQueryAndPage(query: String?, page: Int) {
        val input = query?.toLowerCase(Locale.getDefault())?.trim()
        if (input == queryTvLiveData.value) {
            return
        }
        _tvQuery.value = input
        searchTvPageLiveData.value = page
    }

    private val _tvSuggestionsQuery = MutableLiveData<String>()
    private val tvSuggestionsQuery: LiveData<String> = _tvSuggestionsQuery
    val tvSuggestions = tvSuggestionsQuery
        .switchMap {
            launchOnViewModelScope {
                discoverRepository.getTvSuggestionsFromRoom(tvSuggestionsQuery.value!!)
            }
        }

    fun setTvSuggestionsQuery(newText: String?) {
        _tvSuggestionsQuery.value = newText
    }

    val tvRecentQueries = launchOnViewModelScope {
        discoverRepository.getTvRecentQueries()
    }

    fun deleteAllTvRecentQueries() {
        viewModelScope.launch {
            discoverRepository.deleteAllTvRecentQueries()
        }
    }
}