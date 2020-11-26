package com.mustafa.movieguideapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mustafa.movieguideapp.models.entity.MovieRecentQueries
import com.mustafa.movieguideapp.models.entity.PeopleRecentQueries
import com.mustafa.movieguideapp.models.entity.TvRecentQueries
import com.mustafa.movieguideapp.utils.StringListConverter

@Database(
    entities = [
        MovieRecentQueries::class,
        PeopleRecentQueries::class,
        TvRecentQueries::class],
    version = 1, exportSchema = false
)
@TypeConverters(
    value = [(StringListConverter::class)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun tvDao(): TvDao
    abstract fun peopleDao(): PeopleDao
}
