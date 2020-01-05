package com.mustafa.movieapp.view.ui.movies.movielist

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentMoviesBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.MovieListAdapter
import com.mustafa.movieapp.view.adapter.RecyclerViewPaginator
import com.mustafa.movieapp.view.ui.common.AppExecutors
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.toolbar_search.*
import timber.log.Timber
import javax.inject.Inject

@Suppress("SpellCheckingInspection")
@OpenForTesting
class MovieListFragment : Fragment(), Injectable {

    private val TAG: String = MovieListFragment::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<MovieListViewModel> { viewModelFactory }

    var dataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentMoviesBinding>()

    var adapter by autoCleared<MovieListAdapter>()

    var paginator: RecyclerViewPaginator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("GraMovieListFragment", "onCreateView")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_movies,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("GraMovieListFragment", "onViewCreated")
        subscribers()
        initializeUI()
    }

    private fun initializeUI() {
        //Init the toolbar
        intiToolbar(getString(R.string.fragment_movies))

        Log.d("viewModel.value", "" + viewModel.movieListLiveData.value)

        // Adapter
        val rvAdapter = MovieListAdapter(dataBindingComponent) {
            findNavController().navigate(
                MovieListFragmentDirections.actionMoviesToMovieDetail(it)
            )
        }
//        recyclerView_movies.setHasFixedSize(true)
//        adapter.setHasStableIds(true) // prevent blinking .. in Case notifyDataSetChanged()
        // To have a nice animation and avoid blinking in the RecyclerView:
        /**
         * 1-  adapter.setHasStableIds(true)
         * 2-  Use notifyItemRangeInserted(start, count)
         */
        recyclerView_movies.adapter = rvAdapter
        adapter = rvAdapter

        Timber.d("adapter${adapter.hashCode()}")
        Timber.d("adapter${rvAdapter.hashCode()}")

        recyclerView_movies.layoutManager = GridLayoutManager(context, 2)

        paginator = object : RecyclerViewPaginator(
            recyclerView = recyclerView_movies,
            hasNext = { viewModel.movieListLiveData.value?.hasNextPage!! }
        ) {
            override fun onLoadMore(currentPage: Int) {
                loadMoreMovies(currentPage)
            }
        }
    }

    private fun subscribers() {
//        viewModel.movieListLiveData.removeObservers(this)
        viewModel.movieListLiveData.observe(viewLifecycleOwner, Observer {
//            Log.d("TEST", "[onChanged]: " + hashCode() + " " + it.status)
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
    }

    private fun loadMoreMovies(page: Int) {
        viewModel.setMoviePage(page)
    }


    /**
     * Init the toolbar
     * @param titleIn
     */
    fun intiToolbar(titleIn: String) {
        val title: TextView = toolbar_title
        title.text = titleIn

        search_icon.setOnClickListener {
            findNavController().navigate(MovieListFragmentDirections.actionMoviesToFragmentMoviesSearch())
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    /**
     * dismiss Keyboard
     *
     * @param windowToken The token of the window that is making the request, as returned by View.getWindowToken().
     */
    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("GraMovieListFragment", "onActivityCreated")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("GraMovieListFragment", "onAttach")
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        Log.d("GraMovieListFragment", "onAttachFragment")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GraMovieListFragment", "onCreate")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("GraMovieListFragment", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("GraMovieListFragment", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("GraMovieListFragment", "onDetach")
    }

    override fun onPause() {
        super.onPause()
        Log.d("GraMovieListFragment", "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("GraMovieListFragment", "onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.d("GraMovieListFragment", "onResume")
    }

}