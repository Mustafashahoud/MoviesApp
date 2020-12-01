package com.mustafa.movieguideapp.view.ui.movies.moviedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.repository.movies.MovieRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject


@OpenForTesting
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val movieIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val keywordListLiveData = movieIdLiveData.switchMap { id ->
        repository.loadKeywords(id)
    }

    val videoListLiveData = movieIdLiveData.switchMap { id ->
        repository.loadVideos(id)
    }

    val reviewListLiveData = movieIdLiveData.switchMap { id ->
        repository.loadReviews(id)
    }


    fun setMovieId(id: Int) {
        movieIdLiveData.value = id
    }

}
