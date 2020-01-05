
package com.mustafa.movieapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.models.entity.SearchMovieResult
import com.mustafa.movieapp.models.entity.Tv
import com.mustafa.movieapp.utils.IntegerListConverter
import com.mustafa.movieapp.utils.KeywordListConverter
import com.mustafa.movieapp.utils.ReviewListConverter
import com.mustafa.movieapp.utils.StringListConverter
import com.mustafa.movieapp.utils.VideoListConverter

@Database(entities = [(Movie::class), (Tv::class), (Person::class), (SearchMovieResult::class)],
  version = 13, exportSchema = false)
@TypeConverters(value = [(StringListConverter::class), (IntegerListConverter::class),
  (KeywordListConverter::class), (VideoListConverter::class), (ReviewListConverter::class)])
abstract class AppDatabase : RoomDatabase() {
  abstract fun movieDao(): MovieDao
  abstract fun tvDao(): TvDao
  abstract fun peopleDao(): PeopleDao
}
