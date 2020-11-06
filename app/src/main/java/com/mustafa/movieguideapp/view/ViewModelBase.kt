package com.mustafa.movieguideapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher

open class ViewModelBase (private val dispatcher: CoroutineDispatcher) : ViewModel() {
    internal fun <T> launchOnViewModelScope(block: suspend () -> LiveData<T>): LiveData<T> {
        return liveData(viewModelScope.coroutineContext + dispatcher) {
            emitSource(block())
        }
    }
}