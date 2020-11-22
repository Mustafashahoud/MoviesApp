package com.mustafa.movieguideapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.mustafa.movieguideapp.models.Movie

class MovieDiffUtilCallBack : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
}