package com.mustafa.movieguideapp.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mustafa.movieguideapp.models.entity.MovieRecentQueries

open class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson<List<String>>(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromMovieRecentQueries(movieRecentQueries: MovieRecentQueries?): String {
        return Gson().toJson(movieRecentQueries?.query)
    }

    @TypeConverter
    fun toMovieRecentQueries(string: String): MovieRecentQueries {
        return Gson().fromJson(string, MovieRecentQueries::class.java)
    }
}
