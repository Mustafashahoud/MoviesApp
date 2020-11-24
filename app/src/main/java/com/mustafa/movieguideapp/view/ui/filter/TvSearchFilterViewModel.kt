package com.mustafa.movieguideapp.view.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.repository.tvs.TvsRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ViewModelBase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@OpenForTesting
class TvSearchFilterViewModel @Inject constructor(
    private val repository: TvsRepository,
    dispatcher: CoroutineDispatcher
) : ViewModelBase(dispatcher) {

    private var pageFiltersNumber = 1

    private var filterData = FilterData()

    private val searchTvFilterPageLiveData: MutableLiveData<Int> = MutableLiveData()

    private val _totalTvsCount = MutableLiveData<String>()
    val totalTvsCount: LiveData<String> get() = _totalTvsCount

    val searchTvListFilterLiveData = liveData {
        emitSource(repository.loadFilteredTvs(filterData = filterData) {
            _totalTvsCount.postValue(it.toString())
        }.cachedIn(viewModelScope))
    }

    fun setFilters(
        filterData: FilterData,
        page: Int
    ) {
        this.filterData = filterData
        searchTvFilterPageLiveData.value = page
    }

    fun resetFilterValues() {
        filterData = FilterData()
        this.pageFiltersNumber = 1
    }

}