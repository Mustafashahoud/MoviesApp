package com.mustafa.movieguideapp.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.mustafa.movieguideapp.api.Api
import com.mustafa.movieguideapp.testing.OpenForTesting
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
@OpenForTesting
class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {
    @BindingAdapter(value = ["imageUrl", "imageRequestListener"], requireAll = false)
    fun bindImage(imageView: ImageView, url: String?, listener: RequestListener<Drawable?>?) {
        val movieUrl = url?.let { Api.getPosterPath(it) }
        movieUrl?.let {
            Glide.with(fragment)
                .load(it)
                .listener(listener)
                .into(imageView)
        }
    }
}
