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
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentTvSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.hideKeyboard
import com.mustafa.movieguideapp.models.Status
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.TvSearchListAdapter
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import com.mustafa.movieguideapp.view.ui.common.RetryCallback
import com.mustafa.movieguideapp.view.ui.search.TvSearchViewModel
import kotlinx.android.synthetic.main.fragment_tv_search_result.*
import kotlinx.android.synthetic.main.fragment_tv_search_result.view.*
import kotlinx.android.synthetic.main.toolbar_search_result.*
import javax.inject.Inject

class TvSearchResultFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<TvSearchViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentTvSearchResultBinding>()
    private var adapter by autoCleared<TvSearchListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tv_search_result,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeUI()

        subscribers()

        viewModel.setSearchTvQueryAndPage(getQuerySafeArgs(), 1)

        with(binding) {
            lifecycleOwner = this@TvSearchResultFragment
            searchResult = viewModel.searchTvListLiveData
            query = viewModel.queryTvLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }
    }

    private fun subscribers() {
        viewModel.searchTvListLiveData.observe(viewLifecycleOwner) {
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        }
    }


    private fun getQuerySafeArgs(): String? {
        val params =
            TvSearchResultFragmentArgs.fromBundle(
                requireArguments()
            )
        return params.query
    }

    private fun initializeUI() {
        adapter = TvSearchListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            findNavController().navigate(
                TvSearchResultFragmentDirections.actionTvSearchFragmentResultToTvDetail(it)
            )
        }

        hideKeyboard()
        binding.root.recyclerView_search_result_tvs.adapter = adapter

        recyclerView_search_result_tvs.layoutManager = LinearLayoutManager(context)

        recyclerView_search_result_tvs.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1
                    && viewModel.searchTvListLiveData.value?.status != Status.LOADING
                ) {
                    if (viewModel.searchTvListLiveData.value?.hasNextPage!!) {
                        viewModel.loadMore()
                    }
                }
            }
        })

        search_view.setOnSearchClickListener {
            findNavController().navigate(TvSearchResultFragmentDirections.actionTvSearchFragmentResultToTvSearchFragment())
        }

        arrow_back.setOnClickListener {
            findNavController().navigate(TvSearchResultFragmentDirections.actionTvSearchFragmentResultToTvSearchFragment())
        }
    }
}