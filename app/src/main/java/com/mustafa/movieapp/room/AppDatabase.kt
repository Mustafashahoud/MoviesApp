package com.mustafa.movieapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mustafa.movieapp.models.entity.*
import com.mustafa.movieapp.utils.IntegerListConverter
import com.mustafa.movieapp.utils.KeywordListConverter
import com.mustafa.movieapp.utils.ReviewListConverter
import com.mustafa.movieapp.utils.StringListConverter
import com.mustafa.movieapp.utils.VideoListConverter

@Database(
    entities = [(Movie::class),
        (Tv::class),
        (Person::class),
        (SearchMovieResult::class),
        (DiscoveryMovieResult::class),
        (DiscoveryTvResult::class),
        (MovieRecentQueries::class),
        (SearchTvResult::class),
        (TvRecentQueries::class),
        (MovieSuggestionsFts::class),
        (TvSuggestionsFts::class),
        (FilteredMovieResult::class),
        (FilteredTvResult::class),
        (PeopleResult::class),
        (MoviePerson::class),
        (TvPerson::class),
        (MoviePersonResult::class),
        (TvPersonResult::class),
        (SearchPeopleResult::class),
        (PeopleRecentQueries::class),
        (PeopleSuggestionsFts::class)],
    version = 30, exportSchema = false
)
@TypeConverters(
    value = [(StringListConverter::class), (IntegerListConverter::class),
        (KeywordListConverter::class), (VideoListConverter::class), (ReviewListConverter::class)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun tvDao(): TvDao
    abstract fun peopleDao(): PeopleDao
}
