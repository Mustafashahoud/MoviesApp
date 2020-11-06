package com.mustafa.movieguideapp.view.ui.tv.tvlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@OpenForTesting
class TvListViewModel @Inject
constructor(private val discoverRepository: DiscoverRepository, dispatcherIO: CoroutineDispatcher) :
    ViewModelBase(dispatcherIO) {

    private var pageNumber = 1

    private var tvPageLiveData: MutableLiveData<Int> = MutableLiveData()

    val tvListLiveData = tvPageLiveData.switchMap { pageNumber ->
        launchOnViewModelScope {
            discoverRepository.loadTvs(pageNumber).asLiveData()
        }
    }

    fun setTvPage(page: Int) {
        tvPageLiveData.value = page
    }

    init {
        tvPageLiveData.value = 1
    }

    fun loadMore() {
        pageNumber++
        tvPageLiveData.value = pageNumber
    }

    fun refresh() {
        tvPageLiveData.value?.let {
            tvPageLiveData.value = it
        }
    }
}