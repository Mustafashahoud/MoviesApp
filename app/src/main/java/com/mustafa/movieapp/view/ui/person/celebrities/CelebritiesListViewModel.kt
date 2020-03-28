package com.mustafa.movieapp.view.ui.person.celebrities

import androidx.lifecycle.*
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.repository.PeopleRepository
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.AbsentLiveData
import javax.inject.Inject


@OpenForTesting
class CelebritiesListViewModel @Inject
constructor(private val peopleRepository: PeopleRepository) : ViewModel() {

    private var pageNumber = 1
    private var peoplePageLiveData: MutableLiveData<Int> = MutableLiveData()

    val peopleLiveData: LiveData<Resource<List<Person>>> = Transformations
            .switchMap(peoplePageLiveData) {
                if (it == null) {
                    AbsentLiveData.create()
                } else {
                    peopleRepository.loadPeople(it)
                }
            }
    init {
        peoplePageLiveData.value = 1
    }

    fun setPeoplePage(page: Int) {
        peoplePageLiveData.postValue(page)
    }
    fun loadMore() {
        pageNumber++
        peoplePageLiveData.value = pageNumber
    }

    fun refresh() {
        peoplePageLiveData.value?.let {
            peoplePageLiveData.value = it
        }
    }

}