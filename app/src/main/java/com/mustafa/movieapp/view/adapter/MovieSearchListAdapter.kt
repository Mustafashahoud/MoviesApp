package com.mustafa.movieapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.mustafa.movieapp.R
import com.mustafa.movieapp.databinding.ItemMovieBinding
import com.mustafa.movieapp.databinding.ItemMovieSearchBinding
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.view.ui.common.RecyclerViewBase

class MovieSearchListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        private val movieOnClickCallback: ((Movie) -> Unit)?
) : RecyclerViewBase<Movie, ItemMovieSearchBinding>() {

    override fun createBinding(parent: ViewGroup): ItemMovieSearchBinding {

        val binding = DataBindingUtil.inflate<ItemMovieSearchBinding>(
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

        return binding
    }

    override fun bind(binding: ItemMovieSearchBinding, item: Movie) {
        binding.movie = item
    }
}