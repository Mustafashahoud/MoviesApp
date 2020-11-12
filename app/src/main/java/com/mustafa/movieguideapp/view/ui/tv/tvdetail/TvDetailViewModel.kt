package com.mustafa.movieguideapp.view.ui.tv.tvdetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mustafa.movieguideapp.repository.TvRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData


@OpenForTesting
class TvDetailViewModel @ViewModelInject constructor(
    private val repository: TvRepository
) : ViewModel() {

    private val tvIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val keywordListLiveData = Transformations
        .switchMap(tvIdLiveData) {
            tvIdLiveData.value?.let {
                repository.loadKeywordList(it)
            } ?: AbsentLiveData.create()
        }

    val videoListLiveData = Transformations
        .switchMap(tvIdLiveData) {
            tvIdLiveData.value?.let {
                repository.loadVideoList(it)
            } ?: AbsentLiveData.create()
        }

    val reviewListLiveData = Transformations.switchMap(tvIdLiveData) {
        tvIdLiveData.value?.let {
            repository.loadReviewsList(it)
        } ?: AbsentLiveData.create()
    }

    fun setTvId(id: Int) = tvIdLiveData.postValue(id)
}
