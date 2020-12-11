package com.mustafa.movieguideapp.view.ui.search.base

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
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
import com.mustafa.movieguideapp.databinding.FragmentSearchBinding
import com.mustafa.movieguideapp.extension.gone
import com.mustafa.movieguideapp.extension.inVisible
import com.mustafa.movieguideapp.extension.isEmptyOrBlank
import com.mustafa.movieguideapp.extension.visible
import com.mustafa.movieguideapp.models.SelectableItem
import com.mustafa.movieguideapp.utils.ActivityResultApiObserver
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
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.FilterMultiSelectableAdapter
import com.mustafa.movieguideapp.view.ui.main.MainActivity
import org.jetbrains.anko.textColor

abstract class SearchFragmentBase(@LayoutRes layout: Int) : Fragment(layout) {

    private var isComingFromEdit = false

    private val hasAnyFilterBeenSelected = MutableLiveData<Boolean>()

    private var mapFilterTypeToSelectedFilters by autoCleared<HashMap<FilterMultiSelectableAdapter, ArrayList<String>>>()

    private var mapFilterTypeToSelectedFiltersToBeEdited by autoCleared<HashMap<FilterMultiSelectableAdapter, ArrayList<String>>>()

    private var filtersToReSelect = ArrayList<String>()

    private var hasRecentQueriesChanged = MutableLiveData<Boolean>()

    private var arrayAdapter: ArrayAdapter<String>? = null

    protected var searchBinding by autoCleared<FragmentSearchBinding>()

    private lateinit var activityResultApiObserver: ActivityResultApiObserver


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initResultApiObserve()

        searchBinding = FragmentSearchBinding.bind(view)


        mapFilterTypeToSelectedFilters = HashMap()
        mapFilterTypeToSelectedFiltersToBeEdited = HashMap()

        observeRecentQueriesChanged()
        initializeUI()
        subscribers()
        setBindingVariables()

