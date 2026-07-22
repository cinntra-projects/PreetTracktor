package com.preetTractor.galaxyAndroid.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.preetTractor.galaxyAndroid.data.beatplan.CustomItem

class CustomAdapter(
    context: Context,
    private val items: List<CustomItem>
) : ArrayAdapter<CustomItem>(context, android.R.layout.simple_dropdown_item_1line, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = items[position].name // Display the name property in the dropdown
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val filteredItems = if (constraint.isNullOrEmpty()) {
                    items
                } else {
                    items.filter { it.name.contains(constraint, ignoreCase = true) }
                }
                results.values = filteredItems
                results.count = filteredItems.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                if (results?.values != null) {
                    @Suppress("UNCHECKED_CAST")
                    addAll(results.values as List<CustomItem>)
                }
                notifyDataSetChanged()
            }
        }
    }
}
