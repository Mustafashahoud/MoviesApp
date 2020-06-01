package com.mustafa.movieguideapp.utils

import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.models.entity.*
import com.mustafa.movieguideapp.models.network.PersonDetail

class MockTestUtil {
    companion object {

        fun mockMovie() = Movie(123)

        fun mockTv() = Tv(123)

        fun mockPerson() = Person(1, mockPersonDetail(), "/", false, 123, "MUSTAFA", 0f, false)

        fun mockMoviePerson() = MoviePerson(
            1,
            "Achilles",
            "",
            false,
            "/",
            false,
            "After Paris, a prince of Troy, has an affair with Menelaus's wife, Helen, he decides to take her with him. Later, Menelaus's brother uses that as an excuse to wage war against the city of Troy.",
            "2004",
            listOf(36),
            "",
            "",
            "Troy",
            "/",
        1F,
            1,
            1F
        )
        fun mockTvPerson() = TvPerson(
            1,
            "Marty Byrde",
            "",
            "",
            "Created by Bill Dubuque (\"The Accountant,\" \"The Judge\"), this drama series stars Jason Bateman as Marty Byrde, a financial planner who relocates his family from Chicago to a summer resort community in the Ozarks",
            "2017",
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
