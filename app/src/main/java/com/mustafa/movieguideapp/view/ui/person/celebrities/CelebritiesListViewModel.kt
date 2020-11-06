package com.mustafa.movieguideapp.view.ui.person.celebrities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Person
import com.mustafa.movieguideapp.repository.PeopleRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


@OpenForTesting
class CelebritiesListViewModel @Inject constructor(
    private val peopleRepository: PeopleRepository,
    dispatcherIO: CoroutineDispatcher
) : ViewModelBase(dispatcherIO) {

    private var pageNumber = 1
    private var peoplePageLiveData: MutableLiveData<Int> = MutableLiveData()

    val peopleLiveData: LiveData<Resource<List<Person>>> = peoplePageLiveData.switchMap {
        launchOnViewModelScope {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                peopleRepository.loadPeople(it).asLiveData()
            }
        }
    }

    init {
        peoplePageLiveData.value = 1
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