        if (isComingFromEdit) {
            val filterTab = searchBinding.tabs.getTabAt(1)
            filterTab?.select()
            renderViewsWhenFiltersTabSelected()
        } else {
            val recentTab = searchBinding.tabs.getTabAt(0)
            recentTab?.select()
            renderViewsWhenRecentTabSelected()
            searchBinding.toolbarSearch.searchView.onActionViewExpanded()
        }

    }

    private fun initResultApiObserve() {
        activityResultApiObserver =
            ActivityResultApiObserver(requireActivity().activityResultRegistry) { query ->
                query?.let {
                    navigateFromSearchFragmentToSearchFragmentResult(it)
                }
            }
        lifecycle.addObserver(activityResultApiObserver)
    }

    private fun observeRecentQueriesChanged() {
        hasRecentQueriesChanged.observe(viewLifecycleOwner) {
            arrayAdapter?.let { adapter ->
                searchBinding.clearRecentQueries.isClickable = !adapter.isEmpty
            }
        }
    }


    private fun initializeUI() {

        initToolbar()

        initTabLayout(searchBinding.tabs)

        setSearchViewHint()

        setFilterButtons()

        initClearRecentClearAndSeeResultBarWidgets()

        if (searchBinding.tabs.getTabAt(0)?.isSelected!!)
            observeAndSetRecentQueries()

        // Adapter
        setRecyclerViewAdapter()

        observeSuggestions()

    }

    private fun initClearRecentClearAndSeeResultBarWidgets() {
        searchBinding.filters.clearFilter.setOnClickListener {

            for (adapter in mapFilterTypeToSelectedFilters.keys) {
                adapter.clearSelection()
            }

            mapFilterTypeToSelectedFilters.map {
                it.value.clear()
            }
            filtersToReSelect.clear()
            hasAnyFilterBeenSelected.value = true
        }

        searchBinding.filters.seeResult.setOnClickListener {
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


        searchBinding.clearRecentQueries.setOnClickListener {
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
                    searchBinding.filters.clearFilter.isEnabled = true
                    searchBinding.filters.clearFilter.textColor =
                        context?.resources?.getColor(R.color.colorAccent, context?.theme)!!
                } else {
                    searchBinding.filters.clearFilter.isEnabled =
                        false // to NOT let ripple effect work
                    searchBinding.filters.clearFilter.textColor =
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
        recyclerView?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
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

        hasRecentQueriesChanged.value = true
        searchBinding.listViewRecentQueries.apply {
            setHeaderDividersEnabled(true)
            setFooterDividersEnabled(true)
            adapter = arrayAdapter
            setOnItemClickListener { parent, _, position, _ ->
                val query = parent.getItemAtPosition(position) as String
                navigateFromSearchFragmentToSearchFragmentResult(query)
            }
        }
    }

    /**
     * Init the toolbar
     */
    private fun initToolbar() {

        searchBinding.toolbarSearch.arrowBack.setOnClickListener {
            val voiceIntent = (activity as MainActivity).getVoiceRecognitionIntent()
            voiceIntent?.let {
                activityResultApiObserver.startVoiceRecognitionActivityForResult(it)
            }
        }


        searchBinding.toolbarSearch.arrowBack.setOnClickListener {
            searchBinding.toolbarSearch.searchView.clearFocus()
            navigateFromSearchFragmentToListItemsFragment()
        }

        searchBinding.toolbarSearch.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { navigateFromSearchFragmentToSearchFragmentResult(query) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setSuggestionsQuery(newText)
                return true
            }
        })
    }

    /**
     *
     */
    private fun renderViewsWhenRecentTabSelected() {
        searchBinding.toolbarSearch.searchView.requestFocus()
        hideFiltersLayout()
        searchBinding.toolbarSearch.searchView.visible()
        searchBinding.toolbarSearch.voiceSearch.visible()
        searchBinding.toolbarSearch.filterLabel.gone()
        if (isEmptyOrBlank(searchBinding.toolbarSearch.searchView.query.toString())) {
            showRecentSearchesBar()
            showRecentQueries()
        } else {
            hideRecentQueries()
            showSuggestion()
        }
    }

    /**
     *
     */
    private fun renderViewsWhenFiltersTabSelected() {
        hideListViewAndRecyclerView()
        searchBinding.toolbarSearch.searchView.gone()
        searchBinding.toolbarSearch.voiceSearch.gone()
        searchBinding.toolbarSearch.filterLabel.text = getString(R.string.filters)
        searchBinding.toolbarSearch.filterLabel.visible()
        showFiltersLayout()
        hideRecentSearchesBar()
        dismissKeyboard(searchBinding.toolbarSearch.searchView.windowToken)
    }

    private fun hideFiltersLayout() {
        searchBinding.filters.filters.inVisible()
    }

    private fun showFiltersLayout() {
        searchBinding.filters.filters.visible()
    }


    private fun hideRecentSearchesBar() {
        searchBinding.recentQueriesBar.gone()
    }

    private fun showRecentSearchesBar() {
        searchBinding.recentQueriesBar.visible()
    }

    private fun hideListViewAndRecyclerView() {
        hideRecentQueries()
        hideSuggestion()
    }

    private fun hideRecentQueries() {
        searchBinding.listViewRecentQueries.gone()
    }

    private fun showRecentQueries() {
        searchBinding.listViewRecentQueries.visible()
    }

    private fun showSuggestion() {
        searchBinding.recyclerViewSuggestion.visible()
    }

    private fun hideSuggestion() {
        searchBinding.recyclerViewSuggestion.inVisible()
    }

    protected fun showSuggestionViewAndHideRecentSearches() {
        showSuggestion()
        hideRecentQueries()
        hideRecentSearchesBar()
    }

    protected fun hideSuggestionViewAndShowRecentSearches() {
        showRecentSearchesBar()
        showRecentQueries()
        hideSuggestion()
    }


    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    protected fun isRecentTabSelected() = searchBinding.tabs.getTabAt(0)?.isSelected!!

    protected abstract fun setBindingVariables()
    protected abstract fun setSuggestionsQuery(newText: String?)
    protected abstract fun deleteAllRecentQueries()
    protected abstract fun observeAndSetRecentQueries()
    protected abstract fun setRecyclerViewAdapter()
    protected abstract fun setSearchViewHint()
    protected abstract fun observeSuggestions()
    protected abstract fun navigateFromSearchFragmentToSearchFragmentResultFilter(bundle: Bundle)
    protected abstract fun navigateFromSearchFragmentToSearchFragmentResult(query: String)
    protected abstract fun navigateFromSearchFragmentToListItemsFragment()
    protected abstract fun setFilterTabName(tab: TabLayout.Tab?)

}