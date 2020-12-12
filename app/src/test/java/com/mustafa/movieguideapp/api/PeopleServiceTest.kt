package com.mustafa.movieguideapp.api

import com.mustafa.movieguideapp.utils.LiveDataTestUtil.getValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PeopleServiceTest : ApiHelperAbstract<PeopleService>() {

    private lateinit var service: PeopleService

    @Before
    fun initService() {
        this.service = createService(PeopleService::class.java)
    }

    @Test
    fun fetchPersonListTest() {
        enqueueResponse("/people.json")
        val response = getValue(service.fetchPopularPeople(1)) as ApiSuccessResponse
        assertThat(response.body.results[0].id, `is`(28782))
        assertThat(response.body.total_pages, `is`(984))
        assertThat(response.body.total_results, `is`(19671))
    }

    @Test
    fun fetchPersonDetail() {
        enqueueResponse("person.json")
        val response = getValue(service.fetchPersonDetail(123)) as ApiSuccessResponse
        assertThat(response.body.birthday, `is`("1963-12-18"))
        assertThat(response.body.known_for_department, `is`("Acting"))
        assertThat(response.body.place_of_birth, `is`("Shawnee, Oklahoma, USA"))
    }

    @Test
    fun fetchPersonMovies() {
        enqueueResponse("person_movies.json")
        val response = getValue(service.fetchPersonMovies(116)) as ApiSuccessResponse
        assertThat(response.body.cast[0].title, `is`("Atonement"))
        assertThat(response.body.cast[0].character, `is`("Cecilia Tallis"))
        assertThat(response.body.id, `is`(116))
        assertThat(response.body.cast[0].id, `is`(4347))
    }

    @Test
    fun fetchPersonTvs() {
        enqueueResponse("person_tvs.json")
        val response = getValue(service.fetchPersonTvs(116)) as ApiSuccessResponse
        assertThat(response.body.cast[0].name, `is`("The Ellen DeGeneres Show"))
        assertThat(response.body.cast[0].first_air_date, `is`("2003-09-08"))
        assertThat(response.body.id, `is`(116))
        assertThat(response.body.cast[0].id, `is`(562))
    }

    @Test
    fun searchPeople() {
        enqueueResponse("search_people.json")
        val response = getValue(service.searchPeople("Mustafa", 1)) as ApiSuccessResponse
        assertThat(response.body.results[0].name, `is`("Mustafa Shakir"))
        assertThat(response.body.total_results, `is`(301))
        assertThat(response.body.total_pages, `is`(16))
    }
}
