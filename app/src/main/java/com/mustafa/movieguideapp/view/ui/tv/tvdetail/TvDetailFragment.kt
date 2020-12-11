package com.mustafa.movieguideapp.view.ui.tv.tvdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.api.Api
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentTvDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.ReviewListAdapter
import com.mustafa.movieguideapp.view.adapter.VideoListAdapter
import javax.inject.Inject


class TvDetailFragment : Fragment(R.layout.fragment_tv_detail), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private val mViewModel by viewModels<TvDetailViewModel> { viewModelFactory }
    private var binding by autoCleared<FragmentTvDetailBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = DataBindingUtil.bind(view, dataBindingComponent)!!

        val receivedTv = getTvFromIntent()

        initializeUI()

        with(binding) {
            lifecycleOwner = this@TvDetailFragment.viewLifecycleOwner
            viewModel = mViewModel
            tv = receivedTv
        }

        mViewModel.setTvId(receivedTv.id)


    }


    private fun initializeUI() {
        binding.detailBody.apply {
            detailBodyRecyclerViewTrailers.apply {
                adapter = VideoListAdapter(dataBindingComponent) { video ->
                    val playVideoIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(Api.getYoutubeVideoPath(video.key))
                        )
                    startActivity(playVideoIntent)
                }
            }

            detailBodyRecyclerViewReviews.adapter = ReviewListAdapter(dataBindingComponent)
        }
    }

    private fun getTvFromIntent(): Tv {
        return TvDetailFragmentArgs.fromBundle(
            requireArguments()
        ).tv
    }
}
