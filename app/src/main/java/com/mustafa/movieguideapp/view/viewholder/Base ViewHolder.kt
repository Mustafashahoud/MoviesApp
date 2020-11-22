package com.mustafa.movieguideapp.view.viewholder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 * https://github.com/skydoves/TheMovies
 */
/** BaseViewHolder is an abstract class for structuring the base view holder class.  */
@Suppress("unused", "LeakingThis")
abstract class BaseViewHolder(
    private val view: View
) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

    init {
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
    }

    abstract fun bindData(data: Any)

    /** gets the view of the [RecyclerView.ViewHolder]. */
    fun view(): View {
        return view
    }

    /** gets the context. */
    fun context(): Context {
        return view.context
    }
}