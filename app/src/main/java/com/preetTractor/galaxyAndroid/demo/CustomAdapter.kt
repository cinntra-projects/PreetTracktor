package com.preetTractor.galaxyAndroid.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.preetTractor.galaxyAndroid.R

class CustomAdapter(private val context: Context, private val itemList: ArrayList<GroupInfo>) : BaseExpandableListAdapter() {

    private var filteredItemList: List<GroupInfo> = itemList.toMutableList()

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val productList = itemList[groupPosition].productList
        return productList[childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        val detailInfo = getChild(groupPosition, childPosition) as ChildInfo
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.child_items, null)
        }

        val sequence = view?.findViewById<TextView>(R.id.sequence)
        sequence?.text = "${detailInfo.sequence.trim()}. "
        val childItem = view?.findViewById<TextView>(R.id.childItem)
        childItem?.text = detailInfo.name.trim()

        return view!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val productList = filteredItemList[groupPosition].productList
        return productList.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return filteredItemList[groupPosition]
    }

    override fun getGroupCount(): Int {
        return filteredItemList.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        val headerInfo = getGroup(groupPosition) as GroupInfo
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.group_items, null)
        }

        val heading = view?.findViewById<TextView>(R.id.heading)
        heading?.text = headerInfo.name?.trim()

        return view!!
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

   /* fun filterList(query: String) {
        filteredItemList = if (query.isEmpty()) {
            itemList
        } else {
            itemList.filter {
                it.SalesEmployeeName.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }*/


}
