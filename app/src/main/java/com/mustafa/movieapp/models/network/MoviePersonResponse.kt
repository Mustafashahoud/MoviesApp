package com.mustafa.movieapp.models.network

import com.mustafa.movieapp.models.NetworkResponseModel
import com.mustafa.movieapp.models.entity.MoviePerson

class MoviePersonResponse(
    val cast: List<MoviePerson>,
    val id : Int
) : NetworkResponseModel