package com.mustafa.movieguideapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.button.MaterialButton
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.FilterButtonItemBinding
import com.mustafa.movieguideapp.models.SelectableItem

class FilterMultiSelectableAdapter(
    private var items: List<SelectableItem>,
    private val context: Context?,
    private val filterButtonSelectedOnClickCallback: ((String) -> Unit)?,
    private val filterButtonUnSelectedOnClickCallback: ((String) -> Unit)?,
    val adapterName: String
) :
    Adapter<FilterMultiSelectableAdapter.SelectableViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableViewHolder {
        val binding: FilterButtonItemBinding = FilterButtonItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectableViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SelectableViewHolder, position: Int) {
        val selectableItem = items[position]
        holder.button.text = selectableItem.title

        // init colors
        setButtonAndTextColors(holder.button, selectableItem, context)

        holder.button.setOnClickListener {
            // if the item not selected that means we are selecting
            if (!selectableItem.isSelected) {
                filterButtonSelectedOnClickCallback?.invoke(holder.button.text as String)
            } else { // the item is selected that means we are un selecting
                filterButtonUnSelectedOnClickCallback?.invoke(holder.button.text as String)
            }
            selectableItem.isSelected = !selectableItem.isSelected

            // Render colors after selecting
            setButtonAndTextColors(holder.button, selectableItem, context)
        }
    }

    private fun setButtonAndTextColors(
        button: MaterialButton,
        selectableItem: SelectableItem,
        context: Context?
    ) {
        button.setBackgroundColor(getProperButtonColor(selectableItem, context))
        button.setTextColor(getProperTextColor(selectableItem, context))
    }

    private fun getProperButtonColor(selectableItem: SelectableItem, context: Context?): Int {
        return if (selectableItem.isSelected)
            getSelectedButtonColor(context)
        else getUnSelectedButtonColor(context)
    }

    private fun getProperTextColor(selectableItem: SelectableItem, context: Context?): Int {
        return if (selectableItem.isSelected)
            getSelectedButtonTextColor(context)
        else getUnSelectedButtonTextColor(context)
    }

    class SelectableViewHolder(binding: FilterButtonItemBinding) : ViewHolder(binding.root) {
        val button = binding.buttonFilter
    }

    fun clearSelection() {
        items.forEach {
            it.isSelected = false
        }
        notifyDataSetChanged()
    }


    private fun getSelectedButtonColor(context: Context?) =
        context?.resources?.getColor(R.color.colorAccent, context.theme)!!

    private fun getUnSelectedButtonColor(context: Context?) =
        context?.resources?.getColor(R.color.colorPrimaryDark, context.theme)!!

    private fun getSelectedButtonTextColor(context: Context?) =
        context?.resources?.getColor(R.color.colorPrimaryDark, context.theme)!!

    private fun getUnSelectedButtonTextColor(context: Context?) =
        context?.resources?.getColor(R.color.white, context.theme)!!
}