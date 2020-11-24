package com.mustafa.movieguideapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ItemMovieSearchBinding
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.utils.MovieDiffUtilCallBack

class FilteredMoviesAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val movieOnClickCallback: ((Movie) -> Unit)?
) : PagingDataAdapter<Movie, FilteredMoviesAdapter.ViewHolder>(MovieDiffUtilCallBack()) {
    class ViewHolder(private val binding: ItemMovieSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movie = movie
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMovieSearchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_movie_search,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.setOnClickListener {
            binding.movie?.let {
                movieOnClickCallback?.invoke(it)
            }
        }

        return ViewHolder(binding)
    }
}