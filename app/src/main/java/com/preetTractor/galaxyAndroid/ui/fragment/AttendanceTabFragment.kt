package com.preetTractor.galaxyAndroid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.FragmentAttendanceTabBinding
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh

// TODO: Rename parameter arguments, choose names that match


/**
 * A simple [Fragment] subclass.
 * Use the [AttendanceTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AttendanceTabFragment : Fragment() {
    lateinit var binding: FragmentAttendanceTabBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAttendanceTabBinding.inflate(layoutInflater)


        binding.tabExpences.visibility = if (PrefsByShubh.getString("role", "") == "Beauty Advisor") View.GONE else View.VISIBLE


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.nestedNavHost.getFragment<NavHostFragment>().navController.apply {
            addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.attendanceFragment -> {
                        binding.tabAttendence.isChecked = true
                        binding.tabLeave.isChecked = false
                        binding.tabExpences.isChecked = false
                    }

                    R.id.leaveFragment -> {
                        binding.tabAttendence.isChecked = false
                        binding.tabLeave.isChecked = true
                        binding.tabExpences.isChecked = false
                    }

                    R.id.expensesFragment -> {
                        binding.tabAttendence.isChecked = false
                        binding.tabLeave.isChecked = false
                        binding.tabExpences.isChecked = true
                    }
                }
            }
        }
        binding.tabsStrip.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = requireActivity().findViewById(checkedId)
            when (checkedId) {
                R.id.tabAttendence -> {
                    binding.nestedNavHost.getFragment<NavHostFragment>().navController.navigate(R.id.attendanceFragment)
                }

                R.id.tabLeave -> {
                    binding.nestedNavHost.getFragment<NavHostFragment>().navController.navigate(R.id.leaveFragment)
                }

                R.id.tabExpences -> {
                    binding.nestedNavHost.getFragment<NavHostFragment>().navController.navigate(R.id.expensesFragment)
                }
            }

        }
    }

}