package com.mustafa.movieguideapp.view.ui.tv.tvdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.api.Api
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentTvDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.ReviewListAdapter
import com.mustafa.movieguideapp.view.adapter.VideoListAdapter
import javax.inject.Inject


class TvDetailFragment : Fragment(R.layout.fragment_tv_detail),
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private val mViewModel by viewModels<TvDetailViewModel> { viewModelFactory }
    private var binding by autoCleared<FragmentTvDetailBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = DataBindingUtil.bind(view, dataBindingComponent)!!

        with(binding) {
            lifecycleOwner = this@TvDetailFragment.viewLifecycleOwner
            viewModel = mViewModel
            tv = getTvFromIntent()
        }

        mViewModel.setTvId(getTvFromIntent().id)

        initializeUI()
    }


    private fun initializeUI() {
        binding.apply {
            detailBody.detailBodyRecyclerViewTrailers
            detailBody.detailBodyRecyclerViewTrailers.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            detailBody.detailBodyRecyclerViewTrailers.adapter =
                VideoListAdapter(dataBindingComponent) { video ->
                    val playVideoIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(Api.getYoutubeVideoPath(video.key)))
                    startActivity(playVideoIntent)
                }
            detailBody.detailBodyRecyclerViewReviews.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            detailBody.detailBodyRecyclerViewReviews.adapter = ReviewListAdapter(dataBindingComponent)
            detailBody.detailBodyRecyclerViewReviews.isNestedScrollingEnabled = false
            detailBody.detailBodyRecyclerViewReviews.setHasFixedSize(true)
        }

    }

    private fun getTvFromIntent(): Tv {
        return TvDetailFragmentArgs.fromBundle(requireArguments()).tv
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            activity?.onBackPressed()
        return false
    }

}
