package com.mustafa.movieguideapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ItemReviewBinding
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.view.ui.common.RecyclerViewBase

class ReviewListAdapter(
    private val dataBindingComponent: DataBindingComponent
) : RecyclerViewBase<Review, ItemReviewBinding>() {

    override fun createBinding(parent: ViewGroup): ItemReviewBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_review,
            parent,
            false,
            dataBindingComponent
        )

    }

    override fun bind(binding: ItemReviewBinding, item: Review) {
        binding.review = item
    }
}