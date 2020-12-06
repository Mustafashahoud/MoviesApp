package com.mustafa.movieguideapp.view.ui.movies.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.repository.movies.MoviesRepository
import com.mustafa.movieguideapp.testing.OpenForTesting
import io.reactivex.Flowable
import javax.inject.Inject

@OpenForTesting
class MovieListViewModel @Inject constructor(
    repository: MoviesRepository
) : ViewModel() {


    val moviesStream: Flowable<PagingData<Movie>> =
        repository.loadPopularMovies()
            .map { pagingData -> pagingData.filter { it.poster_path != null } }
            .cachedIn(viewModelScope)


}
