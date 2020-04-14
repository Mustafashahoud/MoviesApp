package com.mustafa.movieapp.view.ui.person.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentCelebrityDetailBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.MoviePersonListAdapter
import com.mustafa.movieapp.view.adapter.TvPersonListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import kotlinx.android.synthetic.main.fragment_celebrity_detail.*
import kotlinx.android.synthetic.main.toolbar_detail.*
import javax.inject.Inject

class CelebrityDetailFragment : Fragment(), Injectable {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel by viewModels<PersonDetailViewModel> { viewModelFactory }

    private var binding by autoCleared<FragmentCelebrityDetailBinding>()

    private var adapterMoviesForCelebrity by autoCleared<MoviePersonListAdapter>()

    private var adapterTvsForCelebrity by autoCleared<TvPersonListAdapter>()

    private var personId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_celebrity_detail,
            container,
            false
        )

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            appExecutors,
            dataBindingComponent
        ) {
            findNavController().navigate(
                CelebrityDetailFragmentDirections.actionCelebrityDetailToCelebrityMovieDetail(
                    it
                )
            )

        }
        recycler_view_celebrity_movies.adapter = adapterMoviesForCelebrity
        recycler_view_celebrity_movies.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        adapterTvsForCelebrity = TvPersonListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            findNavController().navigate(
                CelebrityDetailFragmentDirections.actionCelebrityDetailToCelebrityTvDetail(
                    it
                )
            )
        }
        recycler_view_celebrity_tvs.adapter = adapterTvsForCelebrity
        recycler_view_celebrity_tvs.layoutManager = LinearLayoutManager(
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

        viewModel.moviesOfCelebrity.observe(viewLifecycleOwner, Observer {
            if (!it.data.isNullOrEmpty()) {
                val moviesPerson = it.data.filter { moviePerson -> moviePerson.poster_path != null }
                adapterMoviesForCelebrity.submitList(moviesPerson)
            }
        })

        viewModel.tvsOfCelebrity.observe(viewLifecycleOwner, Observer {
            if (!it.data.isNullOrEmpty()) {
                val tvsPerson = it.data.filter { tvPerson -> tvPerson.poster_path != null }
                adapterTvsForCelebrity.submitList(tvsPerson)
            }
        })
    }
}
