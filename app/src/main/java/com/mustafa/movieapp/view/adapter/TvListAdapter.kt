package com.mustafa.movieapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.skydoves.baserecyclerviewadapter.BaseAdapter
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import com.skydoves.baserecyclerviewadapter.SectionRow
import com.mustafa.movieapp.R
import com.mustafa.movieapp.databinding.ItemMovieBinding
import com.mustafa.movieapp.databinding.ItemTvBinding
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.Tv
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.DataBoundListAdapter
import com.mustafa.movieapp.view.ui.common.RecyclerViewBase

class TvListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        private val tvOnClickCallback: ((Tv) -> Unit)?
) : RecyclerViewBase<Tv, ItemTvBinding>() {
    override fun createBinding(parent: ViewGroup): ItemTvBinding {
        val binding = DataBindingUtil.inflate<ItemTvBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_tv,
                parent,
                false,
                dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.tv?.let {
                tvOnClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: ItemTvBinding, item: Tv) {
        binding.tv = item
    }

}

