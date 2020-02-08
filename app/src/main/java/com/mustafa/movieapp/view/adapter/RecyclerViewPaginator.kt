package com.mustafa.movieapp.view.adapter

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListViewModel
import com.mustafa.movieapp.view.ui.movies.search.result.MovieSearchViewModel


class RecyclerViewPaginator(
    val viewModel: ViewModel,
    recyclerView: RecyclerView,
    private val hasNext: () -> Boolean = { false }
) : RecyclerView.OnScrollListener() {
    private var previousTotal = 0
    private var loading = true

    init {
        recyclerView.addOnScrollListener(this)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager
        val visibleItemCount = recyclerView.childCount
        val totalItemCount = layoutManager!!.itemCount
//        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

        val firstVisibleItem = when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            else -> return
        }

        if (!hasNext()) return

        if (loading && totalItemCount > previousTotal) {
            loading = false
            previousTotal = totalItemCount
        }
        val visibleThreshold = 5
        if (!loading && totalItemCount - visibleItemCount
            <= firstVisibleItem + visibleThreshold
        ) {
            when(viewModel) {
                is MovieListViewModel -> viewModel.loadMore()
                is MovieSearchViewModel -> viewModel.loadMore()
            }
            loading = true
        }
    }

//    abstract fun onLoadMore(currentPage: Int)

//    fun resetCurrentPage() {
//        current_page = 1
//        previousTotal = 0
//        loading = true
//    }
}