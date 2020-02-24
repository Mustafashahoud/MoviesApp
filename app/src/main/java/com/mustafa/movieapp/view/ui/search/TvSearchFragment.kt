package com.mustafa.movieapp.view.ui.search

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mustafa.movieapp.R
import com.mustafa.movieapp.binding.FragmentDataBindingComponent
import com.mustafa.movieapp.databinding.FragmentTvSearchBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.extension.gone
import com.mustafa.movieapp.extension.inVisible
import com.mustafa.movieapp.extension.visible
import com.mustafa.movieapp.utils.autoCleared
import com.mustafa.movieapp.view.adapter.TvSearchListAdapter
import com.mustafa.movieapp.view.adapter.filterSelectableAdapter.FilterMultiSelectableAdapter
import com.mustafa.movieapp.view.adapter.filterSelectableAdapter.SelectableItem
import com.mustafa.movieapp.view.ui.common.AppExecutors
import com.mustafa.movieapp.view.ui.common.RetryCallback
import kotlinx.android.synthetic.main.fragment_movies_search_filter.*
import kotlinx.android.synthetic.main.fragment_tv_search.*
import kotlinx.android.synthetic.main.fragment_tv_search.view.*
import kotlinx.android.synthetic.main.toolbar_search_iconfied.*
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TvSearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel by viewModels<TvSearchViewModel> { viewModelFactory }
    var dataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentTvSearchBinding>()
    var tvAdapter by autoCleared<TvSearchListAdapter>()

//    val columns =
//        arrayOf("_id", "title", "poster_path", "vote_average", "genre_ids", "release_date")

    companion object {
        const val RUNTIME = "RUNTIME"
        const val RATINGS = "RATINGS"
        const val KEYWORDS = "KEYWORDS"
        const val LANGUAGES = "LANGUAGES"
        const val YEARS = "YEARS"
        const val GENRES = "GENRES"
        const val COUNTRIES = "COUNTRIES"
    }

    private val ratings = listOf("+9", "+8", "+7", "+6", "+5", "+4")

    private val runtimes =
        listOf("1 hour or more", "2 hours or more", "3 hours or more", "4 hours or more")

    private val languages = listOf(
        "English", "French", "German", "Spanish", "Chinese",
        "Italian", "Russian", "Japanese"
    )

    private val genres = listOf(
        "Adventure",
        "Crime",
        "History",
        "Drama",
        "History",
        "Thriller",
        "Romance",
        "Comedy",
        "Family",
        "War",
        "Horror",
        "Western",
        "Science Fiction",
        "Fantasy",
        "Documentary",
        "Animation"
    )

    private val countries = listOf(
        "United State", "Canada", "Germany", "France", "United Kingdom",
        "Spain", "Italy", "India", "Japan"
    )

    private val keywords = listOf(
        "Anim", "Superhero", "Bank Robbery", "Based on Novel", "Based on Play",
        "Based on True Story", "Kidnapping", "Cult Film", "High School", "Time Travel", "Zombie"
    )

    private val years = listOf(
        "2020", "2019", "2018", "2017", "2016",
        "2015", "2014", "2013", "2012", "2011", "2010|Before"
    )

    private val hasAnyFilterBeenSelected = MutableLiveData<Boolean>()

    private val mapFilterTypeToSelectedFilters =
        HashMap<FilterMultiSelectableAdapter, ArrayList<String>>()

