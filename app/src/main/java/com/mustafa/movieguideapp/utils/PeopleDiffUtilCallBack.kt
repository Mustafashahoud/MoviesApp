package com.mustafa.movieguideapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.mustafa.movieguideapp.models.Person

class PeopleDiffUtilCallBack : DiffUtil.ItemCallback<Person>() {
    override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean = oldItem == newItem
}