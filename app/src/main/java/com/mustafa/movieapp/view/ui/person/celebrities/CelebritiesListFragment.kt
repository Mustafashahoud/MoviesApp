package com.mustafa.movieapp.view.ui.person.celebrities

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentCelebritiesBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.PeopleAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.RetryCallback
import kotlinx.android.synthetic.main.fragment_celebrities.*
import kotlinx.android.synthetic.main.toolbar_search.*
import timber.log.Timber
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_celebrities,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        with(binding) {
            lifecycleOwner = this@CelebritiesListFragment
            searchResult = viewModel.peopleLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }
        subscribers()
    }


    private fun initializeUI() {
        intiToolbar(getString(R.string.fragment_celebrities))
        adapter = PeopleAdapter(appExecutors, dataBindingComponent) {
            findNavController().navigate(
                CelebritiesListFragmentDirections.actionCelebritiesToCelebrity(
                    it
                )
            )
        }
        recyclerView_list_celebrities.adapter = adapter

        recyclerView_list_celebrities.layoutManager = GridLayoutManager(context, 3)
        recyclerView_list_celebrities.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1
                    && viewModel.peopleLiveData.value?.status != Status.LOADING
                    && dy > 0
                ) {
                    viewModel.loadMore()
                }
            }
        })

        search_icon.setOnClickListener {
            navController().navigate(CelebritiesListFragmentDirections.actionCelebritiesToSearchCelebritiesFragment())
        }
    }


    private fun subscribers() {
        viewModel.peopleLiveData.observe(viewLifecycleOwner, Observer {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
    }

    fun intiToolbar(title: String) {
        toolbar_title.text = title
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
