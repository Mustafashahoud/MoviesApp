package com.mustafa.movieguideapp.view.ui.tv.tvlist

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
import com.mustafa.movieguideapp.databinding.FragmentTvsBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.models.Status
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.TvListAdapter
import com.mustafa.movieguideapp.view.ui.common.InfinitePager
import com.mustafa.movieguideapp.view.ui.common.RetryCallback
import kotlinx.android.synthetic.main.toolbar_search.*
import javax.inject.Inject

class TvListFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel by viewModels<TvListViewModel> {
        viewModelFactory
    }
    private var binding by autoCleared<FragmentTvsBinding>()

    private var adapter by autoCleared<TvListAdapter>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tvs,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            lifecycleOwner = this@TvListFragment
            searchResult = viewModel.tvListLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }

        initializeUI()
        subscribers()

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
        binding.recyclerViewListTvs.adapter = adapter
        binding.recyclerViewListTvs.layoutManager = GridLayoutManager(context, 3)
        binding.recyclerViewListTvs.addOnScrollListener(object :
            InfinitePager(adapter) {
            override fun loadMoreCondition(): Boolean {
                viewModel.tvListLiveData.value?.let { resource ->
                    return resource.hasNextPage && resource.status != Status.LOADING
                }
                return false
            }

            override fun loadMore() {
                viewModel.loadMore()
            }
        })

        search_icon.setOnClickListener {
            findNavController().navigate(
                TvListFragmentDirections.actionTvsFragmentToTvSearchFragment()
            )
        }
    }

    private fun subscribers() {
        viewModel.tvListLiveData.observe(viewLifecycleOwner, {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
    }

    private fun intiToolbar(title: String) {
        toolbar_title.text = title
    }

}
