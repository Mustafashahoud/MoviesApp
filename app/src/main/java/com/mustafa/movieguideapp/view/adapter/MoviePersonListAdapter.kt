package com.mustafa.movieguideapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ItemMovieForCelebrityBinding
import com.mustafa.movieguideapp.models.entity.MoviePerson
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import com.mustafa.movieguideapp.view.ui.common.DataBoundListAdapter

class MoviePersonListAdapter(
    appExecutors: AppExecutors,
    private val dataBindingComponent: DataBindingComponent,
    private val movieOnClickCallback: ((MoviePerson) -> Unit)?
) : DataBoundListAdapter<MoviePerson, ItemMovieForCelebrityBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<MoviePerson>() {
        override fun areItemsTheSame(oldItem: MoviePerson, newItem: MoviePerson): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MoviePerson, newItem: MoviePerson): Boolean {
            return oldItem.id == newItem.id
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ItemMovieForCelebrityBinding {
        val binding = DataBindingUtil.inflate<ItemMovieForCelebrityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_movie_for_celebrity,
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

    override fun bind(binding: ItemMovieForCelebrityBinding, item: MoviePerson) {
        binding.movie = item
    }
}