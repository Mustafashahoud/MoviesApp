package com.mustafa.movieapp.view.ui.person.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mustafa.movieapp.R
import com.mustafa.movieapp.api.Api
import com.mustafa.movieapp.databinding.FragmentTvCelebrityDetailBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.models.entity.TvPerson
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.viewholder.VideoListViewHolder

class TvPersonDetailFragment : Fragment(), VideoListViewHolder.Delegate, Injectable {

    private var binding by autoCleared<FragmentTvCelebrityDetailBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tv_celebrity_detail,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding) {
            tv = getTvFromIntent()
            detailHeader.tv = getTvFromIntent()
        }
    }

    private fun getTvFromIntent(): TvPerson {
        return TvPersonDetailFragmentArgs.fromBundle(
            requireArguments()
        ).tv
    }

    override fun onItemClicked(video: Video) {
        val playVideoIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(Api.getYoutubeVideoPath(video.key)))
        startActivity(playVideoIntent)
    }
}