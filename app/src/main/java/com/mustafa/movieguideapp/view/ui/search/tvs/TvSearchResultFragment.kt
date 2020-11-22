package com.mustafa.movieguideapp.view.ui.search.tvs

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentTvSearchResultBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.hideKeyboard
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.TvsSearchAdapter
import kotlinx.android.synthetic.main.toolbar_search_result.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TvSearchResultFragment : Fragment(R.layout.fragment_tv_search_result), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TvSearchViewModel> { viewModelFactory }
    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentTvSearchResultBinding>()
    private var adapter by autoCleared<TvsSearchAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentTvSearchResultBinding.bind(view)

        initializeUI()

        getQuerySafeArgs()?.let { query ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchTvs(query).collectLatest {
                    adapter.submitData(it)
                }
            }
        }

//        with(binding) {
//            lifecycleOwner = this@TvSearchResultFragment
//            searchResult = viewModel.searchTvListLiveData
//            query = viewModel.queryTvLiveData
//            callback = object : RetryCallback {
//                override fun retry() {
//                    viewModel.refresh()
//                }
//            }
//        }
    }


    private fun getQuerySafeArgs(): String? {
        val params =
            TvSearchResultFragmentArgs.fromBundle(
                requireArguments()
            )
        return params.query
    }

    private fun initializeUI() {
        adapter = TvsSearchAdapter(
            dataBindingComponent
        ) {
            findNavController().navigate(
                TvSearchResultFragmentDirections.actionTvSearchFragmentResultToTvDetail(
                    it
                )
            )
        }

        hideKeyboard()
        binding.recyclerViewSearchResultTvs.adapter = adapter
        binding.recyclerViewSearchResultTvs.layoutManager = LinearLayoutManager(context)

        search_view.setOnSearchClickListener {
            findNavController().navigate(TvSearchResultFragmentDirections.actionTvSearchFragmentResultToTvSearchFragment())
        }

        arrow_back.setOnClickListener {
            findNavController().navigate(TvSearchResultFragmentDirections.actionTvSearchFragmentResultToTvSearchFragment())
        }
    }
}