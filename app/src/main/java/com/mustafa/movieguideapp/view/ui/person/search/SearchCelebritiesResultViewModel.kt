package com.mustafa.movieguideapp.view.ui.person.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.PeopleRecentQueries
import com.mustafa.movieguideapp.models.entity.Person
import com.mustafa.movieguideapp.repository.PeopleRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import java.util.*
import javax.inject.Inject


@OpenForTesting
class SearchCelebritiesResultViewModel @Inject
constructor(private val peopleRepository: PeopleRepository) : ViewModel() {

    private val searchPeoplePageLiveData: MutableLiveData<Int> = MutableLiveData()
    private var peoplePageNumber = 1

    private val _personQuery = MutableLiveData<String>()
    val queryPersonLiveData: LiveData<String> = _personQuery
    val searchPeopleListLiveData: LiveData<Resource<List<Person>>> = Transformations
        .switchMap(searchPeoplePageLiveData) {
            if (it == null || queryPersonLiveData.value == null) {
                AbsentLiveData.create()
            } else {
                peopleRepository.searchPeople(queryPersonLiveData.value!!, it)
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

    @Suppress("unused")
    fun resetPageNumber() {
        peoplePageNumber = 1
    }


    private val _peopleSuggestionsQuery = MutableLiveData<String>()
    private val peopleSuggestionsQuery: LiveData<String> = _peopleSuggestionsQuery
    val peopleSuggestions: LiveData<List<Person>> = Transformations
        .switchMap(peopleSuggestionsQuery) {
            if (it.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                peopleRepository.getPeopleSuggestionsFromRoom(peopleSuggestionsQuery.value!!)
            }
        }

    fun setPeopleSuggestionsQuery(newText: String) {
        _peopleSuggestionsQuery.value = newText
    }

    fun getPeopleRecentQueries(): LiveData<List<PeopleRecentQueries>> =
        peopleRepository.getPeopleRecentQueries()

    fun deleteAllPeopleRecentQueries() = peopleRepository.deleteAllPeopleRecentQueries()
}