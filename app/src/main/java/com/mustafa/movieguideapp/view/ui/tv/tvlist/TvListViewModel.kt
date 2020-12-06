package com.mustafa.movieguideapp.view.ui.tv.tvlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import com.mustafa.movieguideapp.repository.tvs.TvsRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject

@OpenForTesting
class TvListViewModel @Inject
constructor(repository: TvsRepository) :
    ViewModel() {

    val tvsStream =
        repository.loadPopularTvs()
            .map { pagingData -> pagingData.filter { it.poster_path != null } }
            .cachedIn(viewModelScope)
}

