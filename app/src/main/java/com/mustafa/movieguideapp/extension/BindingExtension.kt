package com.mustafa.movieguideapp.extension

import android.view.View
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Status
import org.jetbrains.anko.toast

fun View.bindResource(resource: Resource<Any>?, onSuccess: () -> Unit) {
    if (resource != null) {
        when (resource.status) {
            Status.LOADING -> Unit
            Status.SUCCESS -> onSuccess()
            Status.ERROR -> this.context.toast(resource.message.toString())
        }
    }
}
