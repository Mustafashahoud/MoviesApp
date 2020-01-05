
package com.mustafa.movieapp.api.api

import com.mustafa.movieapp.api.TvService
import com.mustafa.movieapp.utils.LiveDataTestUtil
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.IOException

class TvServiceTest : ApiAbstract<TvService>() {

  private lateinit var service: TvService

  @Before
  fun initService() {
    this.service = createService(TvService::class.java)
  }

  @Throws(IOException::class)
  @Test
  fun fetchTvKeywordsTest() {
    enqueueResponse("/tmdb_movie_keywords.json")
    val response = LiveDataTestUtil.getValue(service.fetchKeywords(1))
    assertThat(response.body?.id, CoreMatchers.`is`(550))
    assertThat(response.body?.keywords?.get(0)?.id, `is`(825))
    assertThat(response.body?.keywords?.get(0)?.name, `is`("support group"))
  }

  @Throws(IOException::class)
  @Test
  fun fetchTvVideosTest() {
    enqueueResponse("/tmdb_movie_videos.json")
    val response = LiveDataTestUtil.getValue(service.fetchVideos(1))
    assertThat(response.body?.id, `is`(550))
    assertThat(response.body?.results?.get(0)?.id, `is`("533ec654c3a36854480003eb"))
    assertThat(response.body?.results?.get(0)?.key, `is`("SUXWAEX2jlg"))
  }

  @Throws(IOException::class)
  @Test
  fun fetchTvReviewsTest() {
    enqueueResponse("/tmdb_movie_reviews.json")
    val response = LiveDataTestUtil.getValue(service.fetchReviews(1))
    assertThat(response.body?.id, `is`(297761))
    assertThat(response.body?.results?.get(0)?.id, `is`("57a814dc9251415cfb00309a"))
    assertThat(response.body?.results?.get(0)?.author, `is`("Frank Ochieng"))
  }
}
