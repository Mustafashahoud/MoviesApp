package com.mustafa.movieapp.models.network

import com.mustafa.movieapp.models.NetworkResponseModel
import com.mustafa.movieapp.models.entity.TvPerson

class TvPersonResponse(
    val cast: List<TvPerson>,
    val id : Int
) : NetworkResponseModel