package com.preetTractor.galaxyAndroid.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.preetTractor.galaxyAndroid.ui.fragment.overview.CategoryCustomerFragment
import com.preetTractor.galaxyAndroid.ui.fragment.overview.ItemCustomerFragment
import com.preetTractor.galaxyAndroid.ui.fragment.overview.ItemCustomerLedgerFragment
import com.preetTractor.galaxyAndroid.ui.fragment.overview.OverViewInnerFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(
        OverViewInnerFragment(),   // Replace with your actual fragment classes
        CategoryCustomerFragment(),  // Replace with your actual fragment classes
        ItemCustomerFragment()   , // Replace with your actual fragment classes
        ItemCustomerLedgerFragment()    // Replace with your actual fragment classes
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
