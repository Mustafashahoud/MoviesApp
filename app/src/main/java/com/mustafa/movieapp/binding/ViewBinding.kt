package com.mustafa.movieapp.binding

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import co.lujun.androidtagview.TagContainerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mustafa.movieapp.api.Api
import com.mustafa.movieapp.extension.bindResource
import com.mustafa.movieapp.extension.requestGlideListener
import com.mustafa.movieapp.extension.visible
import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.models.entity.Tv
import com.mustafa.movieapp.models.network.PersonDetail
import com.mustafa.movieapp.utils.KeywordListMapper

@BindingAdapter("visibilityByResource")
fun bindVisibilityByResource(view: View, resource: Resource<List<Any>>?) {
    view.bindResource(resource) {
        if (resource?.data?.isNotEmpty()!!) {
            view.visible()
        }
    }
}

@BindingAdapter("mapKeywordList")
fun bindMapKeywordList(view: TagContainerLayout, resource: Resource<List<Keyword>>?) {
    resource?.let {
        view.bindResource(resource) {
            if (it.data != null) {
                view.tags = KeywordListMapper.mapToStringList(it.data)
                if (it.data.isNotEmpty()) {
                    view.visible()
                }
            }
        }
    }
}

@BindingAdapter("biography")
fun bindBiography(view: TextView, resource: Resource<PersonDetail>?) {
    view.bindResource(resource) {
        view.text = resource?.data?.biography
    }
}

@BindingAdapter("nameTags")
fun bindTags(view: TagContainerLayout, resource: Resource<PersonDetail>?) {
    view.bindResource(resource) {
        view.tags = resource?.data?.also_known_as
        if (resource?.data?.also_known_as?.isNotEmpty()!!) {
            view.visible()
        }
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bindReleaseDate")
fun bindReleaseDate(view: TextView, movie: Movie) {
    view.text = "Release Date : ${movie.release_date}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bindAirDate")
fun bindAirDate(view: TextView, tv: Tv) {
    view.text = "First Air Date : ${tv.first_air_date}"
}

@BindingAdapter("bindBackDrop")
fun bindBackDrop(view: ImageView, movie: Movie) {
    if (movie.backdrop_path != null) {
        Glide.with(view.context).load(Api.getBackdropPath(movie.backdrop_path))
                .listener(view.requestGlideListener())
                .into(view)
    } else if (movie.poster_path != null) {
        Glide.with(view.context).load(Api.getBackdropPath(movie.poster_path))
                .listener(view.requestGlideListener())
                .into(view)
    }
}

@BindingAdapter("bindBackDrop")
fun bindBackDrop(view: ImageView, tv: Tv) {
    if (tv.backdrop_path != null) {
        Glide.with(view.context).load(Api.getBackdropPath(tv.backdrop_path))
                .listener(view.requestGlideListener())
                .into(view)
    } else {
        Glide.with(view.context).load(tv.poster_path?.let { Api.getBackdropPath(it) })
                .listener(view.requestGlideListener())
                .into(view)
    }
}

@BindingAdapter("bindBackDrop")
fun bindBackDrop(view: ImageView, person: Person) {
    if (person.profile_path != null) {
        Glide.with(view.context).load(Api.getBackdropPath(person.profile_path))
                .apply(RequestOptions().circleCrop())
                .into(view)
    }
}
