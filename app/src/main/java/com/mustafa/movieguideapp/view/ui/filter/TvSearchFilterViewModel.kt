package com.mustafa.movieguideapp.view.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.repository.tvs.TvsRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@OpenForTesting
class TvSearchFilterViewModel @Inject constructor(
    private val discoverRepository: TvsRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private var pageFiltersNumber = 1

    private var filterData = FilterData()

    private val searchTvFilterPageLiveData: MutableLiveData<Int> = MutableLiveData()

    private val _totalTvsCount = MutableLiveData<String>()
    val totalTvsCount : LiveData<String> get() = _totalTvsCount

    val searchTvListFilterLiveData: LiveData<Resource<List<Tv>>> =
        searchTvFilterPageLiveData.switchMap { page ->
            launchOnViewModelScope {
                discoverRepository.loadFilteredTvs(
                    filterData = filterData,
                    page = page
                ) {
                    _totalTvsCount.postValue(it.toString())
                }.asLiveData()
            }
        }

    fun setFilters(
        filterData: FilterData,
        page: Int
    ) {
        this.filterData = filterData
        searchTvFilterPageLiveData.value = page
    }

    fun loadMoreFilters() {
        pageFiltersNumber++
        searchTvFilterPageLiveData.value = pageFiltersNumber
    }

    fun resetFilterValues() {
        filterData = FilterData()
        this.pageFiltersNumber = 1
    }


    fun refresh() {
        searchTvFilterPageLiveData.value?.let {
            searchTvFilterPageLiveData.value = it
        }
    }

}