package com.preetTractor.galaxyAndroid.ui.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.data.LeadValue
import com.preetTractor.galaxyAndroid.databinding.CustomBottomSheetBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.AddBPCustomer

class LeadCreateUpdateBottomSheet(
    private val leadData: LeadValue,
    private val onOptionClick: (LeadValue) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: CustomBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = CustomBottomSheetBinding.inflate(
            inflater, container, false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        binding.update.setOnClickListener {
            onOptionClick(leadData)
            dismiss()
            /* Bundle bundle = new Bundle();
                        bundle.putParcelable(Globals.Lead_Data,lv);
                        LeadFollowUpFragment chatterFragment = new LeadFollowUpFragment();
                        chatterFragment.setArguments(bundle);
                        FragmentTransaction chattransaction =  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                        chattransaction.add(R.id.customer_lead, chatterFragment).addToBackStack(null);
                        chattransaction.commit();*/

        }
        if (leadData.status.equals("Qualified", ignoreCase = true)) {
            binding.createBp.visibility = View.VISIBLE
        } else {
            binding.createBp.visibility = View.GONE
        }
        binding.createBp.setOnClickListener {
            Prefs.putString(Globals.AddBp, "Lead")
            val intent: Intent = Intent(context, AddBPCustomer::class.java)
            intent.putExtra(Globals.AddBp, leadData)
            requireContext().startActivity(intent)
            dismiss()
        }
        binding.btnCancelDialog.setOnClickListener {
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}