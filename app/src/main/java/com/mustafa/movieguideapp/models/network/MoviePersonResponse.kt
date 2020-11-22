package com.mustafa.movieguideapp.models.network

import com.mustafa.movieguideapp.models.MoviePerson
import com.mustafa.movieguideapp.models.NetworkResponseModel

class MoviePersonResponse(
    val cast: List<MoviePerson>,
    val id : Int
) : NetworkResponseModel