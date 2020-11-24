package com.mustafa.movieguideapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ItemPersonSearchBinding
import com.mustafa.movieguideapp.models.Person
import com.mustafa.movieguideapp.utils.PeopleDiffUtilCallBack

class PeopleSearchAdapter(
    val dataBindingComponent: DataBindingComponent,
    private val personOnClickCallback: ((Person) -> Unit)?
) : PagingDataAdapter<Person, PeopleSearchAdapter.ViewHolder>(PeopleDiffUtilCallBack()) {
    class ViewHolder(private val binding: ItemPersonSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person) {
            binding.person = person
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPersonSearchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_person_search,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.setOnClickListener {
            binding.person?.let {
                personOnClickCallback?.invoke(it)
            }
        }
        return ViewHolder(
            binding
        )
    }
}