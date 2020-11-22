package com.mustafa.movieguideapp.view.ui.person.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.models.MoviePerson
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.TvPerson
import com.mustafa.movieguideapp.repository.people.PeopleRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


@OpenForTesting
class PersonDetailViewModel @Inject constructor(
    private val repository: PeopleRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private val personIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val personLiveData = personIdLiveData.switchMap {
        launchOnViewModelScope {
            personIdLiveData.value?.let {
                repository.loadPersonDetail(it).asLiveData()
            } ?: AbsentLiveData.create()
        }
    }

    fun postPersonId(id: Int) = personIdLiveData.postValue(id)

    private val personId = MutableLiveData<Int>()
    val moviesOfCelebrity: LiveData<Resource<List<MoviePerson>>> = personId.switchMap {
        launchOnViewModelScope {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.loadMoviesForPerson(personId = personId.value!!).asLiveData()
            }
        }
    }


    val tvsOfCelebrity: LiveData<Resource<List<TvPerson>>> = personId.switchMap {
        launchOnViewModelScope {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.loadTvsForPerson(personId = personId.value!!).asLiveData()
            }
        }
    }

    fun setPersonId(id: Int) {
        personId.value = id
    }
}
