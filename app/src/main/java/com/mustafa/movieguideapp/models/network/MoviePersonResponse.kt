package com.mustafa.movieguideapp.models.network

import com.mustafa.movieguideapp.models.NetworkResponseModel
import com.mustafa.movieguideapp.models.entity.MoviePerson

class MoviePersonResponse(
    val cast: List<MoviePerson>,
    val id : Int
) : NetworkResponseModel