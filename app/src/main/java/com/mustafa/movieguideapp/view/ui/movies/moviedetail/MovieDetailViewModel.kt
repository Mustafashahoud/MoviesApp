package com.mustafa.movieguideapp.view.ui.movies.moviedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.repository.MovieRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject


@OpenForTesting
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val movieIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val keywordListLiveData = movieIdLiveData.switchMap { id ->
        repository.loadKeywordList(id)
    }

    val videoListLiveData = movieIdLiveData.switchMap { id ->
        repository.loadVideoList(id)
    }

    val reviewListLiveData = movieIdLiveData.switchMap { id ->
        repository.loadReviewsList(id)
    }


    fun setMovieId(id: Int) {
        movieIdLiveData.postValue(id)
    }

}
