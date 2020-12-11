package com.mustafa.movieguideapp.view.ui.person.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.MoviePerson
import com.mustafa.movieguideapp.models.entity.TvPerson
import com.mustafa.movieguideapp.repository.PeopleRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import javax.inject.Inject


@OpenForTesting
class PersonDetailViewModel @Inject constructor(
    private val repository: PeopleRepository,
) : ViewModel() {

    private val personId = MutableLiveData<Int>()

    val personLiveData = personId.switchMap {
        if (it == null) {
            AbsentLiveData.create()
        } else {
            repository.loadPersonDetail(it)
        }
    }
    val moviesOfCelebrity: LiveData<Resource<List<MoviePerson>>> = personId.switchMap {
        if (it == null) {
            AbsentLiveData.create()
        } else {
            repository.loadMoviesForPerson(personId = it)
        }
    }


    val tvsOfCelebrity: LiveData<Resource<List<TvPerson>>> = personId.switchMap {
        if (it == null) {
            AbsentLiveData.create()
        } else {
            repository.loadTvsForPerson(personId = it)
        }
    }

    fun setPersonId(id: Int) {
        personId.value = id
    }
}
