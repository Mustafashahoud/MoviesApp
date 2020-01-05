package com.mustafa.movieapp.view.ui.tv

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
import com.mustafa.movieapp.databinding.FragmentTvsBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.RecyclerViewPaginator
import com.mustafa.movieapp.view.adapter.TvListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import kotlinx.android.synthetic.main.fragment_tvs.*
import kotlinx.android.synthetic.main.toolbar_search.*
import javax.inject.Inject

@OpenForTesting
@Suppress("SpellCheckingInspection")
class TvListFragment : Fragment(), Injectable {


    @Inject
    lateinit var viewmodelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel by viewModels<TvListViewModel> {
        viewmodelFactory
    }
    var binding by autoCleared<FragmentTvsBinding>()

    var adapter by autoCleared<TvListAdapter>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tvs,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this

        initializeUI()
        subscribers()
        loadMoreTvs(page = 1)

    }

    private fun initializeUI() {
        intiToolbar(getString(R.string.fragment_tvs))
        adapter = TvListAdapter(dataBindingComponent) {
            findNavController().navigate(
                TvListFragmentDirections.actionTvsToTvDetail(
                    it
                )
            )
        }
        adapter.setHasStableIds(true)
        recycler_view_main_fragment_tv.adapter = adapter
        recycler_view_main_fragment_tv.layoutManager = GridLayoutManager(context, 2)
        val paginator = object : RecyclerViewPaginator(
            recyclerView = recycler_view_main_fragment_tv,
            hasNext = { viewModel.tvListLiveData.value?.hasNextPage!! }
        ) {
            override fun onLoadMore(currentPage: Int) {
                loadMoreTvs(currentPage)
            }
        }

        paginator.resetCurrentPage()
    }

    private fun loadMoreTvs(page: Int) {
        viewModel.setTvPage(page)
    }

    private fun subscribers() {
        viewModel.tvListLiveData.observe(viewLifecycleOwner, Observer {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
                progressBar.visibility = View.INVISIBLE
            } else if (Status.LOADING == it.status) {
                progressBar.visibility = View.VISIBLE
            }
        })
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

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

}
