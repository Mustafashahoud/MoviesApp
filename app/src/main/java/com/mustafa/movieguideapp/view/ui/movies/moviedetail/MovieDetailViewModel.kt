package com.mustafa.movieguideapp.view.ui.movies.moviedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mustafa.movieguideapp.repository.MovieRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import javax.inject.Inject


@OpenForTesting
class MovieDetailViewModel @Inject constructor(private val repository: MovieRepository) :
    ViewModel() {

    private val movieIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val keywordListLiveData = Transformations.switchMap(movieIdLiveData) {
        movieIdLiveData.value?.let {
            repository.loadKeywordList(it)
        } ?: AbsentLiveData.create()
    }

    val videoListLiveData = Transformations.switchMap(movieIdLiveData) {
        movieIdLiveData.value?.let {
            repository.loadVideoList(it)
        } ?: AbsentLiveData.create()
    }

    val reviewListLiveData = Transformations.switchMap(movieIdLiveData) {
        movieIdLiveData.value?.let {
            repository.loadReviewsList(it)
        } ?: AbsentLiveData.create()
    }


    fun setMovieId(id: Int?) {
        movieIdLiveData.value = id
    }

}
