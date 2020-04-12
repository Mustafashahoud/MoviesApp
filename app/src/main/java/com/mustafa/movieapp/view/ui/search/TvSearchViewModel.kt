package com.mustafa.movieapp.view.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Tv
import com.mustafa.movieapp.models.entity.TvRecentQueries
import com.mustafa.movieapp.repository.DiscoverRepository
import com.mustafa.movieapp.utils.AbsentLiveData
import java.util.*
import javax.inject.Inject

class TvSearchViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository
) : ViewModel() {

    private val searchTvPageLiveData: MutableLiveData<Int> = MutableLiveData()
    private var tvsPageNumber = 1

    private val _tvQuery = MutableLiveData<String>()
    val queryTvLiveData: LiveData<String> = _tvQuery
    val searchTvListLiveData: LiveData<Resource<List<Tv>>> = Transformations
        .switchMap(searchTvPageLiveData) {
            if (it == null || queryTvLiveData.value.isNullOrEmpty()) {
                AbsentLiveData.create()
            } else {
                discoverRepository.searchTvs(queryTvLiveData.value!!, it)
            }
        }


    fun refresh() {
        searchTvPageLiveData.value?.let {
            searchTvPageLiveData.value = it
        }
    }

    fun resetPageNumber() {
        tvsPageNumber = 1
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
    val tvSuggestions: LiveData<List<Tv>> = Transformations
        .switchMap(tvSuggestionsQuery) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                discoverRepository.getTvSuggestionsFromRoom(tvSuggestionsQuery.value!!)
            }
        }

    fun setTvSuggestionsQuery(newText: String?) {
        _tvSuggestionsQuery.value = newText
    }

    fun getTvRecentQueries(): LiveData<List<TvRecentQueries>> = discoverRepository.getTvRecentQueries()
    fun deleteAllTvRecentQueries() {
        discoverRepository.deleteAllTvRecentQueries()
    }
}