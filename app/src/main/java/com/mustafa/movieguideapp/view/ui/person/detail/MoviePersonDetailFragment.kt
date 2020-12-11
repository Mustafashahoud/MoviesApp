package com.mustafa.movieguideapp.view.ui.person.detail

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentMovieCelebrityDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.entity.MoviePerson
import com.mustafa.movieguideapp.utils.autoCleared


class MoviePersonDetailFragment : Fragment(R.layout.fragment_movie_celebrity_detail), Injectable {

    private var binding by autoCleared<FragmentMovieCelebrityDetailBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = DataBindingUtil.bind(view, dataBindingComponent)!!

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