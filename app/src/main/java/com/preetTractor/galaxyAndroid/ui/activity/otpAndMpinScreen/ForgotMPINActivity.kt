package com.preetTractor.galaxyAndroid.ui.activity.otpAndMpinScreen

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.preetTractor.galaxyAndroid.databinding.ActivityForgotMpinactivityBinding
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.google.android.material.textfield.TextInputEditText

class ForgotMPINActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgotMpinactivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotMpinactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ivBackPress.setOnClickListener {
            finish()
        }

        binding.submitBtn.setOnClickListener {
            /* var intent = Intent(this@ForgotMPINActivity, VerifyMPINOtpActivity::class.java)
             startActivity(intent)*/

            if (validateEditTexts(binding.mpinEditText, binding.confirmMpinEditText)) {
//                Prefs.putString(Global.MPIN_VALUE, binding.mpinEditText.text.toString().trim())
                PrefsByShubh.setMPINValue(binding.mpinEditText.text.toString())

                finish()
                Toast.makeText(this@ForgotMPINActivity, "Changed Successfully", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this@ForgotMPINActivity,
                    "Please enter MPIN correctly",
                    Toast.LENGTH_SHORT
                ).show()
            }
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