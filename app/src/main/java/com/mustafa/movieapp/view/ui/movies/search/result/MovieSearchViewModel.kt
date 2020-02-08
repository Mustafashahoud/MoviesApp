package com.mustafa.movieapp.view.ui.movies.search.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.RecentQueries
import com.mustafa.movieapp.repository.DiscoverRepository
import com.mustafa.movieapp.utils.AbsentLiveData
import java.util.*
import javax.inject.Inject

class MovieSearchViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository
) : ViewModel() {

    private var query: String? = null
    private val _query = MutableLiveData<String>()
    private val searchMoviePageLiveData: MutableLiveData<Int> = MutableLiveData()
    private var pageNumber = 1

    val queryLiveData: LiveData<String> = _query



    val searchMovieListLiveData: LiveData<Resource<List<Movie>>> = Transformations
        .switchMap(searchMoviePageLiveData) {
            if (it == null || queryLiveData.value == null) {
                AbsentLiveData.create()
            } else {
                discoverRepository.searchMovies(queryLiveData.value!!, it)
            }
        }

    fun setSearchMovieQueryAndPage(query: String?, page: Int) {
        val input = query?.toLowerCase(Locale.getDefault())?.trim()
        if (input == queryLiveData.value) {
            return
        }
        _query.value = input
        searchMoviePageLiveData.value = page
    }

    fun setSearchMoviePage(page: Int) {
        setSearchMovieQueryAndPage(query, page)
    }

    fun loadMore() {
        pageNumber++
        searchMoviePageLiveData.value = pageNumber
    }

    fun refresh() {
        searchMoviePageLiveData.value?.let {
            searchMoviePageLiveData.value = it
        }
    }

    fun resetPageNumber() {
        pageNumber = 1
    }


    private val _suggestionsQuery = MutableLiveData<String>()
    private val suggestionsQuery: LiveData<String> = _suggestionsQuery
    val suggestions: LiveData<List<Movie>> = Transformations
        .switchMap(suggestionsQuery) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                discoverRepository.getSuggestions(suggestionsQuery.value!!, 1)
            }
        }

    fun setSuggestionsQuery(newText: String) {
//        _query.value = input
        _suggestionsQuery.value = newText
    }


    fun getRecentQueries(): LiveData<List<RecentQueries>> = discoverRepository.getRecentQueries()
    fun deleteAllRecentQueries() {
        discoverRepository.deleteAllRecentQueries()
    }
}