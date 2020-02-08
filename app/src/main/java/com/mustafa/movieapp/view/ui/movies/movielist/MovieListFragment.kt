package com.mustafa.movieapp.view.ui.movies.movielist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentMoviesBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.extension.visible
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.MovieListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.toolbar_search.*
import javax.inject.Inject

@Suppress("SpellCheckingInspection")
@OpenForTesting
class MovieListFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<MovieListViewModel> { viewModelFactory }

    @Suppress("LeakingThis")
    var dataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentMoviesBinding>()

    var adapter by autoCleared<MovieListAdapter>()

//    var paginator: RecyclerViewPaginator? = null

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
        //Init the toolbar
        intiToolbar(getString(R.string.fragment_movies))

        // Adapter
        val rvAdapter = MovieListAdapter(appExecutors, dataBindingComponent) {
            navController().navigate(
                MovieListFragmentDirections.actionMoviesFragmentToMovieDetail(
                    it
                )
            )
        }
//        recyclerView_movies.setHasFixedSize(true)
//        adapter.setHasStableIds(true) // prevent blinking .. in Case notifyDataSetChanged()
        // To have a nice animation and avoid blinking in the RecyclerView:
        /**
         * 1-  adapter.setHasStableIds(true)
         * 2-  Use notifyItemRangeInserted(start, count)
         */
        recyclerView_movies.setHasFixedSize(true)
        recyclerView_movies.setItemViewCacheSize(40)
        recyclerView_movies.adapter = rvAdapter
        adapter = rvAdapter


        recyclerView_movies.layoutManager = GridLayoutManager(context, 3)

        recyclerView_movies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1
                    && viewModel.movieListLiveData.value?.status != Status.LOADING
                    && dy > 0
                ) {
                    viewModel.loadMore()
                } else {
                    return
                }
            }
        })

//        paginator = RecyclerViewPaginator(
//            viewModel = viewModel,
//            recyclerView = recyclerView_movies,
//            hasNext = { viewModel.movieListLiveData.value?.hasNextPage!! }
//        )
    }

    private fun subscribers() {
        viewModel.getMovies().observe(viewLifecycleOwner, Observer {
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

//        filter.setOnClickListener {
//            val intent = Intent(context, SearchActivity::class.java)
//            startActivity(intent)
//        }

        search_icon.setOnClickListener {
            navController().navigate(MovieListFragmentDirections.actionMoviesFragmentToMovieSearchFragment())
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visible()
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

//    /**
//     * dismiss Keyboard
//     *
//     * @param windowToken The token of the window that is making the request, as returned by View.getWindowToken().
//     */
//    private fun dismissKeyboard(windowToken: IBinder) {
//        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//        imm?.hideSoftInputFromWindow(windowToken, 0)
//    }

}