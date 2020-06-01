package com.mustafa.movieguideapp.view.ui.person.detail

import androidx.lifecycle.*
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.MoviePerson
import com.mustafa.movieguideapp.models.entity.TvPerson
import com.mustafa.movieguideapp.repository.PeopleRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import javax.inject.Inject


@OpenForTesting
class PersonDetailViewModel @Inject
constructor(private val repository: PeopleRepository) : ViewModel() {

    private val personIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val personLiveData = personIdLiveData.switchMap {
        personIdLiveData.value?.let {
            repository.loadPersonDetail(it)
        } ?: AbsentLiveData.create()
    }

    fun postPersonId(id: Int) = personIdLiveData.postValue(id)

    private val personId = MutableLiveData<Int>()
    val moviesOfCelebrity: LiveData<Resource<List<MoviePerson>>> = Transformations
        .switchMap(personId) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.loadMoviesForPerson(personId = personId.value!!)
            }
        }


    val tvsOfCelebrity: LiveData<Resource<List<TvPerson>>> = Transformations
        .switchMap(personId) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.loadTvsForPerson(personId = personId.value!!)
            }
        }

    fun setPersonId(id: Int) {
        personId.value = id
    }
}
