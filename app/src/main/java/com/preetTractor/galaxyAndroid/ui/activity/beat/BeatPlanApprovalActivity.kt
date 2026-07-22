package com.preetTractor.galaxyAndroid.ui.activity.beat

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ActivityBeatPlanApprovalBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.ui.activity.BaseActivity

class BeatPlanApprovalActivity : BaseActivity() {

    lateinit var binding: ActivityBeatPlanApprovalBinding
    var salesEmpoyeeCode = ""
    var salesEmpoyeeName = ""
    lateinit var dialog: Dialog
    var dateString: String? = null

    companion object{
        private const val TAG = "BeatPlanApprovalActivit"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBeatPlanApprovalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        salesEmpoyeeCode = intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES).toString()
        salesEmpoyeeName = intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES_NAME).toString()

        initialStatus()

        supportActionBar!!.title = salesEmpoyeeName
        supportActionBar!!.setHomeButtonEnabled(true)

    }

    private fun initialStatus() {
        binding.rgLeave.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            when (checkedId) {
                R.id.allRB -> {
                    Log.e(TAG, "intialStatus:ALl")
                  //  adapter.updateEmployeeListItems(allLeaveStatusItemList)
                }

                R.id.approvedRB -> {
                    Log.e(TAG, "intialStatus:approved")
       /*             var filterList =
                        allLeaveStatusItemList!!.filter { it.Approval_Status == "Approved" }*/
                   // adapter.updateEmployeeListItems(filterList)
                }

                R.id.pendingRB -> {
                    Log.e(TAG, "intialStatus:Pending")
               /*     var filterList = allLeaveStatusItemList!!.filter { it.Approval_Status == "Pending"}*/
                   // adapter.updateEmployeeListItems(filterList)
                }

                R.id.rejecedRB -> {
                    Log.e(TAG, "intialStatus:rejected")
                   /* var filterList =
                        allLeaveStatusItemList!!.filter { it.Approval_Status == "Rejected" }*/
                   // adapter.updateEmployeeListItems(filterList)
                }
            }


        }
    }
}