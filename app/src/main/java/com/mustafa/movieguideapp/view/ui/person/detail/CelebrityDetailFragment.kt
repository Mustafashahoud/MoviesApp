package com.mustafa.movieguideapp.view.ui.person.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentCelebrityDetailBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.Person
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.MoviePersonListAdapter
import com.mustafa.movieguideapp.view.adapter.TvPersonListAdapter
import kotlinx.android.synthetic.main.toolbar_detail.*
import javax.inject.Inject

class CelebrityDetailFragment : Fragment(R.layout.fragment_celebrity_detail), Injectable {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val dataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel by viewModels<PersonDetailViewModel> { viewModelFactory }

    private var binding by autoCleared<FragmentCelebrityDetailBinding>()

    private var adapterMoviesForCelebrity by autoCleared<MoviePersonListAdapter>()

    private var adapterTvsForCelebrity by autoCleared<TvPersonListAdapter>()

    private var personId = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCelebrityDetailBinding.bind(view)

        val selectedPersonId = getSelectedPerson().id
        //Just to Not reload the movies of person when there no need
        if (personId != selectedPersonId) {
            personId = selectedPersonId
            viewModel.postPersonId(getSelectedPerson().id)
        }
        with(binding) {
            lifecycleOwner = this@CelebrityDetailFragment
            viewmodel = viewModel
            person = getSelectedPerson()
        }
        observeMoviesAndTvsForCelebrity()
        initializeUI()
    }


    private fun initializeUI() {
        toolbar_back_arrow.setOnClickListener { activity?.onBackPressed() }
        toolbar_title.text = getSelectedPerson().name
        viewModel.setPersonId(getSelectedPerson().id)
        adapterMoviesForCelebrity = MoviePersonListAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                CelebrityDetailFragmentDirections.actionCelebrityDetailToCelebrityMovieDetail(
                    it
                )
            )

        }
        binding.recyclerViewCelebrityMovies.adapter = adapterMoviesForCelebrity
        binding.recyclerViewCelebrityMovies.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        adapterTvsForCelebrity = TvPersonListAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                CelebrityDetailFragmentDirections.actionCelebrityDetailToCelebrityTvDetail(
                    it
                )
            )
        }
        binding.recyclerViewCelebrityTvs.adapter = adapterTvsForCelebrity
        binding.recyclerViewCelebrityTvs.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }


    private fun getSelectedPerson(): Person {
        val params =
            CelebrityDetailFragmentArgs.fromBundle(
                requireArguments()
            )
        return params.person
    }

    private fun observeMoviesAndTvsForCelebrity() {

        viewModel.moviesOfCelebrity.observe(viewLifecycleOwner) {
            if (!it.data.isNullOrEmpty()) {
                val moviesPerson = it.data.filter { moviePerson -> moviePerson.poster_path != null }
                adapterMoviesForCelebrity.submitList(moviesPerson)
            }
        }

        viewModel.tvsOfCelebrity.observe(viewLifecycleOwner) {
            if (!it.data.isNullOrEmpty()) {
                val tvsPerson = it.data.filter { tvPerson -> tvPerson.poster_path != null }
                adapterTvsForCelebrity.submitList(tvsPerson)
            }
        }
    }
}
