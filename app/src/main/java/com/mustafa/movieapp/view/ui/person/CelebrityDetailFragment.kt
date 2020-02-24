
package com.mustafa.movieapp.view.ui.person

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.mustafa.movieapp.R
import com.mustafa.movieapp.databinding.FragmentCelebrityDetailBinding
import com.mustafa.movieapp.di.Injectable
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.testing.OpenForTesting
import com.mustafa.movieapp.utils.autoCleared
import kotlinx.android.synthetic.main.toolbar_detail.*
import javax.inject.Inject

@OpenForTesting
class CelebrityDetailFragment : Fragment(), Injectable {


  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private val vm by viewModels<PersonDetailViewModel> { viewModelFactory }

  var binding by autoCleared<FragmentCelebrityDetailBinding>()

  override fun onCreateView(
          inflater: LayoutInflater, container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_celebrity_detail,
            container,
            false
    )

    return binding.root
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    vm.postPersonId(getPersonFromIntent().id)
    with(binding) {
      lifecycleOwner = this@CelebrityDetailFragment
      viewModel = vm
      person = getPersonFromIntent()
    }
    initializeUI()
  }



  private fun initializeUI() {
    toolbar_back_arrow.setOnClickListener { activity?.onBackPressed() }
    toolbar_title.text = getPersonFromIntent().name
  }




  private fun getPersonFromIntent(): Person {
    val params =
        CelebrityDetailFragmentArgs.fromBundle(
            requireArguments()
        )
    return params.person
  }

  companion object {
    const val personId = "person"
    const val intent_requestCode = 1000

//    fun startActivity(activity: Activity?, person: Person, view: View) {
//      if (activity != null) {
//        if (checkIsMaterialVersion()) {
//          val intent = Intent(activity, CelebrityDetailFragment::class.java)
//          ViewCompat.getTransitionName(view)?.let {
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, it)
//            intent.putExtra(personId, person)
//            activity.startActivityForResult(intent, intent_requestCode, options.toBundle())
//          }
//        } else {
////          activity.startActivity<CelebrityDetailFragment>(personId to person)
//        }
//      }
//    }
  }
}
