package com.mustafa.movieguideapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ItemTvSearchBinding
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.utils.TvDiffUtilCallBack

class FilteredTvsAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val tvOnClickCallback: ((Tv) -> Unit)?
) : PagingDataAdapter<Tv, FilteredTvsAdapter.ViewHolder>(TvDiffUtilCallBack()) {
    class ViewHolder(private val binding: ItemTvSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tv: Tv) {
            binding.tv = tv
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTvSearchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_tv_search,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.setOnClickListener {
            binding.tv?.let {
                tvOnClickCallback?.invoke(it)
            }
        }

        return ViewHolder(binding)
    }
}