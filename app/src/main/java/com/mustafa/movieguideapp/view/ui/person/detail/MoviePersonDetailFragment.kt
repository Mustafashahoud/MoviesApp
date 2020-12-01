package com.mustafa.movieguideapp.view.ui.person.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.FragmentMovieCelebrityDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.MoviePerson
import com.mustafa.movieguideapp.utils.autoCleared


class MoviePersonDetailFragment : Fragment(R.layout.fragment_movie_celebrity_detail), Injectable {

    private var binding by autoCleared<FragmentMovieCelebrityDetailBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentMovieCelebrityDetailBinding.bind(view)

        with(binding) {
            movie = getMovieFromIntent()
            detailHeader.movie = getMovieFromIntent()
        }
    }

    private fun getMovieFromIntent(): MoviePerson {
        return MoviePersonDetailFragmentArgs.fromBundle(
            requireArguments()
        ).movie
    }
}