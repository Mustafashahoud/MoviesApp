package com.mustafa.movieguideapp.view.ui.tv.tvdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.repository.tvs.TvRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


@OpenForTesting
class TvDetailViewModel @Inject constructor(
    private val repository: TvRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private val tvIdLiveData: MutableLiveData<Int> = MutableLiveData()

    val keywordListLiveData = tvIdLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            repository.loadKeywords(pageNumber).asLiveData()
        }
    }

    val videoListLiveData = tvIdLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            repository.loadVideos(pageNumber).asLiveData()
        }
    }

    val reviewListLiveData = tvIdLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            repository.loadReviews(pageNumber).asLiveData()
        }
    }


    fun setTvId(id: Int) = tvIdLiveData.postValue(id)
}
