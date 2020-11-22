package com.mustafa.movieguideapp.view.ui.person.search

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentCelebritiesSearchBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.extension.gone
import com.mustafa.movieguideapp.extension.inVisible
import com.mustafa.movieguideapp.extension.visible
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.PeopleAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class SearchCelebritiesFragment : Fragment(R.layout.fragment_celebrities_search), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<SearchCelebritiesResultViewModel> { viewModelFactory }

    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var binding by autoCleared<FragmentCelebritiesSearchBinding>()

    private var adapter by autoCleared<PeopleAdapter>()

    private var arrayAdapter: ArrayAdapter<String>? = null

    private val hasRecentQueriesChanged = MutableLiveData<Boolean>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCelebritiesSearchBinding.bind(view)
        initializeUI()
        subscribers()
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

        voiceSearch.setOnClickListener {
            val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            voiceIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            if (activity?.packageManager?.let { it1 -> voiceIntent.resolveActivity(it1) } != null) {
                startActivityForResult(voiceIntent, 10)
            } else {
                Toast.makeText(
                    context,
                    "your device does not support Speech Input",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        arrow_back.setOnClickListener {
            search_view.clearFocus()
            findNavController().navigate(
                SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToCelebritiesFragment()
            )
        }

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                navigateToSearchCelebritiesResultFragment(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.getSuggestions(newText!!)
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
                        binding.listViewRecentQueries.inVisible()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }

                builder.create().show()
            }
        }
    }

    private fun initRecyclerView() {
        adapter = PeopleAdapter(dataBindingComponent) {
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

    private fun navigateToSearchCelebritiesResultFragment(query: String?) {
        findNavController().navigate(
            SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToSearchCelebritiesResultFragment(
                query!!
            )
        )
    }


    private fun subscribers() {
        viewModel.peopleRecentQueries.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                setListViewOfRecentQueries(it)
            }
        })

        hasRecentQueriesChanged.observe(viewLifecycleOwner, Observer {
            arrayAdapter?.let { adapter -> clear_recent_queries.isClickable = !adapter.isEmpty }
        })
    }

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
        newText?.let {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getSuggestions(it).collectLatest { pagingData ->
                    if (it.isNotEmpty()) {
                        showSuggestionViewAndHideRecentSearches()
                    }
                    adapter.submitData(pagingData)
                    if ((newText.isEmpty() || newText.isBlank())) {
                        hideSuggestionViewAndShowRecentSearches()
                        adapter.submitData(PagingData.empty())
                    }
                }
            }
        }
    }

    private fun hideSuggestionViewAndShowRecentSearches() {
        binding.recentQueriesBar.visible()
        binding.listViewRecentQueries.visible()
        binding.recyclerViewSuggestion.inVisible()
    }

    private fun showSuggestionViewAndHideRecentSearches() {
        binding.recentQueriesBar.gone()
        binding.listViewRecentQueries.inVisible()
        binding.recyclerViewSuggestion.visible()
    }
}