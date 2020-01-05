package com.mustafa.movieapp.view.ui.person

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
import androidx.recyclerview.widget.GridLayoutManager
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentCelebritiesBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.extension.gone
import com.mustafa.movieapp.extension.visible
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.PeopleAdapter
import com.mustafa.movieapp.view.adapter.RecyclerViewPaginator
import com.mustafa.movieapp.view.ui.common.AppExecutors
import kotlinx.android.synthetic.main.fragment_celebrities.*
import kotlinx.android.synthetic.main.toolbar_search.*
import javax.inject.Inject

@OpenForTesting
class CelebritiesListFragment : Fragment(), Injectable {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel by viewModels<CelebritiesListViewModel> {
        viewModelFactory
    }
    private var binding by autoCleared<FragmentCelebritiesBinding>()

    private var adapter by autoCleared<PeopleAdapter>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_celebrities,
                container,
                false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        subscribers()
        loadMoreStars(page = 1)
    }


    private fun initializeUI() {
        intiToolbar(getString(R.string.fragment_celebrities))
        adapter = PeopleAdapter(dataBindingComponent) {
            findNavController().navigate(
                CelebritiesListFragmentDirections.actionCelebritiesToCelebrity(
                    it
                )
            )
        }

//    if (!adapter.hasObservers()) {
//      adapter.setHasStableIds(true)
//    }

        adapter.setHasStableIds(true)
        recycler_view_main_fragment_star.adapter = adapter

        recycler_view_main_fragment_star.layoutManager = GridLayoutManager(context, 2)
        val paginator = object : RecyclerViewPaginator(
                recyclerView = recycler_view_main_fragment_star,
                hasNext = { viewModel.peopleLiveData.value?.hasNextPage!! }
        ) {
            override fun onLoadMore(currentPage: Int) {
                loadMoreStars(currentPage)
            }
        }
        paginator.resetCurrentPage()
    }


    private fun subscribers() {
        viewModel.peopleLiveData.observe(viewLifecycleOwner, Observer {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
                progressBar.visibility = View.INVISIBLE
            } else if (Status.LOADING == it.status) {
                progressBar.visibility = View.VISIBLE
            }
        })
    }

    private fun loadMoreStars(page: Int) {
        viewModel.setPeoplePage(page)
    }


    fun intiToolbar(title: String) {
//        search_view.setOnSearchClickListener {
//            toolbar_main.toolbar_title.gone()
//        }
//
//        search_view.setOnCloseListener {
//            toolbar_main.toolbar_title.visible()
//            false
//        }
        toolbar_title.text = title
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
