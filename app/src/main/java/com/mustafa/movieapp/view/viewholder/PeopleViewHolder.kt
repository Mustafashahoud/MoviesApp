
package com.mustafa.movieapp.view.viewholder

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import com.mustafa.movieapp.api.Api
import com.mustafa.movieapp.models.entity.Person
import kotlinx.android.synthetic.main.item_person.view.*

class PeopleViewHolder(
  val view: View,
  private val delegate: Delegate
) : BaseViewHolder(view) {

  interface Delegate {
    fun onItemClick(person: Person)
  }

  private lateinit var person: Person

  @Throws(Exception::class)
  override fun bindData(data: Any) {
    if (data is Person) {
      person = data
      drawItem()
    }
  }

  private fun drawItem() {
    itemView.run {
      item_person_name.text = person.name
      person.profile_path?.let {
        Glide.with(context)
          .load(Api.getPosterPath(it))
          .apply(RequestOptions().circleCrop())
          .into(item_person_profile)
      }
    }
  }

  override fun onClick(p0: View?) {
    delegate.onItemClick(person)
  }

  override fun onLongClick(p0: View?) = false
}
