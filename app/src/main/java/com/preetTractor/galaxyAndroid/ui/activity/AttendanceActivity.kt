package com.preetTractor.galaxyAndroid.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.preetTractor.galaxyAndroid.databinding.ActivityAttendanceBinding
import com.preetTractor.galaxyAndroid.helper.Constant

class AttendanceActivity : BaseActivity() {
    lateinit var binding : ActivityAttendanceBinding
    var salesEmpoyeeCode = ""
    var salesEmpoyeeName = ""
    var attendanceItemId = ""
    var checkFlag = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        salesEmpoyeeCode = intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES).toString()
        salesEmpoyeeName = intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES_NAME).toString()
        attendanceItemId = intent.getStringExtra("attendanceItemId").toString()
        checkFlag = intent.getStringExtra("checkFlag").toString()

        binding.tvNameOfEmployee.setText(salesEmpoyeeName)

        binding.ivBack.setOnClickListener {
//            findNavController(R.id.fragmentContainerAttendanceActivity).popBackStack()
            finish()
            finish()

        }
        binding.fragmentContainerAttendanceActivity.getFragment<NavHostFragment>().navController.apply {
            addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {


                }
            }
        }


    }


}