package com.mustafa.movieapp.view.adapter.filterSelectableAdapter


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mustafa.movieapp.R
import com.mustafa.movieapp.databinding.FilterButtonItemBinding
import com.mustafa.movieapp.databinding.ItemMovieBinding
import com.mustafa.movieapp.models.entity.Movie
import kotlinx.android.synthetic.main.filter_button_item.view.*


class FilterMultiSelectableAdapter (
    private var items: List<SelectableItem>,
    private val context: Context?,
    private val dataBindingComponent: DataBindingComponent,
    private val filterButtonSelectedOnClickCallback: ((String) -> Unit)?,
    private val filterButtonUnSelectedOnClickCallback: ((String) -> Unit)?,
    val adapterName: String):
    RecyclerView.Adapter<FilterMultiSelectableAdapter.SelectableViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_button_item, parent, false)

        val binding = DataBindingUtil.inflate<FilterButtonItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.filter_button_item,
            parent,
            false,
            dataBindingComponent
        )
//        binding.root.setOnClickListener {
//            binding.buttonTitle?.let{
//                filterButtonOnClickCallback?.invoke(it)
//            }
//        }
        return SelectableViewHolder(view)
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: SelectableViewHolder, position: Int) {
        val selectableItem = items[position]
        holder.button.text = selectableItem.title

        holder.button.setBackgroundColor(
            if (selectableItem.isSelected)
                getSelectedButtonColor(context)
            else getUnSelectedButtonColor(context)
        )
        holder.button.setTextColor(
            if (selectableItem.isSelected)
                getSelectedButtonTextColor(context)
            else getUnSelectedButtonTextColor(context)
        )
        holder.button.setOnClickListener {
            // if the item not selected that means we are selecting
            if (!selectableItem.isSelected) {
                filterButtonSelectedOnClickCallback?.invoke(holder.button.text as String)
            } else { // the item is selected that means we are un selecting
                filterButtonUnSelectedOnClickCallback?.invoke(holder.button.text as String)
            }

            selectableItem.isSelected = !selectableItem.isSelected
            holder.button.setBackgroundColor(
                if (selectableItem.isSelected)
                    getSelectedButtonColor(context)
                else getUnSelectedButtonColor(context)
            )
            holder.button.setTextColor(
                if(selectableItem.isSelected)
                    getSelectedButtonTextColor(context)
                else getUnSelectedButtonTextColor(context)
            )
        }
    }
    class SelectableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var button: Button = view.button_filter
    }

    fun clearSelection() {
        for (item in items){
            item.isSelected = false
        }
        notifyDataSetChanged()
    }

    private fun getSelectedButtonColor(context: Context?) = context?.resources?.getColor(R.color.colorAccent, context.theme)!!
    private fun getUnSelectedButtonColor(context: Context?) = context?.resources?.getColor(R.color.colorPrimaryDark, context.theme)!!
    private fun getSelectedButtonTextColor(context: Context?) = context?.resources?.getColor(R.color.colorPrimaryDark, context.theme)!!
    private fun getUnSelectedButtonTextColor(context: Context?) = context?.resources?.getColor(R.color.white, context.theme)!!


}