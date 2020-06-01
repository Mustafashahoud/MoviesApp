package com.mustafa.movieguideapp.view.adapter

import android.view.View
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.view.viewholder.BaseAdapter
import com.mustafa.movieguideapp.view.viewholder.BaseViewHolder
import com.mustafa.movieguideapp.view.viewholder.SectionRow
import com.mustafa.movieguideapp.view.viewholder.VideoListViewHolder

/**
 * Copied from https://github.com/skydoves/TheMovies
 */
class VideoListAdapter(private val delegate: VideoListViewHolder.Delegate) : BaseAdapter() {

    init {
        addSection(ArrayList<Video>())
    }

    fun addVideoList(resource: Resource<List<Video>>) {
        resource.data?.let {
            sections()[0].addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun layout(sectionRow: SectionRow): Int {
        return R.layout.item_video
    }

    override fun viewHolder(layout: Int, view: View): BaseViewHolder {
        return VideoListViewHolder(view, delegate)
    }
}
