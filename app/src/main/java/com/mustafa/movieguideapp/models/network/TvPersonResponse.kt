package com.mustafa.movieguideapp.models.network

import com.mustafa.movieguideapp.models.NetworkResponseModel
import com.mustafa.movieguideapp.models.entity.TvPerson

class TvPersonResponse(
    val cast: List<TvPerson>,
    val id : Int
) : NetworkResponseModel