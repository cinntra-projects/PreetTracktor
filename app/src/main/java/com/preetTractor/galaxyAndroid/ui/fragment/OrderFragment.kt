package com.preetTractor.galaxyAndroid.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.FragmentOrderBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.fragment.PendingOrderListFragment
import com.preetTractor.galaxyAndroid.ui.orderUi.fragment.PlaceOrderFragment

class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    lateinit var viewModel: MainViewModel
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(savedInstanceState)
        clickListener()
    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewModel = (activity as CustomerDetailActivity).viewModel
        binding.toggleButton.check(binding.btnPlace.id)
        updateButtonStyles(binding.toggleButton)

        if (savedInstanceState == null) {
            Globals.loadFragmentWithFrameLayout(
                PlaceOrderFragment(),
                binding.fragmentContainerOrderFragment.id,
                childFragmentManager
            )
        }
    }

    private fun clickListener() {
        binding.apply {
            toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
                updateButtonStyles(group)
               if (isChecked) {
                    when (checkedId) {
                        btnPlace.id -> {
                            Log.d("FragmentLoad", "Loading PlaceOrderFragment")
                            Globals.loadFragmentWithFrameLayout(
                                PlaceOrderFragment(),
                                fragmentContainerOrderFragment.id,
                                childFragmentManager
                            )
                        }
                        btnDispatch.id -> {
                            Log.d("FragmentLoad", "Loading DispatchedOrderFragment")
                            /*Globals.loadFragmentWithFrameLayout(
                                DispatchedOrderFragment(),
                                fragmentContainerOrderFragment.id,
                                childFragmentManager
                            )*/

                            Globals.loadFragmentWithFrameLayout(
                                DispatchOrderListFragment(),
                                fragmentContainerOrderFragment.id,
                                childFragmentManager
                            )

                        }
                        btnPending.id -> {
                            Log.d("FragmentLoad", "Loading PendingOrderListFragment")
                            /*Globals.loadFragmentWithFrameLayout(
                                PendingOrderFragment(),
                                fragmentContainerOrderFragment.id,
                                childFragmentManager
                            )*/
                            Globals.loadFragmentWithFrameLayout(
                                PendingOrderListFragment(),
                                fragmentContainerOrderFragment.id,
                                childFragmentManager
                            )
                        }
                    }
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.containerFragment, fragment)
        transaction.commit() // Commit the transaction without adding to the back stack
    }

    private fun updateButtonStyles(group: MaterialButtonToggleGroup) {
        for (i in 0 until group.childCount) {
            val button = group.getChildAt(i) as MaterialButton
            if (button.id == group.checkedButtonId) {
                setStyleChecked(
                    button,
                    R.style.Widget_MaterialComponents_Button_Filled
                ) // Filled style
            } else {
                setStyle(
                    button,
                    R.style.Widget_MaterialComponents_Button_Outlined
                ) // Outlined style
            }
        }
    }

    private fun setStyleChecked(button: MaterialButton, @StyleRes styleResId: Int) {
        try {
            button.setBackgroundColor(requireActivity().resources.getColor(R.color.colorPrimary))
            button.setTextColor(requireActivity().resources.getColor(R.color.white))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ResourceType")
    private fun setStyle(button: MaterialButton, @StyleRes styleResId: Int) {
        val attrs = intArrayOf(android.R.attr.background, android.R.attr.textColor)
        try {
            // Apply the new style
            button.setTextAppearance(styleResId)
            // Obtain styled attributes
            val a = button.context.obtainStyledAttributes(styleResId, attrs)
            val backgroundColor = a.getColor(0, 0) // Get background color
            //   val textColor = a.getColor(1, 0) // Get text color
            button.setBackgroundColor(backgroundColor)
            //   button.setTextColor(textColor)
            a.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
    }
}