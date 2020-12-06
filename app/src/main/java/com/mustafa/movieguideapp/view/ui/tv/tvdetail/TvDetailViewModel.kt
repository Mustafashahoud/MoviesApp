package com.mustafa.movieguideapp.view.ui.tv.tvdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.repository.tvs.TvRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject


@OpenForTesting
class TvDetailViewModel @Inject constructor(
    private val repository: TvRepository
) : ViewModel() {

    private val tvIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val keywordListLiveData = tvIdLiveData.switchMap { pageNumber ->
        repository.loadKeywords(pageNumber)
    }

    val videoListLiveData = tvIdLiveData.switchMap { pageNumber ->
        repository.loadVideos(pageNumber)
    }

    val reviewListLiveData = tvIdLiveData.switchMap { pageNumber ->
        repository.loadReviews(pageNumber)
    }

    fun setTvId(id: Int) = tvIdLiveData.postValue(id)
}
