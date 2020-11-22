package com.mustafa.movieguideapp.view.ui.tv.tvlist

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
import com.mustafa.movieguideapp.databinding.FragmentTvsBinding
import com.mustafa.movieguideapp.di.Injectable
import com.mustafa.movieguideapp.utils.autoCleared
import com.mustafa.movieguideapp.view.adapter.TvsAdapter
import kotlinx.android.synthetic.main.toolbar_search.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TvListFragment : Fragment(R.layout.fragment_tvs), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private val viewModel by viewModels<TvListViewModel> { viewModelFactory }
    private var binding by autoCleared<FragmentTvsBinding>()
    private var adapter by autoCleared<TvsAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentTvsBinding.bind(view)

        initializeUI()

        subscribers()

    }

    private fun initializeUI() {
        intiToolbar(getString(R.string.fragment_tvs))
        val rxAdapter = TvsAdapter(dataBindingComponent) {
            findNavController().navigate(
                TvListFragmentDirections.actionTvsToTvDetail(
                    it
                )
            )
        }
        adapter = rxAdapter
        binding.recyclerViewListTvs.adapter = adapter
        binding.recyclerViewListTvs.layoutManager = GridLayoutManager(context, 3)

        search_icon.setOnClickListener {
            findNavController().navigate(
                TvListFragmentDirections.actionTvsFragmentToTvSearchFragment()
            )
        }
    }

    private fun subscribers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tvsStream.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun intiToolbar(title: String) {
        toolbar_title.text = title
    }

}
