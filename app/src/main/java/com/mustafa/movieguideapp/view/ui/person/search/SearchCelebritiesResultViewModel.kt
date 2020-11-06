package com.mustafa.movieguideapp.view.ui.person.search

import androidx.lifecycle.*
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.PeopleRecentQueries
import com.mustafa.movieguideapp.models.entity.Person
import com.mustafa.movieguideapp.repository.PeopleRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@OpenForTesting
class SearchCelebritiesResultViewModel @Inject constructor(
    private val peopleRepository: PeopleRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private val searchPeoplePageLiveData: MutableLiveData<Int> = MutableLiveData()
    private var peoplePageNumber = 1

    private val _personQuery = MutableLiveData<String>()
    val queryPersonLiveData: LiveData<String> = _personQuery
    val searchPeopleListLiveData: LiveData<Resource<List<Person>>> =
        searchPeoplePageLiveData.switchMap {
            launchOnViewModelScope {
                if (it == null || queryPersonLiveData.value == null) {
                    AbsentLiveData.create()
                } else {
                    peopleRepository.searchPeople(queryPersonLiveData.value!!, it).asLiveData()
                }
            }
        }


    fun setSearchPeopleQueryAndPage(query: String?, page: Int) {
        val input = query?.toLowerCase(Locale.getDefault())?.trim()
        if (input == queryPersonLiveData.value) {
            return
        }
        _personQuery.value = input
        searchPeoplePageLiveData.value = page
    }


    fun loadMore() {
        peoplePageNumber++
        searchPeoplePageLiveData.value = peoplePageNumber
    }

    fun refresh() {
        searchPeoplePageLiveData.value?.let {
            searchPeoplePageLiveData.value = it
        }
    }

    private val peopleSuggestionsQuery = MutableLiveData<String>()
    val peopleSuggestions: LiveData<List<Person>> = peopleSuggestionsQuery.switchMap {
        launchOnViewModelScope {
            if (it.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                peopleRepository.getPeopleSuggestionsFromRoom(peopleSuggestionsQuery.value!!)
            }
        }
    }

    fun setPeopleSuggestionsQuery(newText: String) {
        peopleSuggestionsQuery.value = newText
    }

    fun getPeopleRecentQueries(): LiveData<List<PeopleRecentQueries>> =
        launchOnViewModelScope {
            peopleRepository.getPeopleRecentQueries()
        }

    fun deleteAllPeopleRecentQueries() {
        viewModelScope.launch {
            peopleRepository.deleteAllPeopleRecentQueries()
        }
    }
}