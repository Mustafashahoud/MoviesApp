package com.mustafa.movieguideapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ItemPersonBinding
import com.mustafa.movieguideapp.models.Person
import com.mustafa.movieguideapp.utils.PeopleDiffUtilCallBack

class PeopleAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val tvOnClickCallback: ((Person) -> Unit)?
) : PagingDataAdapter<Person, PeopleAdapter.PeopleViewHolder>(PeopleDiffUtilCallBack()) {

    companion object {
        private const val NETWORK_VIEW_TYPE = 1
        private const val WALLPAPER_VIEW_TYPE = 2
    }

    class PeopleViewHolder(private val binding: ItemPersonBinding) : ViewHolder(binding.root) {
        fun bind(person: Person) {
            binding.person = person
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount) {
            NETWORK_VIEW_TYPE
        } else {
            WALLPAPER_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val binding: ItemPersonBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_person,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.setOnClickListener {
            binding.person?.let {
                tvOnClickCallback?.invoke(it)
            }
        }

        return PeopleViewHolder(binding)
    }
}