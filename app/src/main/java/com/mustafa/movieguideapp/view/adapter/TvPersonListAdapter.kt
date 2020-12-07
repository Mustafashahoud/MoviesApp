package com.mustafa.movieguideapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ItemTvForCelebrityBinding
import com.mustafa.movieguideapp.models.TvPerson
import com.mustafa.movieguideapp.view.ui.common.DataBoundListAdapter

class TvPersonListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val movieOnClickCallback: ((TvPerson) -> Unit)?
) : DataBoundListAdapter<TvPerson, ItemTvForCelebrityBinding>(
    diffCallback = object : DiffUtil.ItemCallback<TvPerson>() {
        override fun areItemsTheSame(oldItem: TvPerson, newItem: TvPerson): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TvPerson, newItem: TvPerson): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ItemTvForCelebrityBinding {
        val binding = DataBindingUtil.inflate<ItemTvForCelebrityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_tv_for_celebrity,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.tv?.let {
                movieOnClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: ItemTvForCelebrityBinding, item: TvPerson) {
        binding.tv = item
    }
}