package com.mustafa.movieguideapp.extension

import android.view.View
import com.mustafa.movieguideapp.models.Resource
import org.jetbrains.anko.toast

fun View.bindResource(resource: Resource<*>?, onSuccess: () -> Unit) {
    resource?.let {
        when (resource) {
            is Resource.Loading -> Unit
            is Resource.Success -> onSuccess()
            is Resource.Error -> this.context.toast(resource.message.toString())
        }
    }
}

