package com.mustafa.movieapp.view.ui.movies.moviedetail

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
import com.mustafa.movieapp.R
import com.mustafa.movieapp.api.Api
import com.mustafa.movieapp.databinding.FragmentMovieDetailBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.ReviewListAdapter
import com.mustafa.movieapp.view.adapter.VideoListAdapter
import com.mustafa.movieapp.view.viewholder.VideoListViewHolder
import kotlinx.android.synthetic.main.layout_movie_detail_body.*
import javax.inject.Inject

class MovieDetailFragment : Fragment(), VideoListViewHolder.Delegate, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MovieDetailViewModel> { viewModelFactory }

    private var binding by autoCleared<FragmentMovieDetailBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        detail_body_recyclerView_trailers.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        detail_body_recyclerView_trailers.adapter = VideoListAdapter(this)
        detail_body_recyclerView_reviews.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        detail_body_recyclerView_reviews.adapter = ReviewListAdapter()
        detail_body_recyclerView_reviews.isNestedScrollingEnabled = false
        detail_body_recyclerView_reviews.setHasFixedSize(true)
    }

    private fun getMovieSafeArgs(): Movie {
        val params =
            MovieDetailFragmentArgs.fromBundle(
                requireArguments()
            )
        return params.movie
    }


    override fun onItemClicked(video: Video) {
        val playVideoIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(Api.getYoutubeVideoPath(video.key)))
        startActivity(playVideoIntent)
    }

}
