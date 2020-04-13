package com.mustafa.movieapp.view.ui.tv.tvdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.repository.TvRepository
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.AbsentLiveData
import timber.log.Timber
import javax.inject.Inject

/**
 * Copied from https://github.com/skydoves/TheMovies
 */
@OpenForTesting
class TvDetailViewModel @Inject
constructor(private val repository: TvRepository) : ViewModel() {

    private val tvIdLiveData: MutableLiveData<Int> = MutableLiveData()
    final val keywordListLiveData: LiveData<Resource<List<Keyword>>>
    final val videoListLiveData: LiveData<Resource<List<Video>>>
    final val reviewListLiveData: LiveData<Resource<List<Review>>>

    init {
        Timber.d("Injection TvDetailViewModel")

        this.keywordListLiveData = tvIdLiveData.switchMap {
            tvIdLiveData.value?.let {
                repository.loadKeywordList(it)
            } ?: AbsentLiveData.create()
        }

        this.videoListLiveData = tvIdLiveData.switchMap {
            tvIdLiveData.value?.let {
                repository.loadVideoList(it)
            } ?: AbsentLiveData.create()
        }

        this.reviewListLiveData = tvIdLiveData.switchMap {
            tvIdLiveData.value?.let {
                repository.loadReviewsList(it)
            } ?: AbsentLiveData.create()
        }
    }

    fun postTvId(id: Int) = tvIdLiveData.postValue(id)
}
