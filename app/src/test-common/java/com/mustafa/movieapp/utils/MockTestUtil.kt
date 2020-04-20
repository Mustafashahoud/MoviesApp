package com.mustafa.movieapp.utils

import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.models.entity.*
import com.mustafa.movieapp.models.network.PersonDetail

class MockTestUtil {
    companion object {

        fun mockMovie() = Movie(123)

        fun mockTv() = Tv(123)

        fun mockPerson() = Person(1, mockPersonDetail(), "/", false, 123, "MUSTAFA", 0f, false)

        fun mockMoviePerson() = MoviePerson(
            1, "",
            "",
            false,
            "",
            false,
            "",
            "",
            listOf(),
            "",
            "",
            "Troy",
            "/",
        1F,
            1,
            1F
        )
        fun mockTvPerson() = TvPerson(
            1, "",
            "",
            "",
            "",
            "",
            listOf(),
            "",
            "",
            "Ozark",
            1,
            "",
            1F,
            1,
            1F
        )
        fun mockKeywordList(): List<Keyword> {
            return listOf(
                Keyword(100, "keyword0"),
                Keyword(100, "keyword0"),
                Keyword(100, "keyword0")
            )
        }

        fun mockVideoList(): List<Video> {
            return listOf(
                Video("123", "video0", "", "", 0, ""),
                Video("123", "video0", "", "", 0, "")
            )
        }

        fun mockReviewList(): List<Review> {
            return listOf(
                Review("123", "Mustafa", "", ""),
                Review("123", "", "", "")
            )
        }

        fun mockPersonDetail(): PersonDetail {
            return PersonDetail(
                "1992",
                "Acting",
                "",
                listOf("Also_Known"),
                ""
            )
        }

        fun createMovies(count: Int): List<Movie> {
            return (0 until count).map {
                Movie(it, "Movie$it")
            }
        }

        fun createTvs(count: Int): List<Tv> {
            return (0 until count).map {
                Tv(it)
            }
        }

        fun createPeople(count: Int): List<Person> {
            return (0 until count).map {
                mockPerson()
            }
        }
    }
}
