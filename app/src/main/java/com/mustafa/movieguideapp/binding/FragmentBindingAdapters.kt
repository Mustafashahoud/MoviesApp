package com.mustafa.movieguideapp.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.api.Api
import com.mustafa.movieguideapp.extension.requestGlideListener
import com.mustafa.movieguideapp.models.*
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
@OpenForTesting
class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {
    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        Glide.with(fragment)
            // it won't be null, i am filtering the null values in viewModels
            .load(Api.getPosterPath(url!!))
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_error)
            .into(imageView)

    }

    @BindingAdapter("videoImg")
    fun bindVideoImg(imageView: ImageView, key: String) {
        Glide.with(fragment)
            .load(Api.getYoutubeThumbnailPath(key))
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_error)
            .into(imageView)

    }



    @BindingAdapter("bindBackDrop")
    fun bindBackDrop(view: ImageView, movie: Movie) {
        if (movie.backdrop_path != null) {
            Glide.with(fragment).load(Api.getBackdropPath(movie.backdrop_path))
                .listener(view.requestGlideListener())
                .into(view)
        } else if (movie.poster_path != null) {
            Glide.with(fragment).load(Api.getBackdropPath(movie.poster_path))
                .listener(view.requestGlideListener())
                .into(view)
        }
    }

    @BindingAdapter("bindBackDropForMoviePerson")
    fun bindBackDropForMoviePerson(view: ImageView, movie: MoviePerson) {
        if (movie.backdrop_path != null) {
            Glide.with(fragment).load(Api.getBackdropPath(movie.backdrop_path))
                .listener(view.requestGlideListener())
                .into(view)
        } else if (movie.poster_path != null) {
            Glide.with(fragment).load(Api.getBackdropPath(movie.poster_path))
                .listener(view.requestGlideListener())
                .into(view)
        }
    }

    @BindingAdapter("bindBackDrop")
    fun bindBackDrop(view: ImageView, tv: Tv) {
        if (tv.backdrop_path != null) {
            Glide.with(fragment).load(Api.getBackdropPath(tv.backdrop_path))
                .listener(view.requestGlideListener())
                .into(view)
        } else {
            Glide.with(fragment).load(tv.poster_path?.let { Api.getBackdropPath(it) })
                .listener(view.requestGlideListener())
                .into(view)
        }
    }

    @BindingAdapter("bindBackDropForTvPerson")
    fun bindBackDropForTvPerson(view: ImageView, movie: TvPerson) {
        if (movie.backdrop_path != null) {
            Glide.with(fragment).load(Api.getBackdropPath(movie.backdrop_path))
                .listener(view.requestGlideListener())
                .into(view)
        } else if (movie.poster_path != null) {
            Glide.with(fragment).load(Api.getBackdropPath(movie.poster_path))
                .listener(view.requestGlideListener())
                .into(view)
        }
    }

    @BindingAdapter("bindBackDrop")
    fun bindBackDrop(view: ImageView, person: Person) {
        if (person.profile_path != null) {
            Glide.with(fragment).load(Api.getBackdropPath(person.profile_path))
                .apply(RequestOptions().circleCrop())
                .into(view)
        }
    }

}
