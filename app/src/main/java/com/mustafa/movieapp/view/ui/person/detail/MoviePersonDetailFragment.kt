package com.mustafa.movieapp.view.ui.person.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mustafa.movieapp.R
import com.mustafa.movieapp.databinding.FragmentMovieCelebrityDetailBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.entity.MoviePerson
import com.mustafa.movieapp.utils.autoCleared


class MoviePersonDetailFragment : Fragment(), Injectable {

    private var binding by autoCleared<FragmentMovieCelebrityDetailBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_movie_celebrity_detail,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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