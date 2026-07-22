package com.preetTractor.galaxyAndroid.ui.activity.otpAndMpinScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.preetTractor.galaxyAndroid.databinding.ActivityVerifyMpinotpBinding

class VerifyMPINOtpActivity : AppCompatActivity() {

    lateinit var binding: ActivityVerifyMpinotpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyMpinotpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBackPress.setOnClickListener {
            finish()
        }

    }
}