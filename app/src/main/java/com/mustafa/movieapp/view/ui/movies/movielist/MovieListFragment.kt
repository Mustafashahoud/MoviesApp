package com.mustafa.movieapp.view.ui.movies.movielist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentMoviesBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.MovieListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.RetryCallback
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.toolbar_search.*
import javax.inject.Inject

class MovieListFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<MovieListViewModel> { viewModelFactory }


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var binding by autoCleared<FragmentMoviesBinding>()

    private var adapter by autoCleared<MovieListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_movies,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribers()
        initializeUI()
    }

    private fun initializeUI() {

        intiToolbar(getString(R.string.fragment_movies))

        with(binding) {
            lifecycleOwner = this@MovieListFragment
            searchResult = viewModel.movieListLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }

        adapter = MovieListAdapter(appExecutors, dataBindingComponent) {
            findNavController().navigate(
                MovieListFragmentDirections.actionMoviesFragmentToMovieDetail(
                    it
                )
            )
        }

        recyclerView_list_movies.setHasFixedSize(true)
        recyclerView_list_movies.adapter = adapter
        recyclerView_list_movies.layoutManager = GridLayoutManager(context, 3)
        recyclerView_list_movies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1
                    && viewModel.movieListLiveData.value?.status != Status.LOADING
                ) {
                    viewModel.movieListLiveData.value?.let {
                        if (it.hasNextPage) viewModel.loadMore()
                    }
                }
            }
        })
    }

    private fun subscribers() {
        viewModel.movieListLiveData.observe(viewLifecycleOwner, Observer {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
    }


    /**
     * Init the toolbar
     * @param titleIn
     */
    fun intiToolbar(titleIn: String) {
        val title: TextView = toolbar_title
        title.text = titleIn

        search_icon.setOnClickListener {
            findNavController().navigate(
                MovieListFragmentDirections
                    .actionMoviesFragmentToMovieSearchFragment(false)
            )
        }
    }

}