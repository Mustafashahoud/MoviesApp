package com.mustafa.movieapp.extension

import android.view.View
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.Status
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
