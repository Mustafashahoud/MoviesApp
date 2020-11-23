package com.mustafa.movieguideapp.view.ui.search.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentMovieSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.hideKeyboard
import com.mustafa.movieguideapp.models.Status
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.MovieSearchListAdapter
import com.mustafa.movieguideapp.view.ui.common.InfinitePager
import com.mustafa.movieguideapp.view.ui.common.RetryCallback
import com.mustafa.movieguideapp.view.ui.search.MovieSearchViewModel
import kotlinx.android.synthetic.main.toolbar_search_result.*
import javax.inject.Inject

class MovieSearchResultFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MovieSearchViewModel> { viewModelFactory }
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentMovieSearchResultBinding>()
    var adapter by autoCleared<MovieSearchListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_movie_search_result,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeUI()
        subscribers()

        viewModel.setSearchMovieQueryAndPage(getQuerySafeArgs(), 1)

        with(binding) {
            lifecycleOwner = this@MovieSearchResultFragment
            searchResult = viewModel.searchMovieListLiveData
            query = viewModel.queryMovieLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }
    }

    private fun subscribers() {
        viewModel.searchMovieListLiveData.observe(viewLifecycleOwner, {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
    }


    private fun getQuerySafeArgs(): String {
        val params =
            MovieSearchResultFragmentArgs.fromBundle(
                requireArguments()
            )
        return params.query
    }

    private fun initializeUI() {
        adapter = MovieSearchListAdapter(dataBindingComponent) {
            findNavController().navigate(
                MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieDetail(it)
            )
        }

        hideKeyboard()
        binding.recyclerViewSearchResultMovies.adapter = adapter
        binding.recyclerViewSearchResultMovies.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewSearchResultMovies.addOnScrollListener(object :
            InfinitePager(adapter) {
            override fun loadMoreCondition(): Boolean {
                viewModel.searchMovieListLiveData.value?.let { resource ->
                    return resource.hasNextPage && resource.status != Status.LOADING
                }
                return false
            }

            override fun loadMore() {
                viewModel.loadMore()
            }
        })

        search_view.setOnSearchClickListener {
            findNavController().navigate(MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieSearchFragment())
        }

        arrow_back.setOnClickListener {
            findNavController().navigate(MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieSearchFragment())
        }
    }

}