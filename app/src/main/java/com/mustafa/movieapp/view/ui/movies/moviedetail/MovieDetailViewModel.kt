package com.mustafa.movieapp.view.ui.movies.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.repository.MovieRepository
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.AbsentLiveData
import javax.inject.Inject


/**
 * Copied from https://github.com/skydoves/TheMovies
 */
@OpenForTesting
class MovieDetailViewModel @Inject constructor(private val repository: MovieRepository) :
    ViewModel() {

    private val movieIdLiveData: MutableLiveData<Int> = MutableLiveData()
    final val keywordListLiveData: LiveData<Resource<List<Keyword>>>
    final val videoListLiveData: LiveData<Resource<List<Video>>>
    final val reviewListLiveData: LiveData<Resource<List<Review>>>

    init {
        this.keywordListLiveData = movieIdLiveData.switchMap {
            movieIdLiveData.value?.let {
                repository.loadKeywordList(it)
            } ?: AbsentLiveData.create()
        }

        this.videoListLiveData = movieIdLiveData.switchMap {
            movieIdLiveData.value?.let {
                repository.loadVideoList(it)
            } ?: AbsentLiveData.create()
        }

        this.reviewListLiveData = movieIdLiveData.switchMap {
            movieIdLiveData.value?.let {
                repository.loadReviewsList(it)
            } ?: AbsentLiveData.create()
        }
    }

    fun postMovieId(id: Int?) = movieIdLiveData.postValue(id)
}
