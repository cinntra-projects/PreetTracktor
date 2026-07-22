package com.preetTractor.galaxyAndroid.ui.activity.otpAndMpinScreen

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.preetTractor.galaxyAndroid.databinding.ActivityUpdateMpinactivityBinding
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.google.android.material.textfield.TextInputEditText

class UpdateMPINActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateMpinactivityBinding
//    lateinit var sessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateMpinactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        sessionManagement = SessionManagement(applicationContext)

        binding.submitBtn.setOnClickListener {
            if (PrefsByShubh.getMPINValue() != binding.edtOldMPin.text.toString()) {
                Toast.makeText(
                    this@UpdateMPINActivity,
                    "Old MPIN does not Match ",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!validateEditTexts(binding.mpinEditText, binding.confirmMpinEditText)) {
                Toast.makeText(
                    this@UpdateMPINActivity,
                    "Please enter MPIN correctly",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
//                Prefs.putString(Global.MPIN_VALUE, binding.mpinEditText.text.toString().trim())
                PrefsByShubh.setMPINValue(binding.mpinEditText.text.toString())
                finish()
                Toast.makeText(this@UpdateMPINActivity, "Changed Successfully", Toast.LENGTH_SHORT)
                    .show()

                /* var intent : Intent = Intent(this@UpdateMPINActivity, VerifyMPINOtpActivity::class.java)
                    startActivity(intent)*/

            }

        }


        binding.ivBackPress.setOnClickListener {
            finish()
        }


    }


    private fun validateEditTexts(
        editText1: TextInputEditText,
        editText2: TextInputEditText
    ): Boolean {
        val text1 = editText1.text.toString().trim { it <= ' ' }
        val text2 = editText2.text.toString().trim { it <= ' ' }
        return if (TextUtils.isEmpty(text1) || TextUtils.isEmpty(text2)) {
            // If any of the EditTexts is empty, return false
            false
        } else text1 == text2

        // If both EditTexts are not empty, compare their contents
    }


}