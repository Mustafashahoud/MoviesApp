package com.mustafa.movieguideapp.view.adapter

import android.view.View

import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.view.viewholder.BaseAdapter
import com.mustafa.movieguideapp.view.viewholder.BaseViewHolder
import com.mustafa.movieguideapp.view.viewholder.ReviewListViewHolder
import com.mustafa.movieguideapp.view.viewholder.SectionRow

/**
 * https://github.com/skydoves/TheMovies
 */
class ReviewListAdapter : BaseAdapter() {

    init {
        addSection(ArrayList<Review>())
    }

    fun addReviewList(resource: Resource<List<Review>>) {
        resource.data?.let {
            sections()[0].addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun layout(sectionRow: SectionRow): Int {
        return R.layout.item_review
    }

    override fun viewHolder(layout: Int, view: View): BaseViewHolder {
        return ReviewListViewHolder(view)
    }
}
