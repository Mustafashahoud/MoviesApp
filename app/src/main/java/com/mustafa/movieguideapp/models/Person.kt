package com.mustafa.movieguideapp.models

import android.os.Parcelable
import androidx.room.Embedded
import kotlinx.parcelize.Parcelize

@Parcelize
data class Person(
    var page: Int,
    @Embedded
    var personDetail: PersonDetail? = null,
    val profile_path: String?,
    val adult: Boolean,
    val id: Int,
    val name: String,
    val popularity: Float,
    var search: Boolean
) : Parcelable