//    private lateinit var mSearchViewAdapter: SuggestionsAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tv_search,
            container,
            false
        )

        Timber.d("Hell..Yeahh...onCreateView()")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeUI()
        subscribers()

        Timber.d("Hell..Yeahh...onViewCreated()")
        with(binding) {
            searchResult = viewModel.searchTvListLiveData
            query = viewModel.queryTvLiveData
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
            lifecycleOwner = this@TvSearchFragment
        }
    }

    private fun initializeUI() {

        hideNavigationBottomView()

        initToolbar()

        initTabLayout()

        setSearchViewHint()

        setFilterButtons()

        initClearAndSeeResultBar()

        setupRecentQueries()


        // Adapter
        tvAdapter = TvSearchListAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            navController().navigate(
                TvSearchFragmentDirections.actionTvSearchFragmentToTvDetail(
                    it
                )
            )
        }

        binding.root.recyclerView_tv_suggestion.adapter =  tvAdapter

        binding.root.recyclerView_tv_suggestion.layoutManager = LinearLayoutManager(context)

    }

    private fun subscribers() {

        hasAnyFilterBeenSelected.observe(viewLifecycleOwner, Observer {
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
        })
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

//        val searchViewEditText =
//            search_view.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        arrow_back.setOnClickListener {
            search_view.clearFocus()
//            navController().navigate(TvSearchFragmentDirections.actionTvSearchFragmentToTvsFragment())
        }


        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                navController().navigate(TvSearchFragmentDirections.actionTvSearchFragmentToTvSearchFragmentResult(query!!))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                        viewModel.setTvSuggestionsQuery(newText!!)
                        viewModel.tvSuggestions.observe(viewLifecycleOwner, Observer {
                            if (!it.isNullOrEmpty()) {
                                showSuggestionViewAndHideRecentSearches()
                            }
                            tvAdapter.submitList(it)

                            if (newText.isEmpty()|| newText.isBlank()) {
                                hideSuggestionViewAndShowRecentSearches()
                                tvAdapter.submitList(null)
                                viewModel.tvSuggestions.removeObservers(viewLifecycleOwner)
                            }
                        })
                        return true
            }
        })
    }

    private fun showSuggestionViewAndHideRecentSearches() {
        binding.root.recyclerView_tv_suggestion.visible()
        hideRecentQueries()
        hideRecentSearchesBar()
    }

    private fun hideSuggestionViewAndShowRecentSearches() {
        showRecentSearchesBar()
        showRecentQueries()
        binding.root.recyclerView_tv_suggestion.inVisible()
    }

    private fun setSearchViewHint() {
            search_view.queryHint = "Search Series"
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    /**
     * dismiss Keyboard
     *
     * @param windowToken The token of the window that is making the request, as returned by View.getWindowToken().
     */
    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * dismiss Keyboard
     *
     * @param view The token of the window that is making the request, as returned by View.getWindowToken().
     */
    private fun showKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(view, 0)
    }


    private fun setupRecentQueries() {
        viewModel.getTvRecentQueries().observe(viewLifecycleOwner, Observer { it ->
            if (!it.isNullOrEmpty()) {
                val queries = it.map { it.query }
                setListViewOfRecentQueries(queries)
            }
        })

    }

    private fun setListViewOfRecentQueries(queries: List<String?>) {
        val arrayAdapter = ArrayAdapter<String>(requireContext() , R.layout.recent_query_item, queries)
        showRecentQueries()
        listView_tv_recent_queries.setHeaderDividersEnabled(true)
        listView_tv_recent_queries.setFooterDividersEnabled(true)
        listView_tv_recent_queries.adapter = arrayAdapter
        listView_tv_recent_queries.setOnItemClickListener { parent, _, position, _ ->
            val query = parent.getItemAtPosition(position) as String
            navController().navigate(
                TvSearchFragmentDirections.actionTvSearchFragmentToTvSearchFragmentResult(
                    query
                )
            )
        }

        clear_recent_queries.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext() , R.style.AlertDialogTheme)
            builder.setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.clear) { _, _ ->
                    viewModel.deleteAllTvRecentQueries()
                    arrayAdapter.clear()
                    hideRecentQueries()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }

            builder.create().show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initTabLayout() {

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

        val tabLayout = tabs[0] as ViewGroup

        val recentTab: View = tabLayout.getChildAt(0)
        recentTab.setOnClickListener {
            hideFiltersLayout()
            search_view.visible()
            voiceSearch.visible()
            filter_label.gone()
            if (search_view.query.isEmpty() || search_view.query.isBlank()) {
                showRecentSearchesBar()
                showRecentQueries()
            } else {
                hideRecentQueries()
                binding.root.recyclerView_tv_suggestion.visible()
            }

            showKeyboard(search_view)
        }
        val filterTab = tabLayout.getChildAt(1)
        filterTab.setOnClickListener {
            hideListViewAndRecyclerView()
            search_view.gone()
            voiceSearch.gone()
            filter_label.text = "Filters"
            filter_label.visible()
            showFiltersLayout()
            hideRecentSearchesBar()
            dismissKeyboard(search_view.windowToken)
        }
    }

    private fun hideNavigationBottomView() {
        activity?.find<BottomNavigationView>(R.id.bottom_navigation)?.gone()
    }

    private fun setFilterButtons() {

        val runTimeRecyclerView =
            activity?.findViewById<RecyclerView>(R.id.recycler_view_runtimes)
        setFilterAdapter(
            runTimeRecyclerView, runtimes,
            RUNTIME
        )

        val ratingRecyclerView = activity?.findViewById<RecyclerView>(R.id.recycler_view_ratings)
        setFilterAdapter(
            ratingRecyclerView, ratings,
            RATINGS
        )

        val genreAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_genres)
        setFilterAdapter(
            genreAdapter, genres,
            GENRES
        )

        val yearAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_years)
        setFilterAdapter(
            yearAdapter, years,
            YEARS
        )

        val keywordAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_keywords)
        setFilterAdapter(
            keywordAdapter, keywords,
            KEYWORDS
        )

        val languageAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_languages)
        setFilterAdapter(
            languageAdapter, languages,
            LANGUAGES
        )

        val countriesAdapter = activity?.findViewById<RecyclerView>(R.id.recycler_view_countries)
        setFilterAdapter(
            countriesAdapter, countries,
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
            val selectableItem = SelectableItem(item, false)
            selectableItemList.add(selectableItem)
        }
        val filterAdapter =
            FilterMultiSelectableAdapter(selectableItemList, context, dataBindingComponent, {
                filters.add(it)
                hasAnyFilterBeenSelected.value = true
            }, {
                if (filters.size > 0) filters.remove(it)
                hasAnyFilterBeenSelected.value = true
            },
                adapterName
            )
        mapFilterTypeToSelectedFilters[filterAdapter] = filters
        recyclerView?.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        recyclerView?.adapter = filterAdapter
        recyclerView?.setHasFixedSize(true)
    }


    private fun initClearAndSeeResultBar() {
        clear_filter.setOnClickListener {
            for (adapter in mapFilterTypeToSelectedFilters.keys) {
                adapter.clearSelection()
            }
            mapFilterTypeToSelectedFilters.map {
                it.value.clear()
            }
            hasAnyFilterBeenSelected.value = true
        }

        see_result.setOnClickListener {
            val bundle = bundleOf("key" to convertAdapterKeyMapToStringKeyMap())
            navController().navigate(
                R.id.action_tvSearchFragment_to_tvSearchFragmentResultFilter,
                bundle
            )
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
        listView_tv_recent_queries.inVisible()
        binding.root.recyclerView_tv_suggestion.inVisible()

    }

    private fun showListViewAndRecyclerView() {
        listView_tv_recent_queries.visible()
        binding.root.recyclerView_tv_suggestion.visible()
    }


    private fun hideRecentQueries() {
        listView_tv_recent_queries.inVisible()
    }

    private fun showRecentQueries() {
        listView_tv_recent_queries.visible()
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
     * @return
     */
    fun convertAdapterKeyMapToStringKeyMap(): Map<String, List<String>> {
        val mapStringAdapterNameToSelectedFilters = HashMap<String, List<String>>()
        mapFilterTypeToSelectedFilters.map { it ->
            when (it.key.adapterName) {
                RATINGS -> mapStringAdapterNameToSelectedFilters[RATINGS] = it.value.map {
                    it.replace("+", "")
                }
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




    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Hell..Yeahh...onDestroy()")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("Hell..Yeahh...onStop()")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Hell..Yeahh...onResume()")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("Hell..Yeahh...onPause()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("Hell..Yeahh...onDestroyView()")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.d("Hell..Yeahh...onDetach()")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("Hell..Yeahh...onStart()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Hell..Yeahh...onCreate()")
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        Timber.d("Hell..Yeahh...onAttachFragment()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("Hell..Yeahh...onAttach()")
    }

//    fun setSelectableItemBackground(view: View) {
//        val typedValue = TypedValue()
//
//        // I used getActivity() as if you were calling from a fragment.
//        // You just want to call getTheme() on the current activity, however you can get it
//        activity?.theme?.resolveAttribute(
//            android.R.attr.selectableItemBackground,
//            typedValue,
//            true
//        )
//
//        // it's probably a good idea to check if the color wasn't specified as a resource
//        if (typedValue.resourceId != 0) {
//            view.setBackgroundResource(typedValue.resourceId)
//        } else {
//            // this should work whether there was a resource id or not
//            view.setBackgroundColor(typedValue.data)
//        }
//
//    }


//    override fun onSuggestionSelect(position: Int): Boolean {
//        val cursor = search_view.suggestionsAdapter.getItem(position) as Cursor
//        val query = cursor.getString(1)
//        search_view.setQuery(query, false)
//        search_view.clearFocus()
//        return true
//    }
//
//    override fun onSuggestionClick(position: Int): Boolean {
//        val cursor = search_view.suggestionsAdapter.getItem(position) as Cursor
//        val query = cursor.getString(1)
//        search_view.setQuery(query, false)
//        search_view.clearFocus()
//        return true
//    }

//
//    private fun initSearchViewForSuggestions() {
//        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val searchableInfo = searchManager.getSearchableInfo(activity!!.componentName)
//        search_view.setSearchableInfo(searchableInfo)
//
//        mSearchViewAdapter = SuggestionsAdapter(
//            activity,
//            dataBindingComponent,
//            R.layout.suggestion_search_item,
//            null,
//            columns,
//            null,
//            -1000
//        )
//        search_view.suggestionsAdapter = mSearchViewAdapter
//
//    }
//
//
//    private fun convertToCursor(movies: List<Movie>): MatrixCursor? {
//
//        val cursor = MatrixCursor(columns)
//        var i = 0
//        for (movie in movies) {
//            val temp = ArrayList<String?>()
//
//            i = i + 1
//
//            temp.add(i.toString())
//
//            temp.add(movie.title)
//
//            if (movie.poster_path == null) {
//                temp.add("")
//            } else {
//                temp.add(movie.poster_path)
//            }
//
//            temp.add(movie.vote_average.toString())
//            temp.add(movie.genre_ids.toString())
//
//            if (movie.release_date == null) {
//                temp.add("")
//            } else {
//                temp.add(StringUtils.formatReleaseDate(movie.release_date))
//            }
//
//            cursor.addRow(temp)
//        }
//        return cursor
//    }


}