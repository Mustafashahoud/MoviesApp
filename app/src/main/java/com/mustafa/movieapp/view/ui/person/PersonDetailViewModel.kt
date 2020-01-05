
package com.mustafa.movieapp.view.ui.person

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.network.PersonDetail
import com.mustafa.movieapp.repository.PeopleRepository
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.AbsentLiveData
import timber.log.Timber
import javax.inject.Inject


@OpenForTesting
class PersonDetailViewModel @Inject
constructor(private val repository: PeopleRepository) : ViewModel() {

  private val personIdLiveData: MutableLiveData<Int> = MutableLiveData()
  val personLiveData: LiveData<Resource<PersonDetail>>

  init {
    Timber.d("Injection : PersonDetailViewModel")

    personLiveData = personIdLiveData.switchMap {
      personIdLiveData.value?.let {
        repository.loadPersonDetail(it)
      } ?: AbsentLiveData.create()
    }
  }

  fun postPersonId(id: Int) = personIdLiveData.postValue(id)
}
