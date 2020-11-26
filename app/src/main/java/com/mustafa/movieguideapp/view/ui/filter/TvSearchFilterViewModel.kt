package com.mustafa.movieguideapp.view.ui.filter

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.repository.tvs.TvsRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject

@OpenForTesting
class TvSearchFilterViewModel @Inject constructor(
    private val repository: TvsRepository,
) : ViewModel() {

    private var pageFiltersNumber = 1

    private var filterData = FilterData()

    private val searchTvFilterPageLiveData: MutableLiveData<Int> = MutableLiveData()

    private val _totalTvsCount = MutableLiveData<String>()
    val totalTvsCount: LiveData<String> get() = _totalTvsCount

    val searchTvListFilterLiveData = searchTvFilterPageLiveData.switchMap {
        repository.loadFilteredTvs(filterData = filterData) {
            _totalTvsCount.postValue(it.toString())
        }.cachedIn(viewModelScope)
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