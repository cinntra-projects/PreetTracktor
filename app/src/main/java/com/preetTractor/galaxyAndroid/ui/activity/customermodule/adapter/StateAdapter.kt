package com.preetTractor.galaxyAndroid.ui.activity.customermodule.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.StateData

class StateAdapter(
    context: Context,
    resourceId: Int,
    items: ArrayList<StateData>
) : ArrayAdapter<StateData>(context, resourceId, items) {

    private val resourceId: Int = resourceId
    private val context: Context = context

    private val originalList = ArrayList(items)
    private var filteredList = ArrayList(items)

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val view = convertView ?: (context as Activity)
            .layoutInflater.inflate(resourceId, parent, false)

        val model = getItem(position)

        val name = view.findViewById<TextView>(R.id.text_view)
        name.text = model?.name

        return view
    }

    override fun getItem(position: Int): StateData? {
        return filteredList[position]
    }

    override fun getCount(): Int {
        return filteredList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(
                constraint: CharSequence?
            ): FilterResults {

                val results = FilterResults()

                if (constraint.isNullOrEmpty()) {

                    results.values = originalList
                    results.count = originalList.size

                } else {

                    val searchText = constraint.toString()
                        .lowercase()
                        .trim()

                    val filtered = ArrayList<StateData>()

                    for (item in originalList) {

                        if (item.name?.lowercase()
                                ?.contains(searchText) == true
                        ) {
                            filtered.add(item)
                        }
                    }

                    results.values = filtered
                    results.count = filtered.size
                }

                return results
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {

                filteredList =
                    results.values as ArrayList<StateData>

                clear()
                addAll(filteredList)
                notifyDataSetChanged()
            }
        }
    }
}