package com.mustafa.movieguideapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ItemTvBinding
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.utils.TvDiffUtilCallBack

class TvsAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val tvOnClickCallback: ((Tv) -> Unit)?
) : PagingDataAdapter<Tv, TvsAdapter.ViewHolder>(TvDiffUtilCallBack()) {

    companion object {
        private const val NETWORK_VIEW_TYPE = 1
        private const val WALLPAPER_VIEW_TYPE = 2
    }

    class ViewHolder(private val binding: ItemTvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tv: Tv) {
            binding.tv = tv
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount) {
            NETWORK_VIEW_TYPE
        } else {
            WALLPAPER_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTvBinding = DataBindingUtil.inflate(
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

        return ViewHolder(binding)
    }
}