package com.mustafa.movieapp.view.ui.tv.tvdetail

import androidx.lifecycle.*
import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.repository.TvRepository
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.AbsentLiveData
import timber.log.Timber
import javax.inject.Inject


@OpenForTesting
class TvDetailViewModel @Inject
constructor(private val repository: TvRepository) : ViewModel() {

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
