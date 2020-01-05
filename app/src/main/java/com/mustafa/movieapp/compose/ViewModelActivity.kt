
package com.mustafa.movieapp.compose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import javax.inject.Inject

@SuppressLint("Registered")
open class ViewModelActivity : AppCompatActivity() {

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
  }

  protected inline fun <reified VM : ViewModel>
    viewModel(): Lazy<VM> = viewModels { viewModelFactory }

  protected inline fun <reified T : ViewDataBinding> binding(resId: Int): Lazy<T> =
    lazy { DataBindingUtil.setContentView<T>(this, resId) }
}
