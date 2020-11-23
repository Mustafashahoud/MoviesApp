package com.mustafa.movieguideapp.view.ui.person.celebrities

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
import androidx.recyclerview.widget.GridLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentCelebritiesBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.Status
import com.mustafa.movieguideapp.utils.InfinitePager
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.PeopleAdapter
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import com.mustafa.movieguideapp.view.ui.common.RetryCallback
import kotlinx.android.synthetic.main.toolbar_search.*
import javax.inject.Inject

class CelebritiesListFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private val viewModel by viewModels<CelebritiesListViewModel> { viewModelFactory }
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
        with(binding) {
            recyclerViewListCelebrities
            recyclerViewListCelebrities.adapter = adapter
            recyclerViewListCelebrities.layoutManager = GridLayoutManager(context, 3)
            recyclerViewListCelebrities.addOnScrollListener(object : InfinitePager(adapter) {
                override fun loadMoreCondition(): Boolean {
                    viewModel.peopleLiveData.value?.let { resource ->
                        return resource.hasNextPage && resource.status != Status.LOADING
                    }
                    return false
                }

                override fun loadMore() {
                    viewModel.loadMore()
                }
            })
        }

        search_icon.setOnClickListener {
            findNavController().navigate(CelebritiesListFragmentDirections.actionCelebritiesToSearchCelebritiesFragment())
        }
    }


    private fun subscribers() {
        viewModel.peopleLiveData.observe(viewLifecycleOwner) {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        }
    }

    private fun intiToolbar(title: String) {
        toolbar_title.text = title
    }
}
