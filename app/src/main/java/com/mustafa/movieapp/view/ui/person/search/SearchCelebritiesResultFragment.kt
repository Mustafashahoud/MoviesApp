package com.mustafa.movieapp.view.ui.person.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentCelebritiesSearchResultBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.extension.hideKeyboard
import com.mustafa.movieapp.models.Status
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.PeopleSearchListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.RetryCallback
import com.mustafa.movieapp.view.ui.search.result.MovieSearchResultFragmentDirections
import kotlinx.android.synthetic.main.fragment_celebrities_search_result.*
import kotlinx.android.synthetic.main.fragment_celebrities_search_result.view.*
import kotlinx.android.synthetic.main.toolbar_search_result.*
import javax.inject.Inject

class SearchCelebritiesResultFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<SearchCelebritiesResultViewModel> { viewModelFactory }
    var dataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentCelebritiesSearchResultBinding>()
    private var adapter by autoCleared<PeopleSearchListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_celebrities_search_result,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            searchResult = viewModel.searchPeopleListLiveData
            query = viewModel.queryPersonLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }

        initializeUI()
        subscribers()
        viewModel.setSearchPeopleQueryAndPage(getQuerySafeArgs(), 1)


    }

    private fun subscribers() {
        viewModel.searchPeopleListLiveData.observe(viewLifecycleOwner, Observer {
            binding.searchResult = viewModel.searchPeopleListLiveData
            if (it.data != null && it.data.isNotEmpty()) {
                adapter.submitList(it.data)
            }
        })
    }


    private fun getQuerySafeArgs(): String? {
        val params =
            SearchCelebritiesResultFragmentArgs.fromBundle(
                requireArguments()
            )
        return params.query
    }

    private fun initializeUI() {

        adapter = PeopleSearchListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            navController().navigate(
                SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToCelebrityDetail(
                    it
                )
            )
        }

        hideKeyboard()
        binding.root.recyclerView_search_result_people.adapter = adapter
        recyclerView_search_result_people.layoutManager = LinearLayoutManager(context)
        recyclerView_search_result_people.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1
                    && viewModel.searchPeopleListLiveData.value?.status != Status.LOADING
                    && dy > 0
                ) {
                    if (viewModel.searchPeopleListLiveData.value?.hasNextPage!!) {
                        viewModel.loadMore()
                    }
                }
            }
        })

        search_view.setOnSearchClickListener {
            navController().navigate(MovieSearchResultFragmentDirections.actionMovieSearchFragmentResultToMovieSearchFragment())
        }

        arrow_back.setOnClickListener {
            navController().navigate(SearchCelebritiesResultFragmentDirections.actionSearchCelebritiesResultFragmentToSearchCelebritiesFragment())
        }
    }

    /**
     * Receiving Voice Query
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            10 -> if (resultCode == Activity.RESULT_OK && data != null) {
                val voiceQuery = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                hideKeyboard()
                search_view.setQuery(voiceQuery?.let { it[0] }, true)
            }
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}