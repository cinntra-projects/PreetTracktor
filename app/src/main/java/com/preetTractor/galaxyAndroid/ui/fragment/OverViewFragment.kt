package com.preetTractor.galaxyAndroid.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.preetTractor.galaxyAndroid.databinding.FragmentOverViewBinding
import com.preetTractor.galaxyAndroid.ui.viewpager.ViewPagerAdapter


class OverViewFragment : Fragment() {
    lateinit var binding: FragmentOverViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOverViewBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "OverViewFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTabs()
    }

    private fun setUpTabs() {
        // Set up the ViewPager with the adapter
        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        // Link TabLayout and ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "OverView" // Customize tab titles
                1 -> tab.text = "Category"
                2 -> tab.text = "Item"
                3 -> tab.text = "Ledger"
            }
        }.attach()
    }
}