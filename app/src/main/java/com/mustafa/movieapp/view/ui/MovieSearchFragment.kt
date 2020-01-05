package com.mustafa.movieapp.view.ui

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentMoviesSearchBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.MovieSearchListAdapter
import com.mustafa.movieapp.view.adapter.RecyclerViewPaginator
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListViewModel
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.fragment_movies.recyclerView_movies
import kotlinx.android.synthetic.main.fragment_movies.view.*
import kotlinx.android.synthetic.main.fragment_movies_search.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import javax.inject.Inject


class MovieSearchFragment : Fragment(), Injectable {

    private val TAG: String = MovieSearchFragment::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<MovieListViewModel> { viewModelFactory }
    var dataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentMoviesSearchBinding>()
    var adapter by autoCleared<MovieSearchListAdapter>()
    lateinit var paginator: RecyclerViewPaginator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_movies_search,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        initializeUI()
        subscribers()
        Log.d("ViewModelForSearch", viewModel.hashCode().toString())
    }

    private fun initializeUI() {
        //Init the toolbar_title
        initToolbar()

        // Adapter
        adapter = MovieSearchListAdapter(dataBindingComponent) {
            findNavController().navigate(MovieSearchFragmentDirections.actionMoviesToMovieDetail(it))
        }
        recyclerView_movies.setHasFixedSize(true)
        recyclerView_movies.addItemDecoration(
            DividerItemDecoration(
                recyclerView_movies.context,
                DividerItemDecoration.HORIZONTAL
            )
        )
        adapter.setHasStableIds(true) // prevent blinking .. in Case notifyDataSetChanged()
        // To have a nice animation and avoid blinking in the RecyclerView:
        /**
         * 1-  adapter.setHasStableIds(true)
         * 2-  Use notifyItemRangeInserted(start, count)
         */
        binding.root.recyclerView_movies.adapter = adapter

        recyclerView_movies.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        paginator = object : RecyclerViewPaginator(
            recyclerView = recyclerView_movies,
            hasNext = { viewModel.searchMovieListLiveData.value?.hasNextPage!! }
        ) {
            override fun onLoadMore(currentPage: Int) {
                loadMoreMovies(currentPage)
            }
        }
        paginator.resetCurrentPage()
    }

    private fun subscribers() {
        viewModel.searchMovieListLiveData.observe(viewLifecycleOwner, Observer {
            it?.let{
                if (it.data != null && it.data.isNotEmpty()) {
                    adapter.submitList(it.data)
                    progressBar.visibility = View.INVISIBLE
                } else if (Status.LOADING == it.status) {
                    progressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun loadMoreMovies(page: Int) {
        viewModel.setSearchMoviePage(page)
    }

    /**
     * Init the toolbar
     */
    private fun initToolbar() {

        search_view.onActionViewExpanded()

        val searchViewEditText =
            search_view.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        search_view.setOnSearchClickListener {
            searchViewEditText.isEnabled = true
        }
        search_view.setOnCloseListener {
            searchViewEditText.isEnabled = false
            false
        }

        arrow_back.setOnClickListener {
            findNavController().navigate(MovieSearchFragmentDirections.actionMoviesSearchToMoviesDiscovery())
        }

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                dismissKeyboard(search_view.windowToken)
                viewModel.setSearchMovieQueryAndPage(query, 1)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        Log.d(TAG, "onAttachFragment")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        dismissKeyboard(search_view.windowToken)
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
//        if (!search_view.isIconified || (search_view.query.toString()).isNotEmpty()) {
//            toolbar_title.gone()
//        }
    }
    /**
     * dismiss Keyboard
     *
     * @param windowToken The token of the window that is making the request, as returned by View.getWindowToken().
     */
    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }
}