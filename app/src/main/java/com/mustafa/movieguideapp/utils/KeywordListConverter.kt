package com.mustafa.movieguideapp.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mustafa.movieguideapp.models.Keyword

@Suppress("unused")
open class  KeywordListConverter {
    @TypeConverter
    fun fromString(value: String): List<Keyword>? {
        val listType = object : TypeToken<List<Keyword>>() {}.type
        return Gson().fromJson<List<Keyword>>(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Keyword>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
