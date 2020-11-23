package com.mustafa.movieguideapp.view.ui.movies.moviedetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
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
import com.mustafa.movieguideapp.databinding.FragmentMovieDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.ReviewListAdapter
import com.mustafa.movieguideapp.view.adapter.VideoListAdapter
import com.mustafa.movieguideapp.view.viewholder.VideoListViewHolder
import javax.inject.Inject

class MovieDetailFragment : Fragment(), VideoListViewHolder.Delegate, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MovieDetailViewModel> { viewModelFactory }

    private var binding by autoCleared<FragmentMovieDetailBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_movie_detail,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.setMovieId(getMovieSafeArgs().id)

        with(binding) {
            lifecycleOwner = this@MovieDetailFragment
            detailBody.viewModel = viewModel
            movie = getMovieSafeArgs()
            detailHeader.movie = getMovieSafeArgs()
            detailBody.movie = getMovieSafeArgs()
        }

        initializeUI()

    }


    private fun initializeUI() {
        binding.detailBody.detailBodyRecyclerViewTrailers.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.detailBody.detailBodyRecyclerViewTrailers.adapter = VideoListAdapter(this)
        binding.detailBody.detailBodyRecyclerViewReviews.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.detailBody.detailBodyRecyclerViewReviews.adapter = ReviewListAdapter()
        binding.detailBody.detailBodyRecyclerViewReviews.isNestedScrollingEnabled = false
        binding.detailBody.detailBodyRecyclerViewReviews.setHasFixedSize(true)
    }

    private fun getMovieSafeArgs(): Movie {
        val params = MovieDetailFragmentArgs.fromBundle(requireArguments())
        return params.movie
    }


    override fun onItemClicked(video: Video) {
        val playVideoIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(Api.getYoutubeVideoPath(video.key)))
        startActivity(playVideoIntent)
    }

}
