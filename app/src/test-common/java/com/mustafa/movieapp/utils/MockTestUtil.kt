package com.mustafa.movieapp.utils

import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.models.entity.Tv
import com.mustafa.movieapp.models.network.PersonDetail

class MockTestUtil {
    companion object {
        fun mockMovie() = Movie(123)
        fun mockTv() = Tv(123)
        fun mockPerson() = Person(1, mockPersonDetail(), "/", false, 123, "", 0f, false)
        fun mockKeywordList(): List<Keyword> {
            val keywords = ArrayList<Keyword>()
            keywords.add(Keyword(100, "keyword0"))
            keywords.add(Keyword(101, "keyword1"))
            keywords.add(Keyword(102, "keyword2"))
            return keywords
        }

        fun mockVideoList(): List<Video> {
            val videos = ArrayList<Video>()
            videos.add(Video("123", "video0", "", "", 0, ""))
            videos.add(Video("123", "video0", "", "", 0, ""))
            return videos
        }

        fun mockReviewList(): List<Review> {
            val reviews = ArrayList<Review>()
            reviews.add(Review("123", "", "", ""))
            reviews.add(Review("123", "", "", ""))
            return reviews
        }

        fun mockPersonDetail(): PersonDetail {
            return PersonDetail("", "", "", emptyList(), "")
        }

        fun createMovies(count: Int): List<Movie> {
            return (0 until count).map {
                Movie(it)
            }
        }

        fun createTvs(count: Int): List<Tv> {
            return (0 until count).map {
                Tv(it)
            }
        }
    }
}
