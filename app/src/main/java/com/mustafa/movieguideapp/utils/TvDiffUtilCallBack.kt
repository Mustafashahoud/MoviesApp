package com.mustafa.movieguideapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.mustafa.movieguideapp.models.Tv

class TvDiffUtilCallBack : DiffUtil.ItemCallback<Tv>() {
    override fun areItemsTheSame(oldItem: Tv, newItem: Tv): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Tv, newItem: Tv): Boolean = oldItem == newItem
}