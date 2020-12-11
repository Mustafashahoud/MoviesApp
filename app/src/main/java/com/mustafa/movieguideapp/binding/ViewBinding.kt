package com.mustafa.movieguideapp.binding

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout
import com.mustafa.movieguideapp.extension.bindResource
import com.mustafa.movieguideapp.extension.visible
import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.models.entity.MoviePerson
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.models.entity.TvPerson
import com.mustafa.movieguideapp.models.network.PersonDetail
import com.mustafa.movieguideapp.utils.KeywordListMapper
import com.mustafa.movieguideapp.utils.StringUtils
import com.mustafa.movieguideapp.view.adapter.ReviewListAdapter
import com.mustafa.movieguideapp.view.adapter.VideoListAdapter


/**
 *  Copied from https://github.com/skydoves/TheMovies
 */
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
@BindingAdapter("bindAirDate")
fun bindAirDateForTvPerson(view: TextView, tv: TvPerson) {
    tv.first_air_date?.let { view.text = "First Air Date: ${tv.first_air_date}" }
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
@BindingAdapter("bindTvGenre")
fun bindTvGenre(view: TextView, tv: Tv) {
    view.text = "Genre: ${StringUtils.getTvGenresById(tv.genre_ids)}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("bindTvGenreForTvPerson")
fun bindTvGenreForTvPerson(view: TextView, tv: TvPerson) {
    view.text = "Genre: ${StringUtils.getTvGenresById(tv.genre_ids)}"
}


@BindingAdapter("setCharacterForTvPerson")
fun setCharacterForTv(textView: TextView, tv: TvPerson) {
    textView.text = tv.let {
        if (tv.character.isNotEmpty()) "Ch.: ${tv.character}"
        else {
            textView.visibility = View.GONE
            ""
        }
    }
}

@BindingAdapter("setCharacterForMoviePerson")
fun setCharacterForMovie(textView: TextView, movie: MoviePerson) {
    textView.text = movie.let {
        if (movie.character.isNotEmpty()) "Ch.: ${movie.character}"
        else {
            textView.visibility = View.GONE
            ""
        }
    }
}


@BindingAdapter("adapterVideoList")
fun bindAdapterVideoList(view: RecyclerView, resource: Resource<List<Video>>?) {
    view.bindResource(resource) {
        if (resource != null) {
            val adapter = view.adapter as? VideoListAdapter
            adapter?.submitList(resource.data)
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
            adapter?.submitList(resource.data)
            if (resource.data?.isNotEmpty()!!) {
                view.visible()
            }
        }
    }
}
