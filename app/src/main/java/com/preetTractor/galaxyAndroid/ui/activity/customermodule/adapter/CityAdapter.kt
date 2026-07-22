package com.preetTractor.galaxyAndroid.ui.activity.customermodule.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.model.CityData

class CityAdapter(
    context: Context,
    private val resourceId: Int,
    items: ArrayList<CityData>
) : ArrayAdapter<CityData>(context, resourceId, items) {

    private val inflater = (context as Activity).layoutInflater

    private val originalList = ArrayList(items)
    private var filteredList = ArrayList(items)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: inflater.inflate(resourceId, parent, false)

        val city = getItem(position)

        view.findViewById<TextView>(R.id.text_view).text = city.CityName

        return view
    }

    override fun getCount(): Int {
        return filteredList.size
    }

    override fun getItem(position: Int): CityData {
        return filteredList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val results = FilterResults()

                if (constraint.isNullOrBlank()) {

                    results.values = originalList
                    results.count = originalList.size

                } else {

                    val search = constraint.toString()
                        .trim()
                        .lowercase()

                    val filtered = ArrayList<CityData>()

                    for (item in originalList) {

                        if (item.CityName
                                .lowercase()
                                .contains(search) == true
                        ) {
                            filtered.add(item)
                        }
                    }

                    results.values = filtered
                    results.count = filtered.size
                }

                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?
            ) {

                filteredList =
                    results?.values as? ArrayList<CityData> ?: arrayListOf()

                clear()
                addAll(filteredList)
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any): CharSequence {
                return (resultValue as CityData).CityName
            }
        }
    }
}