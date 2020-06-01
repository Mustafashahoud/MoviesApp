package com.mustafa.movieguideapp.mappers

import com.mustafa.movieguideapp.models.network.VideoListResponse

class VideoPagingChecker : NetworkPagingChecker<VideoListResponse> {
  override fun hasNextPage(response: VideoListResponse): Boolean {
    return false
  }
}
