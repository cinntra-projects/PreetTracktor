package com.preetTractor.galaxyAndroid.ui.bottomsheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.preetTractor.galaxyAndroid.data.LeadValue
import com.preetTractor.galaxyAndroid.databinding.BottomSheetBinding
import androidx.core.net.toUri

class LeadOptionsBottomSheet(
    private val leadData: LeadValue
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        binding.callView.setOnClickListener {

            val phone = leadData.phoneNumber

            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:$phone")
            )

            startActivity(intent)
        }
        binding.messageView.setOnClickListener {

            val phone = leadData.phoneNumber

            val intent = Intent(
                Intent.ACTION_SENDTO,
                "smsto:$phone".toUri()
            )

            startActivity(intent)
        }
        binding.emailView.setOnClickListener {

            val email = leadData.email

            val intent = Intent(Intent.ACTION_SENDTO).apply {

                data = "mailto:".toUri()

                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(email)
                )

                putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Lead Discussion"
                )
            }

            startActivity(intent)
        }
        binding.whatsappView.setOnClickListener {

            val phone = leadData.phoneNumber

            val formattedNumber = "91$phone"

            val uri = "https://wa.me/$formattedNumber".toUri()

            val intent = Intent(
                Intent.ACTION_VIEW,
                uri
            )

            try {

                startActivity(intent)

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    "WhatsApp not installed",
                    Toast.LENGTH_SHORT
                ).show()
            }
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