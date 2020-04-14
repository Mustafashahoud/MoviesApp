package com.mustafa.movieapp.view.ui.person.search

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentCelebritiesSearchBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.extension.gone
import com.mustafa.movieapp.extension.inVisible
import com.mustafa.movieapp.extension.visible
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.PeopleSearchListAdapter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import java.util.*
import javax.inject.Inject

class SearchCelebritiesFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<SearchCelebritiesResultViewModel> { viewModelFactory }

    var dataBindingComponent = FragmentDataBindingComponent(this)

    private var binding by autoCleared<FragmentCelebritiesSearchBinding>()

    private var adapter by autoCleared<PeopleSearchListAdapter>()

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
        initializeUI()
        subscribers()
    }


    fun initializeUI() {
        initToolbar()
        initAdapter()
    }

    /**
     * Init the toolbar
     */
    private fun initToolbar() {
        search_view.onActionViewExpanded()

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
            findNavController().navigate(SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToCelebritiesFragment())
        }

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                navigateToSearchCelebritiesResultFragment(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setPeopleSuggestionsQuery(newText!!)
                observeSuggestions(newText)
                return true
            }
        })
    }

    private fun initAdapter() {
        adapter = PeopleSearchListAdapter(appExecutors, dataBindingComponent) {
            findNavController().navigate(
                SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToCelebrityDetail(
                    it
                )
            )

        }
        recyclerView_suggestion.adapter = adapter
        recyclerView_suggestion.layoutManager = LinearLayoutManager(context)
    }

    private fun navigateToSearchCelebritiesResultFragment(query: String?) {
        findNavController().navigate(
            SearchCelebritiesFragmentDirections.actionSearchCelebritiesFragmentToSearchCelebritiesResultFragment(
                query!!
            )
        )
    }


    private fun subscribers() {
        viewModel.getPeopleRecentQueries().observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                val queries = it.map { peopleRecentQuery -> peopleRecentQuery.query }
                setListViewOfRecentQueries(queries)
            }
        })
    }

    /**
     *
     */
    private fun setListViewOfRecentQueries(queries: List<String?>) {
        val arrayAdapter =
            ArrayAdapter<String>(requireContext(), R.layout.recent_query_item, queries)

        listView_recent_queries.setHeaderDividersEnabled(true)
        listView_recent_queries.setFooterDividersEnabled(true)
        listView_recent_queries.adapter = arrayAdapter
        listView_recent_queries.setOnItemClickListener { parent, _, position, _ ->
            val query = parent.getItemAtPosition(position) as String
            navigateToSearchCelebritiesResultFragment(query)
        }

        clear_recent_queries.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            builder.setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.clear) { _, _ ->
                    viewModel.deleteAllPeopleRecentQueries()
                    arrayAdapter.clear()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }

            builder.create().show()
        }
    }

    private fun observeSuggestions(newText: String?) {
        viewModel.peopleSuggestions.observe(viewLifecycleOwner, Observer {
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
        })
    }

    private fun hideSuggestionViewAndShowRecentSearches() {
        recent_queries_bar.visible()
        listView_recent_queries.visible()
        recyclerView_suggestion.inVisible()
    }

    private fun showSuggestionViewAndHideRecentSearches() {
        recent_queries_bar.gone()
        listView_recent_queries.inVisible()
        recyclerView_suggestion.visible()
    }
}