package com.preetTractor.galaxyAndroid.ui.activity.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ActivityCustomerProfileAndOutletPicBinding
import com.preetTractor.galaxyAndroid.ui.activity.BaseActivity

class CustomerProfileAndOutletPicActivity : BaseActivity() {
    lateinit var binding: ActivityCustomerProfileAndOutletPicBinding
    var checkedRadioButtonString = ""

    var customerCardCode: String = ""
    var customerModuleFlag: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerProfileAndOutletPicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve extras using intent
        customerCardCode = intent.getStringExtra("CustomerCardCode") ?: "DefaultCardCode"  // Fallback if null
        customerModuleFlag = intent.getStringExtra("CustomerModuleFlag") ?: "DefaultModuleFlag"  // Fallback if null

        if(customerModuleFlag.equals("CustomerModule")){
            binding.includeLayout.title.text = customerCardCode
        }else{
            binding.includeLayout.title.text = CustomerDetailActivity.cardCode
        }

        binding.includeLayout.ivBack.setOnClickListener {
            finish()
        }

        binding.rgCustomerTab.setOnCheckedChangeListener { group, checkedId ->
            //    val radio: RadioButton = requireActivity().findViewById(checkedId)
            when (checkedId) {
                R.id.rbCompanyProfile -> {
                    checkedRadioButtonString = "profile"

                    val bundle = Bundle().apply {
                        putString("CustomerCardCode", customerCardCode)
                        putString("flagCustomerModule", customerModuleFlag)
                    }

                    binding.fragmentCompanyProfile.getFragment<NavHostFragment>().navController.navigate(
                        R.id.companyProfileFragment,
                        bundle,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.companyProfileFragment, true) // Avoids creating a new instance
                            .build()
                    )
                }

                R.id.rbOutletPics -> {
                    checkedRadioButtonString = "outlet"
                    binding.fragmentCompanyProfile.getFragment<NavHostFragment>().navController.navigate(
                        R.id.outletPicFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.outletPicFragment, true) // Avoids creating a new instance
                            .build()
                    )
                }
            }



        }
    }
}