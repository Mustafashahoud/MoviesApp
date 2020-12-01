package com.mustafa.movieguideapp.view.ui.person.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.FragmentTvCelebrityDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.TvPerson
import com.mustafa.movieguideapp.utils.autoCleared

class TvPersonDetailFragment : Fragment(R.layout.fragment_tv_celebrity_detail), Injectable {

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

}