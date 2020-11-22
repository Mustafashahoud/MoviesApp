package com.mustafa.movieguideapp.view.ui.person.celebrities

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentDataBindingComponent
import com.mustafa.movieguideapp.databinding.FragmentCelebritiesBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.PeopleAdapter
import kotlinx.android.synthetic.main.toolbar_search.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class CelebritiesListFragment : Fragment(R.layout.fragment_celebrities), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private val viewModel by viewModels<CelebritiesListViewModel> { viewModelFactory }
    private var binding by autoCleared<FragmentCelebritiesBinding>()
    private var adapter by autoCleared<PeopleAdapter>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCelebritiesBinding.bind(view)

        initializeUI()

        subscribers()
    }


    private fun initializeUI() {
        intiToolbar(getString(R.string.fragment_celebrities))
        adapter = PeopleAdapter(dataBindingComponent) {
            findNavController().navigate(
                CelebritiesListFragmentDirections.actionCelebritiesToCelebrity(it)
            )
        }
        binding.recyclerViewListCelebrities.adapter = adapter
        binding.recyclerViewListCelebrities.layoutManager = GridLayoutManager(context, 3)

        search_icon.setOnClickListener {
            findNavController().navigate(
                CelebritiesListFragmentDirections.actionCelebritiesToSearchCelebritiesFragment()
            )
        }
    }


    private fun subscribers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.peopleStream.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun intiToolbar(title: String) {
        toolbar_title.text = title
    }
}
