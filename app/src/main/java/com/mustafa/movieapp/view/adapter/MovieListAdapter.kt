package com.mustafa.movieapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.mustafa.movieapp.R
import com.mustafa.movieapp.databinding.ItemMovieBinding
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.view.ui.common.RecyclerViewBase

class MovieListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        private val movieOnClickCallback: ((Movie) -> Unit)?
) : RecyclerViewBase<Movie, ItemMovieBinding>() {

    override fun createBinding(parent: ViewGroup): ItemMovieBinding {

        val binding = DataBindingUtil.inflate<ItemMovieBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_movie,
                parent,
                false,
                dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.movie?.let {
                movieOnClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: ItemMovieBinding, item: Movie) {
        binding.movie = item
    }
}