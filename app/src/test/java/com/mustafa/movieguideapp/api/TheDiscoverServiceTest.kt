package com.mustafa.movieguideapp.api

import com.mustafa.movieguideapp.utils.LiveDataTestUtil
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
class TheDiscoverServiceTest : ApiHelperAbstract<TheDiscoverService>() {

    private lateinit var service: TheDiscoverService

    @Before
    fun initService() {
        this.service = createService(TheDiscoverService::class.java)
    }


    @Test
    fun fetchMovieListTest() {
        enqueueResponse("/movie.json")
        val response =
            LiveDataTestUtil.getValue(service.fetchDiscoverMovie(1)) as ApiSuccessResponse
        assertThat(response.body.results[0].id, `is`(164558))
        assertThat(response.body.total_results, `is`(61))
        assertThat(response.body.total_pages, `is`(4))
    }

    @Throws(IOException::class)
    @Test
    fun fetchTvListTest() {
        enqueueResponse("/tv.json")
        val response =
            LiveDataTestUtil.getValue(service.fetchDiscoverTv(1)) as ApiSuccessResponse
        assertThat(response.body.results[0].id, `is`(61889))
        assertThat(response.body.total_results, `is`(61470))
        assertThat(response.body.total_pages, `is`(3074))
    }

    @Test
    fun searchMovies() {
        enqueueResponse("/search_movies.json")
        val response =
            LiveDataTestUtil.getValue(service.searchMovies("Vikings", 1)) as ApiSuccessResponse
        assertThat(response.body.results[0].id, `is`(42661))
        assertThat(response.body.results[0].title, `is`("The Vikings"))
        assertThat(response.body.total_results, `is`(39))
        assertThat(response.body.total_pages, `is`(2))
    }

    @Test
    fun searchTvs() {
        enqueueResponse("/search_tvs.json")
        val response =
            LiveDataTestUtil.getValue(service.searchTvs("Breaking bad", 1)) as ApiSuccessResponse
        assertThat(response.body.results[0].id, `is`(1396))
        assertThat(response.body.results[0].name, `is`("Breaking Bad"))
        assertThat(response.body.total_results, `is`(1))
        assertThat(response.body.total_pages, `is`(1))
    }
}
