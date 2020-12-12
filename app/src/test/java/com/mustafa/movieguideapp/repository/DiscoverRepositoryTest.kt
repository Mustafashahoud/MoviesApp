package com.mustafa.movieguideapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.*
import com.mustafa.movieguideapp.models.network.DiscoverMovieResponse
import com.mustafa.movieguideapp.models.network.DiscoverTvResponse
import com.mustafa.movieguideapp.room.AppDatabase
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.room.TvDao
import com.mustafa.movieguideapp.util.ApiUtil.successCall
import com.mustafa.movieguideapp.util.InstantAppExecutors
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.*


@RunWith(JUnit4::class)
class DiscoverRepositoryTest {

    private lateinit var repository: DiscoverRepository
    private val movieDao = mock<MovieDao>()
    private val tvDao = mock<TvDao>()
    private val service = mock<TheDiscoverService>()

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val db = mock<AppDatabase>()
        `when`(db.movieDao()).thenReturn(movieDao)
        `when`(db.tvDao()).thenReturn(tvDao)
        whenever(db.runInTransaction(any())).thenCallRealMethod()
        repository = DiscoverRepository(service, movieDao, db, tvDao, InstantAppExecutors())
    }

    @Test
    fun loadMovieList() {
        val ids = arrayListOf(1, 2)

        val observer = mock<Observer<Resource<List<Movie>>>>()

        val dbResult = DiscoveryMovieResult(ids, 2)

        // fetchDiscoverMovie form network
        val mockResponse = DiscoverMovieResponse(1, emptyList(), 100, 1)
        val call = successCall(mockResponse)
        `when`(service.fetchDiscoverMovie(1)).thenReturn(call)

        val discoveryMovieResultLiveData = MutableLiveData<DiscoveryMovieResult>()
        `when`(movieDao.getDiscoveryMovieResultByPageLiveData(1)).thenReturn(
            discoveryMovieResultLiveData
        )

        val movies = MutableLiveData<List<Movie>>()
        `when`(movieDao.loadDiscoveryMovieListOrdered(ids)).thenReturn(movies)

        repository.loadMovies(1).observeForever(observer)
        verify(observer).onChanged(Resource.loading(null))
        verifyNoMoreInteractions(service)

        discoveryMovieResultLiveData.postValue(dbResult)

        val movieList = arrayListOf<Movie>()
        movies.postValue(movieList)
        verify(observer).onChanged(Resource.success(movieList, false))

    }

    @Test
    fun loadTvList() {

        val observer = mock<Observer<Resource<List<Tv>>>>()
        // fetchDiscoverMovie form network
        val mockResponse = DiscoverTvResponse(1, emptyList(), 100, 100)
        val call = successCall(mockResponse)
        `when`(service.fetchDiscoverTv(1)).thenReturn(call)

        val ids = arrayListOf(1, 2)
        val dbResult = DiscoveryTvResult(ids, 1)
        val dbResultLiveData = MutableLiveData<DiscoveryTvResult>()
        `when`(tvDao.getDiscoveryTvResultByPageLiveData(1)).thenReturn(dbResultLiveData)

        val tvsLiveData = MutableLiveData<List<Tv>>()
        `when`(tvDao.loadDiscoveryTvListOrdered(ids)).thenReturn(tvsLiveData)

        repository.loadTvs(1).observeForever(observer)
        verify(observer).onChanged(Resource.loading(null))
        verifyNoMoreInteractions(service)

        dbResultLiveData.postValue(dbResult)
        val tvs = arrayListOf<Tv>()
        tvsLiveData.postValue(tvs)
        Mockito.verify(observer).onChanged(Resource.success(tvs, true))
    }

    @Test
    fun loadFilteredMovieList() {
        val ids = arrayListOf(1, 2)

        val observer = mock<Observer<Resource<List<Movie>>>>()

        val dbResult = FilteredMovieResult(ids, 2)

        // fetchDiscoverMovie form network
        val mockResponse = DiscoverMovieResponse(1, emptyList(), 100, 1)
        val call = successCall(mockResponse)
        `when`(
            service.searchMovieFilters(
                nullable(Int::class.java),
                nullable(String::class.java),
                nullable(Int::class.java),
                nullable(String::class.java),
                nullable(String::class.java),
                nullable(String::class.java),
                nullable(Int::class.java),
                nullable(String::class.java),
                anyInt()
            )
        ).thenReturn(call)

        val filteredMovieResultLiveData = MutableLiveData<FilteredMovieResult>()
        `when`(movieDao.getFilteredMovieResultByPageLiveData(1)).thenReturn(
            filteredMovieResultLiveData
        )

        val movies = MutableLiveData<List<Movie>>()
        `when`(movieDao.loadFilteredMovieListOrdered(ids)).thenReturn(movies)

        repository.loadFilteredMovies(FilterData(), 1){}
            .observeForever(observer)
        verify(observer).onChanged(Resource.loading(null))
        verifyNoMoreInteractions(service)

        filteredMovieResultLiveData.postValue(dbResult)

        val movieList = arrayListOf<Movie>()
        movies.postValue(movieList)
        verify(observer).onChanged(Resource.success(movieList, false))

    }

    @Test
    fun loadFilteredTvList() {
        val ids = arrayListOf(1, 2)

        val observer = mock<Observer<Resource<List<Tv>>>>()

        val dbResult = FilteredTvResult(ids, 2)

        // fetchDiscoverMovie form network
        val mockResponse = DiscoverTvResponse(1, emptyList(), 100, 1)
        val call = successCall(mockResponse)
        `when`(
            service.searchTvFilters(
                nullable(Int::class.java),
                nullable(String::class.java),
                nullable(Int::class.java),
                nullable(String::class.java),
                nullable(String::class.java),
                nullable(String::class.java),
                nullable(Int::class.java),
                anyInt()
            )
        ).thenReturn(call)

        val filteredTvResultLiveData = MutableLiveData<FilteredTvResult>()
        `when`(tvDao.getFilteredTvResultByPageLiveData(1)).thenReturn(
            filteredTvResultLiveData
        )

        val tvs = MutableLiveData<List<Tv>>()
        `when`(movieDao.loadFilteredTvListOrdered(ids)).thenReturn(tvs)

        repository.loadFilteredTvs(FilterData(), 1) {}
            .observeForever(observer)
        verify(observer).onChanged(Resource.loading(null))
        verifyNoMoreInteractions(service)

        filteredTvResultLiveData.postValue(dbResult)

        val tvList = arrayListOf<Tv>()
        tvs.postValue(tvList)
        verify(observer).onChanged(Resource.success(tvList, false))
    }

}
