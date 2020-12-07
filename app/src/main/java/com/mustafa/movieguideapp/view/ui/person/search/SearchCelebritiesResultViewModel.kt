package com.mustafa.movieguideapp.view.ui.person.search

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import com.mustafa.movieguideapp.models.Person
import com.mustafa.movieguideapp.repository.people.PeopleRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import io.reactivex.Flowable
import kotlinx.coroutines.launch
import javax.inject.Inject


@OpenForTesting
class SearchCelebritiesResultViewModel @Inject constructor(
    private val peopleRepository: PeopleRepository,
) : ViewModel() {

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flowable<PagingData<Person>>? = null


    fun searchPeople(queryString: String): Flowable<PagingData<Person>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString

        // save the query
        saveQuery(currentQueryValue!!)

        val newResult = peopleRepository
            .searchPeople(queryString)
            .map { pagingData -> pagingData.filter { it.profile_path != null } }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    private fun saveQuery(currentQueryValue: String) {
        viewModelScope.launch {
            peopleRepository.saveQuery(currentQueryValue)
        }
    }

    val querySuggestionLiveDta = MutableLiveData<String>()
    fun getSuggestions(): LiveData<PagingData<Person>> {
        return querySuggestionLiveDta.switchMap {
            peopleRepository.getPeopleSuggestions(it).map { pagingData ->
                pagingData.filter { person ->
                    person.profile_path != null
                }
            }.cachedIn(viewModelScope)
        }
    }

    fun setSuggestionQuery(query: String) {
        querySuggestionLiveDta.value = query
    }


    val peopleRecentQueries: LiveData<List<String>> = liveData(viewModelScope.coroutineContext) {
        val movieRecentQueries = peopleRepository.getPeopleRecentQueries()
        emit(movieRecentQueries)
    }


    fun deleteAllPeopleRecentQueries() {
        viewModelScope.launch {
            peopleRepository.deleteAllPeopleRecentQueries()
        }
    }
}