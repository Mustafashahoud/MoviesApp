package com.mustafa.movieguideapp.view.ui.person.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.api.Api
import com.mustafa.movieguideapp.databinding.FragmentTvCelebrityDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.models.entity.TvPerson
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.viewholder.VideoListViewHolder

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