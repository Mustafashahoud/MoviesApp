package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.network.VideoListResponse

class VideoPagingChecker : NetworkPagingChecker<VideoListResponse> {
  override fun hasNextPage(response: VideoListResponse): Boolean {
    return false
  }
}
