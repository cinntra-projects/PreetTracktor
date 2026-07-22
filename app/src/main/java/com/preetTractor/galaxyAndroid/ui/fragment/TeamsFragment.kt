package com.preetTractor.galaxyAndroid.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.preetTractor.galaxyAndroid.databinding.FragmentTeamsBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.ui.activity.UserScreenActivity

class TeamsFragment : Fragment() {

    private var _binding: FragmentTeamsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "TeamsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTeamsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            linearTeamStatus.setOnClickListener {
                openUserScreen(
                    Constant.WHERE_INTENT_VALUE_STATUS,
                    "Team Status"
                )
            }

            linearLeaveRequest.setOnClickListener {
                openUserScreen(
                    Constant.WHERE_INTENT_VALUE_LEAVE,
                    "Leave Request"
                )
            }

            linearExpenseRequest.setOnClickListener {
                openUserScreen(
                    Constant.WHERE_INTENT_VALUE_EXPENSE,
                    "Expense Request"
                )
            }

            linearAttendance.setOnClickListener {
                openUserScreen(
                    Constant.WHERE_INTENT_VALUE_ATTENDANCE,
                    "Attendance"
                )
            }

            linearBeatPlan.setOnClickListener {
                openUserScreen(
                    Constant.WHERE_INTENT_VALUE_BEAT_PLAN,
                    "Beat Plan"
                )
            }
        }
    }

    private fun openUserScreen(
        whereIntent: String,
        headingTitle: String
    ) {

        startActivity(
            Intent(requireContext(), UserScreenActivity::class.java).apply {

                putExtra(Constant.WHERE_INTENT, whereIntent)

                putExtra(Constant.HEADING_TITLE, headingTitle)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}