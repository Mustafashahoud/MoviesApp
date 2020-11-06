package com.mustafa.movieguideapp.view.ui.tv.tvdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.api.Api
import com.mustafa.movieguideapp.databinding.FragmentTvDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.ReviewListAdapter
import com.mustafa.movieguideapp.view.adapter.VideoListAdapter
import com.mustafa.movieguideapp.view.viewholder.VideoListViewHolder
import kotlinx.android.synthetic.main.layout_tv_detail_body.*
import javax.inject.Inject


class TvDetailFragment : Fragment(), VideoListViewHolder.Delegate, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TvDetailViewModel> { viewModelFactory }
    private var binding by autoCleared<FragmentTvDetailBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tv_detail,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.setTvId(getTvFromIntent().id)
        with(binding) {
            lifecycleOwner = this@TvDetailFragment
            detailBody.viewModel = viewModel
            tv = getTvFromIntent()
            detailHeader.tv = getTvFromIntent()
            detailBody.tv = getTvFromIntent()
        }

        initializeUI()
    }


    private fun initializeUI() {
        binding.detailBody.detailBodyRecyclerViewTrailers
        binding.detailBody.detailBodyRecyclerViewTrailers.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.detailBody.detailBodyRecyclerViewTrailers.adapter = VideoListAdapter(this)
        binding.detailBody.detailBodyRecyclerViewReviews.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.detailBody.detailBodyRecyclerViewReviews.adapter = ReviewListAdapter()
        binding.detailBody.detailBodyRecyclerViewReviews.isNestedScrollingEnabled = false
        binding.detailBody.detailBodyRecyclerViewReviews.setHasFixedSize(true)
    }

    private fun getTvFromIntent(): Tv {
        return TvDetailFragmentArgs.fromBundle(
            requireArguments()
        ).tv
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            activity?.onBackPressed()
        return false
    }

    override fun onItemClicked(video: Video) {
        val playVideoIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(Api.getYoutubeVideoPath(video.key)))
        startActivity(playVideoIntent)
    }

}
