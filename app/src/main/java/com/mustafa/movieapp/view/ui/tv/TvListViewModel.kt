package com.mustafa.movieapp.view.ui.tv

import androidx.lifecycle.*
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.Tv
import com.mustafa.movieapp.repository.DiscoverRepository
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.AbsentLiveData
import javax.inject.Inject

@OpenForTesting
class TvListViewModel @Inject
constructor(private val discoverRepository: DiscoverRepository) : ViewModel() {
    private var tvPageLiveData: MutableLiveData<Int> = MutableLiveData()
    val tvListLiveData: LiveData<Resource<List<Tv>>> = Transformations
            .switchMap(tvPageLiveData) {
                if (it == null) {
                    AbsentLiveData.create()
                } else {
                    discoverRepository.loadTvs(it)
                }
            }
    fun setTvPage(page: Int) {
        tvPageLiveData.postValue(page)
    }
}