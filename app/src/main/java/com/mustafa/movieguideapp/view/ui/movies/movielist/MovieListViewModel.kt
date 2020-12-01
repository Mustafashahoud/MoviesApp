package com.mustafa.movieguideapp.view.ui.movies.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import com.mustafa.movieguideapp.repository.movies.MoviesRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject

@OpenForTesting
class MovieListViewModel @Inject constructor(
    repository: MoviesRepository
) : ViewModel() {

    val moviesStream =
        repository.loadPopularMovies()
            .map { pagingData -> pagingData.filter { it.poster_path != null } }
            .cachedIn(viewModelScope)

}
