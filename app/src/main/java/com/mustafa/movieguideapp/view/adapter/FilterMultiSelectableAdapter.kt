package com.mustafa.movieguideapp.view.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
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
//        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.filter_button_item, parent, false)
        val binding: FilterButtonItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.filter_button_item,
            parent,
            false
        )

        binding.root.setOnClickListener {

        }



        return SelectableViewHolder(binding)
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
                if (selectableItem.isSelected)
                    getSelectedButtonTextColor(context)
                else getUnSelectedButtonTextColor(context)
            )
        }
    }

    class SelectableViewHolder(private val binidng: FilterButtonItemBinding) :
        ViewHolder(binidng.root) {
        var button: Button = binidng.buttonFilter
    }

    fun clearSelection() {
        for (item in items) {
            item.isSelected = false
        }
        notifyDataSetChanged()
    }

//    fun selectItems(itemsToSelect: List<String>) {
//        for (itemToSelect in itemsToSelect) {
//            for (item in items) {
//                if (item.title == itemToSelect) {
//                    item.isSelected = true
//                }
//            }
//        }
//        notifyDataSetChanged()
//    }

    private fun getSelectedButtonColor(context: Context?) =
        context?.resources?.getColor(R.color.colorAccent, context.theme)!!

    private fun getUnSelectedButtonColor(context: Context?) =
        context?.resources?.getColor(R.color.colorPrimaryDark, context.theme)!!

    private fun getSelectedButtonTextColor(context: Context?) =
        context?.resources?.getColor(R.color.colorPrimaryDark, context.theme)!!

    private fun getUnSelectedButtonTextColor(context: Context?) =
        context?.resources?.getColor(R.color.white, context.theme)!!
}