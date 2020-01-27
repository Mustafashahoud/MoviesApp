package com.mustafa.movieapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.mustafa.movieapp.R
import com.mustafa.movieapp.databinding.FilterButtonItemBinding
import com.mustafa.movieapp.databinding.ItemMovieBinding
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.DataBoundListAdapter
import com.mustafa.movieapp.view.ui.common.RecyclerViewBase

class FilterButtonsAdapters(
    appExecutors: AppExecutors,
    private val dataBindingComponent: DataBindingComponent,
    private val buttonOnClickCallback: ((View?) -> Unit)?
) : DataBoundListAdapter<String, FilterButtonItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup): FilterButtonItemBinding {

        val binding = DataBindingUtil.inflate<FilterButtonItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.filter_button_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            buttonOnClickCallback?.invoke(it)
        }

        return binding
    }

    override fun bind(binding: FilterButtonItemBinding, item: String) {
        binding.buttonTitle = item
    }
}