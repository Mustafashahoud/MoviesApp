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
import com.mustafa.movieapp.models.entity.*
import com.mustafa.movieapp.models.network.PersonDetail
import com.mustafa.movieapp.utils.KeywordListMapper
import com.mustafa.movieapp.utils.StringUtils

@BindingAdapter("visibilityByResource")
fun bindVisibilityByResource(view: View, resource: Resource<List<Any>>?) {
    view.bindResource(resource) {
        if (resource?.data?.isNotEmpty()!!) {
            view.visible()
        }
    }
}

@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
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
    view.text = "Release Date: ${movie.release_date}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bindReleaseDate")
fun bindReleaseDateForMoviePerson(view: TextView, movie: MoviePerson) {
    view.text = "Release Date: ${movie.release_date}"
}


@SuppressLint("SetTextI18n")
@BindingAdapter("bindMovieGenre")
fun bindMovieGenre(view: TextView, movie: Movie) {
    view.text = "Genre: ${StringUtils.getMovieGenresById(movie.genre_ids)}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bindMovieGenreForMoviePerson")
fun bindMovieGenreForMoviePerson(view: TextView, movie: MoviePerson) {
    view.text = "Genre: ${StringUtils.getMovieGenresById(movie.genre_ids)}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bindAirDate")
fun bindAirDate(view: TextView, tv: Tv) {
    tv.first_air_date?.let { view.text = "First Air Date: ${tv.first_air_date}" }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bindAirDate")
fun bindAirDateForTvPerson(view: TextView, tv: TvPerson) {
    tv.first_air_date?.let { view.text = "First Air Date: ${tv.first_air_date}" }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bindTvGenre")
fun bindTvGenre(view: TextView, tv: Tv) {
    view.text = "Genre: ${StringUtils.getTvGenresById(tv.genre_ids)}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bindTvGenreForTvPerson")
fun bindTvGenreForTvPerson(view: TextView, tv: TvPerson) {
    view.text = "Genre: ${StringUtils.getTvGenresById(tv.genre_ids)}"
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

@BindingAdapter("bindBackDropForMoviePerson")
fun bindBackDropForMoviePerson(view: ImageView, movie: MoviePerson) {
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

@BindingAdapter("bindBackDropForTvPerson")
fun bindBackDropForTvPerson(view: ImageView, movie: TvPerson) {
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
fun bindBackDrop(view: ImageView, person: Person) {
    if (person.profile_path != null) {
        Glide.with(view.context).load(Api.getBackdropPath(person.profile_path))
            .apply(RequestOptions().circleCrop())
            .into(view)
    }
    @BindingAdapter("setCharacterForTvPerson")
    fun setCharacterForTv(textView: TextView, tv: TvPerson) {
        textView.text = tv.let {
            if (tv.character.isNotEmpty()) "Ch.: ${tv.character}"
            else {
//                textView.visibility = View.GONE
                ""
            }
        }
    }

    @BindingAdapter("setCharacterForMoviePerson")
    fun setCharacterForMovie(textView: TextView, movie: MoviePerson) {
        textView.text = movie.let {
            if (movie.character.isNotEmpty()) "Ch.: ${movie.character}"
            else {
//                textView.visibility = View.GONE
                ""
            }
        }
    }
}
