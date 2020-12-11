package com.mustafa.movieguideapp.view.ui.common

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * A generic RecyclerView adapter that uses Data Binding.
 *
 * @param <T> Type of the items in the list
 * @param <V> The type of the ViewDataBinding
</V></T> */
@Suppress("unused")
abstract class RecyclerViewBase<T, V : ViewDataBinding> :
    RecyclerView.Adapter<DataBoundViewHolder<V>>(){

    private val itemList = arrayListOf<T>()

    fun submitList(newList: List<T>?) {
        newList?.let{itemList.addAll(newList)}
        notifyDataSetChanged()
    }

    fun clearList(){
        itemList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<V> {
        val binding = createBinding(parent)
        return DataBoundViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup): V

    override fun onBindViewHolder(holder: DataBoundViewHolder<V>, position: Int) {
        bind(holder.binding, itemList[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = itemList.size

    protected abstract fun bind(binding: V, item: T)
}