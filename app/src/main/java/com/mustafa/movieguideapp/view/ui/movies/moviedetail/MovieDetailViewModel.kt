package com.mustafa.movieguideapp.view.ui.movies.moviedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.repository.MovieRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


@OpenForTesting
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private val movieIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val keywordListLiveData = movieIdLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            repository.loadKeywords(pageNumber).asLiveData()
        }
    }

    val videoListLiveData = movieIdLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            repository.loadVideos(pageNumber).asLiveData()
        }
    }

    val reviewListLiveData = movieIdLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            repository.loadReviews(pageNumber).asLiveData()
        }
    }


    fun setMovieId(id: Int?) {
        movieIdLiveData.value = id
    }

}
