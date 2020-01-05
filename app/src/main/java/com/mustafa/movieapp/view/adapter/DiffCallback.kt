package com.mustafa.movieapp.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.mustafa.movieapp.models.entity.Movie
import kotlinx.android.synthetic.main.fragment_movies.*


class MovieDiffCallback(
        var oldList: List<Movie>,
        var newList: List<Movie>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList.get(oldItemPosition).id == newList.get(newItemPosition).id

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMovie = oldList.get(oldItemPosition)
        val newMovie = newList.get(newItemPosition)
        return oldMovie == newMovie
    }
}