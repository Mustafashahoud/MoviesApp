
package com.mustafa.movieapp.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieapp.extension.bindResource
import com.mustafa.movieapp.extension.visible
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.view.adapter.MovieListAdapter

import com.mustafa.movieapp.view.adapter.ReviewListAdapter
import com.mustafa.movieapp.view.adapter.VideoListAdapter


@BindingAdapter("adapterMovieList")
fun bindAdapterMovieList(view: RecyclerView, resource: Resource<List<Movie>>?) {
  view.bindResource(resource) {
    if (resource != null) {
      val adapter = view.adapter as? MovieListAdapter
      adapter?.submitList(resource.data)
    }
  }
}


@BindingAdapter("adapterVideoList")
fun bindAdapterVideoList(view: RecyclerView, resource: Resource<List<Video>>?) {
  view.bindResource(resource) {
    if (resource != null) {
      val adapter = view.adapter as? VideoListAdapter
      adapter?.addVideoList(resource)
      if (resource.data?.isNotEmpty()!!) {
        view.visible()
      }
    }
  }
}

@BindingAdapter("adapterReviewList")
fun bindAdapterReviewList(view: RecyclerView, resource: Resource<List<Review>>?) {
  view.bindResource(resource) {
    if (resource != null) {
      val adapter = view.adapter as? ReviewListAdapter
      adapter?.addReviewList(resource)
      if (resource.data?.isNotEmpty()!!) {
        view.visible()
      }
    }
  }
}



