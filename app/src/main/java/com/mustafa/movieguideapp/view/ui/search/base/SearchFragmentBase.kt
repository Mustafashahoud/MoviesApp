package com.mustafa.movieguideapp.view.ui.search.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognizerIntent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.extension.gone
import com.mustafa.movieguideapp.extension.inVisible
import com.mustafa.movieguideapp.extension.visible
import com.mustafa.movieguideapp.models.SelectableItem
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.COUNTRIES
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.GENRES
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.KEYWORDS
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.LANGUAGES
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.RATINGS
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.RUNTIME
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.YEARS
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.countryFilters
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.genreFilters
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.keywordFilters
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.languageFilters
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.ratingFilters
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.runtimeFilters
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.yearFilters
import com.mustafa.movieguideapp.view.adapter.FilterMultiSelectableAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search_filter.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import org.jetbrains.anko.textColor
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class SearchFragmentBase : Fragment() {

    private var isComingFromEdit = false

    private val hasAnyFilterBeenSelected = MutableLiveData<Boolean>()

    private val mapFilterTypeToSelectedFilters =
        HashMap<FilterMultiSelectableAdapter, ArrayList<String>>()

    private var mapFilterTypeToSelectedFiltersToBeEdited =
        HashMap<FilterMultiSelectableAdapter, ArrayList<String>>()

    private var filtersToReSelect = ArrayList<String>()

    private var hasRecentQueriesChanged = MutableLiveData<Boolean>()

    private var arrayAdapter: ArrayAdapter<String>? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeRecentQueriesChanged()
        initializeUI()
        subscribers()
        setBindingVariables()

        if (isComingFromEdit) {
            val filterTab = tabs.getTabAt(1)
            filterTab?.select()
            renderViewsWhenFiltersTabSelected()
        } else {
            val recentTab = tabs.getTabAt(0)
            recentTab?.select()
            renderViewsWhenRecentTabSelected()
            search_view.onActionViewExpanded()
        }

    }

    private fun observeRecentQueriesChanged() {
        hasRecentQueriesChanged.observe(viewLifecycleOwner) {
            arrayAdapter?.let { adapter -> clear_recent_queries.isClickable = !adapter.isEmpty }
        }
    }


    private fun initializeUI() {

        initToolbar()

        initTabLayout(tabs)

        setSearchViewHint()

        setFilterButtons()

        initClearRecentClearAndSeeResultBarWidgets()

        if (tabs.getTabAt(0)?.isSelected!!)
            observeAndSetRecentQueries()

        // Adapter
        setRecyclerViewAdapter()

    }

    private fun initClearRecentClearAndSeeResultBarWidgets() {
        clear_filter.setOnClickListener {

            for (adapter in mapFilterTypeToSelectedFilters.keys) {
                adapter.clearSelection()
            }

            mapFilterTypeToSelectedFilters.map {
                it.value.clear()
            }
            filtersToReSelect.clear()
            hasAnyFilterBeenSelected.value = true
        }

        see_result.setOnClickListener {
            populateFiltersToBeEditedMap()
            val stringKeyMap = convertAdapterKeyMapToStringKeyMap()

            stringKeyMap.map { entry ->
                entry.value.map {
                    if (it.isNotBlank() || it.isNotEmpty())
                        filtersToReSelect.add(it)
                }
            }
            val bundle = bundleOf("key" to stringKeyMap)
            navigateFromSearchFragmentToSearchFragmentResultFilter(bundle)

        }


        clear_recent_queries.setOnClickListener {
            arrayAdapter?.let {
                val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                builder.setMessage(R.string.dialog_message)
                    .setPositiveButton(R.string.clear) { _, _ ->
                        deleteAllRecentQueries()
                        it.clear()
                        hideRecentQueries()
                        hasRecentQueriesChanged.value = true
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }

                builder.create().show()
            }
        }
    }


    private fun populateFiltersToBeEditedMap() {
        mapFilterTypeToSelectedFilters.map {
            mapFilterTypeToSelectedFiltersToBeEdited[it.key] = it.value
        }
    }

    private fun checkIfAllFiltersEmpty(): Boolean {
        mapFilterTypeToSelectedFilters.map {
            if (it.value.isNotEmpty()) {
                return false
            }
        }
        return true
    }

    /**
     * @return
     */
    private fun convertAdapterKeyMapToStringKeyMap(): Map<String, List<String>> {
        val mapStringAdapterNameToSelectedFilters = HashMap<String, List<String>>()
        mapFilterTypeToSelectedFilters.map {
            when (it.key.adapterName) {
                RATINGS -> mapStringAdapterNameToSelectedFilters[RATINGS] = it.value
                RUNTIME -> mapStringAdapterNameToSelectedFilters[RUNTIME] = it.value
                KEYWORDS -> mapStringAdapterNameToSelectedFilters[KEYWORDS] = it.value
                LANGUAGES -> mapStringAdapterNameToSelectedFilters[LANGUAGES] = it.value
                YEARS -> mapStringAdapterNameToSelectedFilters[YEARS] = it.value
                GENRES -> mapStringAdapterNameToSelectedFilters[GENRES] = it.value
                COUNTRIES -> mapStringAdapterNameToSelectedFilters[COUNTRIES] = it.value
            }
        }

        mapFilterTypeToSelectedFilters.clear()

        return mapStringAdapterNameToSelectedFilters
    }

    private fun subscribers() {
        hasAnyFilterBeenSelected.observe(viewLifecycleOwner) {
            if (it != null && it == true) {
                if (!checkIfAllFiltersEmpty()) {
                    clear_filter.isEnabled = true
                    clear_filter.textColor =
                        context?.resources?.getColor(R.color.colorAccent, context?.theme)!!
                } else {
                    clear_filter.isEnabled = false // to NOT let ripple effect work
                    clear_filter.textColor =
                        context?.resources?.getColor(R.color.clearFilterColor, context?.theme)!!
                }
            }
        }
    }

    private fun initTabLayout(tabs: TabLayout) {

        val layoutTab1 = ((tabs.getChildAt(0)) as LinearLayout).getChildAt(0) as LinearLayout
        val layoutTab2 = ((tabs.getChildAt(0)) as LinearLayout).getChildAt(1) as LinearLayout

        val layoutParams1 = layoutTab1.layoutParams as LinearLayout.LayoutParams
        val layoutParams2 = layoutTab2.layoutParams as LinearLayout.LayoutParams

        layoutParams1.marginStart = 125
        layoutParams1.width = ActionBar.LayoutParams.WRAP_CONTENT
        layoutTab1.layoutParams = layoutParams1

        layoutParams2.marginEnd = 125
        layoutParams2.width = ActionBar.LayoutParams.WRAP_CONTENT
        layoutParams2.weight = 0.5f
        layoutTab2.layoutParams = layoutParams2

        setFilterTabName(tabs.getTabAt(1))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        isComingFromEdit = false
                        renderViewsWhenRecentTabSelected()
                    }
                    1 -> {
                        isComingFromEdit = true
                        renderViewsWhenFiltersTabSelected()
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setFilterButtons() {
        val runTimeRecyclerView =
            activity?.findViewById<RecyclerView>(R.id.recycler_view_runtimes)
        setFilterAdapter(
            runTimeRecyclerView, runtimeFilters,
            RUNTIME
        )

        val ratingRecyclerView = activity?.findViewById<RecyclerView>(R.id.recycler_view_ratings)
        setFilterAdapter(
            ratingRecyclerView, ratingFilters,
            RATINGS
        )

        val genreAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_genres)
        setFilterAdapter(
            genreAdapter, genreFilters,
            GENRES
        )

        val yearAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_years)
        setFilterAdapter(
            yearAdapter, yearFilters,
            YEARS
        )

        val keywordAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_keywords)
        setFilterAdapter(
            keywordAdapter, keywordFilters,
            KEYWORDS
        )

        val languageAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_languages)
        setFilterAdapter(
            languageAdapter, languageFilters,
            LANGUAGES
        )

        val countriesAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_countries)
        setFilterAdapter(
            countriesAdapter, countryFilters,
            COUNTRIES
        )

    }


    private fun setFilterAdapter(
        recyclerView: RecyclerView?,
        listOfButtonFiltersTitles: List<String>,
        adapterName: String
    ) {
        val filters = ArrayList<String>()
        val selectableItemList = ArrayList<SelectableItem>()

        for (item in listOfButtonFiltersTitles) {
            val wasItemSelected = filtersToReSelect.contains(item)
            val selectableItem =
                SelectableItem(item, wasItemSelected)
            selectableItemList.add(selectableItem)
        }

        val filterAdapter =
            FilterMultiSelectableAdapter(
                selectableItemList,
                context,
                {
                    filters.add(it)
                    hasAnyFilterBeenSelected.value = true
                },
                {

                    if (filters.size > 0) filters.remove(it)
                    if (filtersToReSelect.contains(it)) filtersToReSelect.remove(it)

                    this.hasAnyFilterBeenSelected.value = true
                },
                adapterName
            )

        // in case we are coming back from edit button, we need to re-check the filters that have been checked before
        selectableItemList.map { selectableItem ->
            if (selectableItem.isSelected) filters.add(selectableItem.title)
        }
        this.mapFilterTypeToSelectedFilters[filterAdapter] = filters
        recyclerView?.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        recyclerView?.adapter = filterAdapter
        recyclerView?.setHasFixedSize(true)
    }


    /**
     *
     */
    protected fun setListViewOfRecentQueries(queries: List<String?>) {
        arrayAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.recent_query_item,
                queries.requireNoNulls()
            )
        if (tabs.getTabAt(0)?.isSelected!!) {
            showRecentQueries()
        }
        hasRecentQueriesChanged.value = true
        listView_recent_queries.setHeaderDividersEnabled(true)
        listView_recent_queries.setFooterDividersEnabled(true)
        listView_recent_queries.adapter = arrayAdapter
        listView_recent_queries.setOnItemClickListener { parent, _, position, _ ->
            val query = parent.getItemAtPosition(position) as String
            navigateFromSearchFragmentToSearchFragmentResult(query)
        }


    }

    /**
     * Init the toolbar
     */
    private fun initToolbar() {

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
            navigateFromSearchFragmentToListItemsFragment()
        }

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                navigateFromSearchFragmentToSearchFragmentResult(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setSuggestionsQuery(newText)
                observeSuggestions(newText)
                return true
            }
        })
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
                dismissKeyboard(search_view.windowToken)
                search_view.setQuery(voiceQuery?.let { it[0] }, true)
            }
        }
    }

    /**
     *
     */
    private fun renderViewsWhenRecentTabSelected() {
        search_view.requestFocus()
        hideFiltersLayout()
        search_view.visible()
        voiceSearch.visible()
        filter_label.gone()
        if (search_view.query.isEmpty() || search_view.query.isBlank()) {
            showRecentSearchesBar()
            showRecentQueries()
        } else {
            hideRecentQueries()
            recyclerView_suggestion.visible()
        }
    }

    /**
     *
     */
    private fun renderViewsWhenFiltersTabSelected() {
        hideListViewAndRecyclerView()
        search_view.gone()
        voiceSearch.gone()
        filter_label.text = getString(R.string.filters)
        filter_label.visible()
        showFiltersLayout()
        hideRecentSearchesBar()
        dismissKeyboard(search_view.windowToken)
    }

    private fun hideFiltersLayout() {
        filters.inVisible()
    }

    private fun showFiltersLayout() {
        filters.visible()
    }


    private fun hideRecentSearchesBar() {
        recent_queries_bar.gone()
    }

    private fun showRecentSearchesBar() {
        recent_queries_bar.visible()
    }


    private fun hideListViewAndRecyclerView() {
        listView_recent_queries.inVisible()
        recyclerView_suggestion.inVisible()

    }

    private fun hideRecentQueries() {
        listView_recent_queries.inVisible()
    }

    private fun showRecentQueries() {
        listView_recent_queries.visible()
    }

    protected fun showSuggestionViewAndHideRecentSearches() {
        recyclerView_suggestion.visible()
        hideRecentQueries()
        hideRecentSearchesBar()
    }

    protected fun hideSuggestionViewAndShowRecentSearches() {
        showRecentSearchesBar()
        showRecentQueries()
        recyclerView_suggestion.inVisible()
    }


    private fun Fragment.dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    protected abstract fun setBindingVariables()
    protected abstract fun setSuggestionsQuery(newText: String?)
    protected abstract fun deleteAllRecentQueries()
    protected abstract fun observeAndSetRecentQueries()
    protected abstract fun setRecyclerViewAdapter()
    protected abstract fun setSearchViewHint()
    protected abstract fun observeSuggestions(newText: String?)
    protected abstract fun navigateFromSearchFragmentToSearchFragmentResultFilter(bundle: Bundle)
    protected abstract fun navigateFromSearchFragmentToSearchFragmentResult(query: String)
    protected abstract fun navigateFromSearchFragmentToListItemsFragment()
    protected abstract fun setFilterTabName(tab: TabLayout.Tab?)

}