package com.mustafa.movieapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.mustafa.movieapp.R
import com.mustafa.movieapp.databinding.ItemPersonBinding
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.view.ui.common.RecyclerViewBase

class PeopleAdapter(
        private val dataBindingComponent: DataBindingComponent,
        private val personOnClickCallback: ((Person) -> Unit)?
) : RecyclerViewBase<Person, ItemPersonBinding>() {

    override fun createBinding(parent: ViewGroup): ItemPersonBinding {
        val binding = DataBindingUtil.inflate<ItemPersonBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_person,
                parent,
                false,
                dataBindingComponent
        )

        binding.root.setOnClickListener {
            binding.person?.let {
                personOnClickCallback?.invoke(it)
            }
        }

        return binding
    }


    override fun bind(binding: ItemPersonBinding, item: Person) {
        binding.person = item
    }
}