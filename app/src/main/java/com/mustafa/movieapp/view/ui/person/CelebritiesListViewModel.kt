package com.mustafa.movieapp.view.ui.person

import androidx.lifecycle.*
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.repository.PeopleRepository
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.AbsentLiveData
import javax.inject.Inject


@OpenForTesting
class CelebritiesListViewModel @Inject
constructor(private val peopleRepository: PeopleRepository) : ViewModel() {

    private var peoplePageLiveData: MutableLiveData<Int> = MutableLiveData()
    val peopleLiveData: LiveData<Resource<List<Person>>> = Transformations
            .switchMap(peoplePageLiveData) {
                if (it == null) {
                    AbsentLiveData.create()
                } else {
                    peopleRepository.loadPeople(it)
                }
            }

    fun setPeoplePage(page: Int) {
        peoplePageLiveData.postValue(page)
    }

}