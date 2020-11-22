package com.mustafa.movieguideapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ItemPersonBinding
import com.mustafa.movieguideapp.models.Person
import com.mustafa.movieguideapp.utils.PeopleDiffUtilCallBack

class PeopleAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val movieOnClickCallback: ((Person) -> Unit)?
) : PagingDataAdapter<Person, PeopleAdapter.ViewHolder>(PeopleDiffUtilCallBack()) {
    class ViewHolder(private val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person) {
            binding.person = person
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPersonBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_person,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.setOnClickListener {
            binding.person?.let {
                movieOnClickCallback?.invoke(it)
            }
        }

        return ViewHolder(binding)
    }
}