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
import androidx.lifecycle.ViewModelProvider
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
import com.mustafa.movieguideapp.view.adapter.PeopleSearchAdapter
import java.util.*
import javax.inject.Inject

class SearchCelebritiesFragment : Fragment(R.layout.fragment_celebrities_search), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<SearchCelebritiesResultViewModel> { viewModelFactory }

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var binding by autoCleared<FragmentCelebritiesSearchBinding>()
    private var adapter by autoCleared<PeopleSearchAdapter>()
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
        binding.toolbarSearchIconfie.searchView.onActionViewExpanded()
        binding.toolbarSearchIconfie.searchView.queryHint = "Search Celebrities"

        binding.toolbarSearchIconfie.voiceSearch.setOnClickListener {
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
                    requireContext(),
                    "your device does not support Speech Input",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        binding.toolbarSearchIconfie.arrowBack.setOnClickListener {
            binding.toolbarSearchIconfie.searchView.clearFocus()
            findNavController().navigate(
                SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToCelebritiesFragment()
            )
        }

        binding.toolbarSearchIconfie.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { navigateToSearchCelebritiesResultFragment(query) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    if (!(text.isEmpty() || text.isBlank())) {
                        showSuggestionViewAndHideRecentSearches()
                        viewModel.setSuggestionQuery(text)
                    }

                    if ((text.isEmpty() || text.isBlank())) {
                        hideSuggestionViewAndShowRecentSearches()
                        adapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                    }
                }

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
        adapter = PeopleSearchAdapter(dataBindingComponent) {
            findNavController().navigate(
                SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToCelebrityDetail(
                    it
                )
            )
        }
        binding.apply {
            recyclerViewSuggestion.adapter = adapter
            recyclerViewSuggestion.layoutManager = LinearLayoutManager(requireContext())
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
        viewModel.peopleRecentQueries.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                setListViewOfRecentQueries(it)
            }
        }

        hasRecentQueriesChanged.observe(viewLifecycleOwner) {
            arrayAdapter?.let { adapter ->
                binding.clearRecentQueries.isClickable = !adapter.isEmpty
            }
        }

        observeSuggestions()
    }

    private fun setListViewOfRecentQueries(queries: List<String?>) {
        arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.recent_query_item,
            queries.requireNoNulls()
        )
        hasRecentQueriesChanged.value = true
        binding.listViewRecentQueries.apply {
            setHeaderDividersEnabled(true)
            setFooterDividersEnabled(true)
            adapter = arrayAdapter
            setOnItemClickListener { parent, _, position, _ ->
                val query = parent.getItemAtPosition(position) as String
                navigateToSearchCelebritiesResultFragment(query)
            }
        }
    }

    private fun observeSuggestions() {
        viewModel.getSuggestions().observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
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