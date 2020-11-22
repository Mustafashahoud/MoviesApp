package com.mustafa.movieguideapp.view.ui.movies.movielist

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentMoviesBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.MoviesAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieListFragment : Fragment(R.layout.fragment_movies), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MovieListViewModel> { viewModelFactory }

    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var binding by autoCleared<FragmentMoviesBinding>()

    private var adapter by autoCleared<MoviesAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMoviesBinding.bind(view)

        initializeUI()
        subscribers()
    }

    private fun initializeUI() {

        //  intiToolbar(getString(R.string.fragment_movies))

        adapter = MoviesAdapter(
            dataBindingComponent
        ) {
            MovieListFragmentDirections.actionMoviesFragmentToMovieDetail(it)
        }
        binding.recyclerViewListMovies.adapter = adapter
        binding.recyclerViewListMovies.layoutManager = GridLayoutManager(context, 3)
    }

    private fun subscribers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.moviesStream.collectLatest {
                adapter.submitData(it)
            }
        }
    }

//
//    /**
//     * Init the toolbar
//     * @param titleIn
//     */
/*    private fun intiToolbar(titleIn: String) {
        val title: TextView = toolbar_title
        title.text = titleIn

        search_icon.setOnClickListener {
            findNavController().navigate(
                MovieListFragmentDirections
                    .actionMoviesFragmentToMovieSearchFragment()
            )
        }
    }*/

}