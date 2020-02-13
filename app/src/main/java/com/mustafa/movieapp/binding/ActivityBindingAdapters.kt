package com.mustafa.movieapp.binding

import android.app.Activity
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.mustafa.movieapp.R
import com.mustafa.movieapp.api.Api
import com.mustafa.movieapp.testing.OpenForTesting
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
@OpenForTesting
class ActivityBindingAdapters @Inject constructor(val activity: Activity) {
    @BindingAdapter(value = ["imageUrl", "imageRequestListener"], requireAll = false)
    fun bindImage(imageView: ImageView, url: String?, listener: RequestListener<Drawable?>?) {
        val movieUrl  = url?.let { Api.getPosterPath(it) }
        movieUrl?.let {
            Glide.with(activity)
                .load(it)
                .listener(listener)
                .into(imageView)}
    }
}