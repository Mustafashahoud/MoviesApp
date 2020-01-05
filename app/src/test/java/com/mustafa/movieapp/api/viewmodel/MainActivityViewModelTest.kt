
package com.mustafa.movieapp.api.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.mustafa.movieapp.api.PeopleService
import com.mustafa.movieapp.api.TheDiscoverService
import com.mustafa.movieapp.api.api.ApiUtil
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.models.entity.Tv
import com.mustafa.movieapp.models.network.DiscoverMovieResponse
import com.mustafa.movieapp.repository.DiscoverRepository
import com.mustafa.movieapp.repository.PeopleRepository
import com.mustafa.movieapp.room.MovieDao
import com.mustafa.movieapp.room.PeopleDao
import com.mustafa.movieapp.room.TvDao
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockMovie
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockPerson
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockTv
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainActivityViewModelTest {

  private lateinit var viewModel: MovieListViewModel

  private lateinit var discoverRepository: DiscoverRepository
  private lateinit var peopleRepository: PeopleRepository

  private val movieDao = mock<MovieDao>()
  private val tvDao = mock<TvDao>()
  private val peopleDao = mock<PeopleDao>()

  private val discoverService = mock<TheDiscoverService>()
  private val peopleService = mock<PeopleService>()

  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()

  @Before
  fun init() {
    discoverRepository = DiscoverRepository(discoverService, movieDao, tvDao)
    peopleRepository = PeopleRepository(peopleService, peopleDao)
    viewModel = MovieListViewModel(
        discoverRepository,
        peopleRepository
    )
  }

  @Test
  fun loadMovieList() {
    val loadFromDB = MutableLiveData<List<Movie>>()
    whenever(movieDao.loadDiscoveryMovieList(1)).thenReturn(loadFromDB)

    val mockResponse = DiscoverMovieResponse(1, emptyList(), 100, 10)
    val call = ApiUtil.successCall(mockResponse)
    whenever(discoverService.fetchDiscoverMovie(1)).thenReturn(call)

    val data = viewModel.movieListLiveData
    val observer = mock<Observer<Resource<List<Movie>>>>()
    data.observeForever(observer)

    viewModel.setMoviePage(1)
    verify(movieDao).loadDiscoveryMovieList(1)
    verifyNoMoreInteractions(discoverService)

    val mockMovieList = ArrayList<Movie>()
    mockMovieList.add(mockMovie())
    loadFromDB.postValue(mockMovieList)
    verify(observer).onChanged(
        Resource.success(viewModel.getMovieListValues()!!.data, false)
    )
  }

  @Test
  fun loadTvList() {
    val loadFromDB = MutableLiveData<List<Tv>>()
    whenever(tvDao.getTvList(1)).thenReturn(loadFromDB)

    val data = viewModel.tvListLiveData
    val observer = mock<Observer<Resource<List<Tv>>>>()
    data.observeForever(observer)

    viewModel.postTvPage(1)
    verify(tvDao).getTvList(1)
    verifyNoMoreInteractions(discoverService)

    val mockTvList = ArrayList<Tv>()
    mockTvList.add(mockTv())
    loadFromDB.postValue(mockTvList)
    verify(observer).onChanged(
        Resource.success(viewModel.getTvListValues()!!.data, false)
    )
  }

  @Test
  fun loadPeople() {
    val loadFromDB = MutableLiveData<List<Person>>()
    whenever(peopleDao.getPeople(1)).thenReturn(loadFromDB)

    val data = viewModel.peopleLiveData
    val observer = mock<Observer<Resource<List<Person>>>>()
    data.observeForever(observer)

    viewModel.postPeoplePage(1)
    verify(peopleDao).getPeople(1)
    verifyNoMoreInteractions(peopleService)

    val mockPeople = ArrayList<Person>()
    mockPeople.add(mockPerson())
    loadFromDB.postValue(mockPeople)
    verify(observer).onChanged(
        Resource.success(viewModel.getPeopleValues()!!.data, false)
    )
  }
}
