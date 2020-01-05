package com.mustafa.movieapp.view.adapter


import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewPaginator3(
        val recyclerView: RecyclerView,
        private val isLoading: () -> Boolean,
        private val hasNext: () -> Boolean = { false }

) : RecyclerView.OnScrollListener() {

    private var currentPage: Int = 1
    private var loading = true
    private var isScrolling = false
    var threshold = 10
    private val TAG: String = RecyclerViewPaginator3::class.java.simpleName

    init {
        recyclerView.addOnScrollListener(this)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {
            val layoutManager = recyclerView.layoutManager
            layoutManager?.let {
                val visibleItemCount = it.childCount
                val totalItemCount = it.itemCount
                val firstVisibleItemPosition = when (layoutManager) {
                    is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                    is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                    else -> return
                }

                val lastVisibleItemPosition = when (layoutManager) {
                    is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                    is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
                    else -> return
                }

                if (!hasNext() || isLoading()) {
                    Log.d(TAG, "Loading or no data left")
                    return
                }

//                if (!isLoading() && lastVisibleItemPosition == totalItemCount - 1) {
                if (loading && (visibleItemCount + firstVisibleItemPosition + threshold) >= totalItemCount) {
                    loading = false
                    Log.d(TAG, "CurrentPage number BEFORE is: $currentPage")
                    currentPage++
                    loadMore(currentPage)
                    Log.d(TAG, "CurrentPage number AFTER is: $currentPage")
                    loading = true
                }
//                }
            }
        }
    }


    abstract fun loadMore(page: Int)
}