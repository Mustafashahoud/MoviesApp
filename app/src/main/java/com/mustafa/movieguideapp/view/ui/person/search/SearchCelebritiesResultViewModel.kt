package com.mustafa.movieguideapp.view.ui.person.search

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.mustafa.movieguideapp.models.Person
import com.mustafa.movieguideapp.repository.people.PeopleRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@OpenForTesting
class SearchCelebritiesResultViewModel @Inject constructor(
    private val peopleRepository: PeopleRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<Person>>? = null


    fun searchPeople(queryString: String): Flow<PagingData<Person>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult = peopleRepository
            .searchPeople(queryString)
            .map { pagingData -> pagingData.filter { it.profile_path != null } }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }


    fun getSuggestions(queryString: String): Flow<PagingData<Person>> {
        return peopleRepository.getPeopleSuggestions(queryString).cachedIn(viewModelScope)
    }


    val peopleRecentQueries = liveData(viewModelScope.coroutineContext) {
        val movieRecentQueries = peopleRepository.getPeopleRecentQueries()
        emit(movieRecentQueries)
    }


    fun deleteAllPeopleRecentQueries() {
        viewModelScope.launch {
            peopleRepository.deleteAllPeopleRecentQueries()
        }
    }
}