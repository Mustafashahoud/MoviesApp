
package com.mustafa.movieapp.compose

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

open class ViewModelFragment : Fragment() {

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  protected inline fun <reified VM : ViewModel>
    viewModel(): Lazy<VM> = viewModels { viewModelFactory }

  protected inline fun <reified T : ViewDataBinding> binding(
    inflater: LayoutInflater,
    resId: Int,
    container: ViewGroup?
  ): T = DataBindingUtil.inflate<T>(inflater, resId, container, false)
}
