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
class TvServiceTest : ApiHelperAbstract<TvService>() {

    private lateinit var service: TvService

    @Before
    fun initService() {
        this.service = createService(TvService::class.java)
    }

    @Throws(IOException::class)
    @Test
    fun fetchTvKeywordsTest() {
        enqueueResponse("/keywords.json")
        val response =
            LiveDataTestUtil.getValue(service.fetchKeywords(1)) as ApiSuccessResponse
        assertThat(response.body.id, `is`(100))
        assertThat(response.body.keywords[0].id, `is`(1992))
        assertThat(response.body.keywords[0].name, `is`("super hero"))
    }

    @Throws(IOException::class)
    @Test
    fun fetchTvVideosTest() {
        enqueueResponse("/movie_videos.json")
        val response =
            LiveDataTestUtil.getValue(service.fetchVideos(1)) as ApiSuccessResponse
        assertThat(response.body.id, `is`(550))
        assertThat(response.body.results[0].id, `is`("1"))
        assertThat(response.body.results[0].key, `is`("key"))
    }

    @Throws(IOException::class)
    @Test
    fun fetchTvReviewsTest() {
        enqueueResponse("/movie_reviews.json")
        val response =
            LiveDataTestUtil.getValue(service.fetchReviews(1)) as ApiSuccessResponse
        assertThat(response.body.id, `is`(297761))
        assertThat(response.body.results[0].id, `is`("1"))
        assertThat(response.body.results[0].author, `is`("Mustafa"))
    }
}
