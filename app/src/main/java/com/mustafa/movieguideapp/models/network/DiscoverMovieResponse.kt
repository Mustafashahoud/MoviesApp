
package com.mustafa.movieguideapp.models.network

import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.models.NetworkResponseModel

data class DiscoverMovieResponse(
    val page: Int,
    val results: List<Movie>,
    val total_results: Int,
    val total_pages: Int
) : NetworkResponseModel
