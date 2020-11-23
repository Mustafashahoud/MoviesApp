package com.mustafa.movieguideapp.view.ui.person.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentCelebritiesSearchBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.gone
import com.mustafa.movieguideapp.extension.inVisible
import com.mustafa.movieguideapp.extension.visible
import com.mustafa.movieguideapp.utils.ActivityResultApiObserver
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.PeopleSearchListAdapter
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import com.mustafa.movieguideapp.view.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import javax.inject.Inject

class SearchCelebritiesFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private lateinit var activityResultApiObserver: ActivityResultApiObserver

    private val viewModel by viewModels<SearchCelebritiesResultViewModel> { viewModelFactory }

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var binding by autoCleared<FragmentCelebritiesSearchBinding>()

    private var adapter by autoCleared<PeopleSearchListAdapter>()

    private var arrayAdapter: ArrayAdapter<String>? = null

    private val hasRecentQueriesChanged = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_celebrities_search,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initResultApiObserve()
        initializeUI()
        subscribers()
    }

    private fun initResultApiObserve() {
        activityResultApiObserver =
            ActivityResultApiObserver(requireActivity().activityResultRegistry) { query ->
                query?.let {
                    navigateToSearchCelebritiesResultFragment(it)
                }
            }
        lifecycle.addObserver(activityResultApiObserver)
    }


    fun initializeUI() {
        initToolbar()
        initRecyclerView()
    }

    /**
     * Init the toolbar
     */
    private fun initToolbar() {
        search_view.onActionViewExpanded()

        search_view.queryHint = "Search Celebrities"

        voice_search.setOnClickListener {
            val voiceIntent = (activity as MainActivity).getVoiceRecognitionIntent()
            voiceIntent?.let {
                activityResultApiObserver.startVoiceRecognitionActivityForResult(it)
            }
        }

        arrow_back.setOnClickListener {
            search_view.clearFocus()
            findNavController().navigate(SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToCelebritiesFragment())
        }

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { navigateToSearchCelebritiesResultFragment(query) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setPeopleSuggestionsQuery(newText!!)
                observeSuggestions(newText)
                return true
            }
        })

        binding.clearRecentQueries.setOnClickListener {
            arrayAdapter?.let {
                val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                builder.setMessage(R.string.dialog_message)
                    .setPositiveButton(R.string.clear) { _, _ ->
                        viewModel.deleteAllPeopleRecentQueries()
                        it.clear()
                        listView_recent_queries.inVisible()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }

                builder.create().show()
            }
        }
    }

    private fun initRecyclerView() {
        adapter = PeopleSearchListAdapter(appExecutors, dataBindingComponent) {
            findNavController().navigate(
                SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToCelebrityDetail(
                    it
                )
            )
        }
        binding.apply {
            recyclerViewSuggestion.adapter = adapter
            recyclerViewSuggestion.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun navigateToSearchCelebritiesResultFragment(query: String) {
        findNavController().navigate(
            SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToSearchCelebritiesResultFragment(
                query
            )
        )
    }


    private fun subscribers() {
        viewModel.getPeopleRecentQueries().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                val queries = it.map { peopleRecentQuery -> peopleRecentQuery.query }
                setListViewOfRecentQueries(queries)
            }
        }

        hasRecentQueriesChanged.observe(viewLifecycleOwner) {
            arrayAdapter?.let { adapter -> clear_recent_queries.isClickable = !adapter.isEmpty }
        }
    }

    /**
     *
     */
    private fun setListViewOfRecentQueries(queries: List<String?>) {
        arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.recent_query_item,
            queries.requireNoNulls()
        )
        hasRecentQueriesChanged.value = true
        binding.apply {
            listViewRecentQueries.setHeaderDividersEnabled(true)
            listViewRecentQueries.setFooterDividersEnabled(true)
            listViewRecentQueries.adapter = arrayAdapter
            listViewRecentQueries.setOnItemClickListener { parent, _, position, _ ->
                val query = parent.getItemAtPosition(position) as String
                navigateToSearchCelebritiesResultFragment(query)
            }
        }


    }

    private fun observeSuggestions(newText: String?) {
        viewModel.peopleSuggestions.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                showSuggestionViewAndHideRecentSearches()
            }
            adapter.submitList(it)

            if (newText != null) {
                if ((newText.isEmpty() || newText.isBlank())) {
                    hideSuggestionViewAndShowRecentSearches()
                    adapter.submitList(null)
                }
            }
        }
    }

    private fun hideSuggestionViewAndShowRecentSearches() {
        binding.apply {
            recentQueriesBar.visible()
            listViewRecentQueries.visible()
            recyclerViewSuggestion.inVisible()
        }
    }

    private fun showSuggestionViewAndHideRecentSearches() {
        binding.apply {
            recentQueriesBar.gone()
            listViewRecentQueries.inVisible()
            recyclerViewSuggestion.visible()
        }
    }
}