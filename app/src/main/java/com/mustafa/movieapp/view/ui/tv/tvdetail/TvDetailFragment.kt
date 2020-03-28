package com.mustafa.movieapp.view.ui.tv.tvdetail

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
import com.mustafa.movieapp.R
import com.mustafa.movieapp.api.Api
import com.mustafa.movieapp.databinding.FragmentTvDetailBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.models.entity.Tv
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.ReviewListAdapter
import com.mustafa.movieapp.view.adapter.VideoListAdapter
import com.mustafa.movieapp.view.viewholder.VideoListViewHolder
import kotlinx.android.synthetic.main.layout_tv_detail_body.*
import javax.inject.Inject


@OpenForTesting
class TvDetailFragment : Fragment(), VideoListViewHolder.Delegate, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val vm by viewModels<TvDetailViewModel> {
        viewModelFactory
    }
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

        vm.postTvId(getTvFromIntent().id)
        with(binding) {
            lifecycleOwner = this@TvDetailFragment
            detailBody.viewModel = vm
            tv = getTvFromIntent()
            detailHeader.tv = getTvFromIntent()
            detailBody.tv = getTvFromIntent()
        }

        initializeUI()
    }


    private fun initializeUI() {
        detail_body_recyclerView_trailers.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        detail_body_recyclerView_trailers.adapter = VideoListAdapter(this)
        detail_body_recyclerView_reviews.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        detail_body_recyclerView_reviews.adapter = ReviewListAdapter()
        detail_body_recyclerView_reviews.isNestedScrollingEnabled = false
        detail_body_recyclerView_reviews.setHasFixedSize(true)
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
