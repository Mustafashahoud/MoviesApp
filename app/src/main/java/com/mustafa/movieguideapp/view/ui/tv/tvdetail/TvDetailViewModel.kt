package com.mustafa.movieguideapp.view.ui.tv.tvdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.repository.TvRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject


@OpenForTesting
class TvDetailViewModel @Inject constructor(
    private val repository: TvRepository
) : ViewModel() {

    private val tvIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val keywordListLiveData = tvIdLiveData.switchMap { id ->
        repository.loadKeywordList(id)
    }

    val videoListLiveData = tvIdLiveData.switchMap { id ->
        repository.loadVideoList(id)
    }

    val reviewListLiveData = tvIdLiveData.switchMap { id ->
        repository.loadReviewsList(id)
    }

    fun setTvId(id: Int) = tvIdLiveData.postValue(id)
}
