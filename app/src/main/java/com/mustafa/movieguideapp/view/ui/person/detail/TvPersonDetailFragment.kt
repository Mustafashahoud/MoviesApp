package com.mustafa.movieguideapp.view.ui.person.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.api.Api
import com.mustafa.movieguideapp.databinding.FragmentTvCelebrityDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.TvPerson
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.viewholder.VideoListViewHolder

class TvPersonDetailFragment : Fragment(R.layout.fragment_tv_celebrity_detail), VideoListViewHolder.Delegate, Injectable {

    private var binding by autoCleared<FragmentTvCelebrityDetailBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentTvCelebrityDetailBinding.bind(view)

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