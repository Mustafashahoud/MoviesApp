package com.mustafa.movieguideapp.view.ui.search.movies

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentMovieSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.hideKeyboard
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.MoviesSearchAdapter
import kotlinx.android.synthetic.main.toolbar_search_result.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieSearchResultFragment : Fragment(R.layout.fragment_movie_search_result), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    private val viewModel by viewModels<MovieSearchViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentMovieSearchResultBinding>()
    private var adapter by autoCleared<MoviesSearchAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentMovieSearchResultBinding.bind(view)

        initializeUI()

        getQuerySafeArgs()?.let { query ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchMovies(query).collectLatest {
                    adapter.submitData(it)
                }
            }
        }

//        with(binding) {
//            lifecycleOwner = this@MovieSearchResultFragment
//            searchResult = viewModel.searchMovieListLiveData
//            query = viewModel.queryMovieLiveData
//            callback = object : RetryCallback {
//                override fun retry() {
//                    viewModel.refresh()
//                }
//            }
//        }
    }


    private fun getQuerySafeArgs(): String? {
        val params =
            MovieSearchResultFragmentArgs.fromBundle(
                requireArguments()
            )
        return params.query
    }

    private fun initializeUI() {
        adapter = MoviesSearchAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieDetail(
                    it
                )
            )
        }

        hideKeyboard()
        binding.recyclerViewSearchResultMovies.adapter = adapter
        binding.recyclerViewSearchResultMovies.layoutManager = LinearLayoutManager(context)

        search_view.setOnSearchClickListener {
            findNavController().navigate(
                MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieSearchFragment()
            )
        }

        arrow_back.setOnClickListener {
            findNavController().navigate(
                MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieSearchFragment()
            )
        }
    }

}