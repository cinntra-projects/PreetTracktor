package com.preetTractor.galaxyAndroid.ui.activity.otpAndMpinScreen

import Event
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.data.AttachmentModel
import com.preetTractor.galaxyAndroid.databinding.ActivityOtpBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import `in`.aabhasjindal.otptextview.OTPListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpBinding

    lateinit var viewModel: MainViewModel

//    lateinit var sessionManagement : SessionManagement

    companion object {
        private const val TAG = "OtpActivity"
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    var OTPval = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinKitLoader.visibility = View.GONE

        setUpViewModel()

        binding.tvMobileNo.text = PrefsByShubh.getMobileNO()
        binding.tvShowOtp.text = "Otp is: "+Prefs.getString(Globals.OTP).toString()

        binding.tvShowOtp.text = "Otp is: "+Prefs.getString(Globals.OTP).toString()
        binding.verifyBtn.setOnClickListener {
            var hashmap = HashMap<String, String>()
            hashmap["mobile"] = PrefsByShubh.getMobileNO().toString()
            hashmap["OTP"] = OTPval
            viewModel.verifyOTP(hashmap, this)

            bindObserver()
        }

        binding.ivBackPress.setOnClickListener {
            finish()
        }


        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            override fun onOTPComplete(otp: String) {

                OTPval = otp
                var hashmap = HashMap<String, String>()
                hashmap["mobile"] = PrefsByShubh.getMobileNO().toString()
                hashmap["OTP"] = otp
                viewModel.verifyOTP(hashmap, this@OtpActivity)

                bindObserver()

            }
        }

    }


    //todo bind observer---
    private fun bindObserver() {
        viewModel.loginSignUpData.observe(/* owner = */ this, /* observer = */ Event.EventObserver(
            onError = {
                binding.spinKitLoader.visibility = View.GONE
                Log.e(TAG, "bindRemarkObserver: $it")
            },
            onLoading = {
                binding.spinKitLoader.visibility = View.VISIBLE
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    callAttachmentAllApi(response.data[0].SalesEmployeeCode)
                    binding.spinKitLoader.visibility = View.GONE
                    Log.d(
                        "PREF_SHUBH",
                        "By Api-> \nCardCode: ${response.data[0].card_code}\nCardName: ${response.data[0].card_name}\nDistributorID: ${response.data[0].distributor_id}\nSalesEmpID: ${response.data[0].SalesEmployeeCode}"
                    )
                    Prefs.putString(Globals.CARDCODE, response.data[0].card_code)
//                    Prefs.putString(Global.DISTRIBUTOR_ID, response.data[0].distributor_id)

                    /*PrefsByShubh.setCardCode(response.data[0].card_code)
                    PrefsByShubh.setCardName(response.data[0].card_name)
                    PrefsByShubh.setDistributorID(response.data[0].distributor_id)*/
                    PrefsByShubh.setSalesEmployeeCode(response.data[0].SalesEmployeeCode)
                    Globals.SalesEmployeeCode=response.data[0].SalesEmployeeCode

                    PrefsByShubh.setEmpCode(response.data[0].emp_code)
                    Globals.empCode =response.data[0].emp_code

                    /*if (!response.info.isNullOrEmpty()) {
                        val infoModule = response.info[0]

                        PrefsByShubh.setUserEmail(infoModule.email)
                        PrefsByShubh.setUserPassowrd(infoModule.password)
                        PrefsByShubh.setUserFCM(infoModule.FCM)
                        PrefsByShubh.setUserAppId(infoModule.app_id)



                        Log.d("userInfo12", "email: ${infoModule.email}, password: ${infoModule.password}, fcm: ${infoModule.FCM}, app_id: ${infoModule.app_id}")
                        Log.d("userInfo12", "email: ${PrefsByShubh.getUserEmail()}, password: ${PrefsByShubh.getUserPassword()}, fcm: ${PrefsByShubh.getUserFCM()}, app_id: ${PrefsByShubh.getUserAppId()}")
                    } else {
                        Log.e("userInfo12", "Error: 'data' list is empty or null")
                    }*/



                    Log.d(
                        "PREF_SHUBH",
                        "By PrefByShubh-> \nCardCode: ${ PrefsByShubh.getCardCode()}\nCardName: ${ PrefsByShubh.getCardName()}\nDistributorID: ${ PrefsByShubh.getDistributorID()}\nSalesEmpID: ${ PrefsByShubh.getSalesEmployeeCode()}"
                    )
                    var intent = Intent(this@OtpActivity, MPINActivity::class.java)
                    startActivity(intent)
                    finish()

                } else if (response.status == 401) {
                    PrefsByShubh.ClearSession()
                    Globals.logoutScreen(this)

                } else {
                    binding.spinKitLoader.visibility = View.GONE
                    Globals.warningMessage(this, response.message)
                }
            }
        ))
    }

    private fun callAttachmentAllApi(salesEmpCode:String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", salesEmpCode/*PrefsByShubh.getSalesEmployeeCode()*/)
        val call: Call<AttachmentModel> =
            RetrofitClient.apiService.getNewAllAttachmentApi(jsonObject)
        call.enqueue(object : Callback<AttachmentModel> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<AttachmentModel>, response: Response<AttachmentModel>
            ) {
                if (response.body()!!.status == 200) {

                    if (response.body()!!.data.isNotEmpty()) {

                        //todo role assgin Locally
                        PrefsByShubh.putString("role", response.body()!!.data[0].employee_detail.role_name)
                        PrefsByShubh.putString("role_id", response.body()!!.data[0].employee_detail.role)
                        Log.i("PREF_SHUBH","ActivitySignIn Role id: ${response.body()!!.data[0].employee_detail.role_name}(${response.body()!!.data[0].employee_detail.id})")
                    }
                } else if (response.code() == 201) {
                    Toast.makeText(this@OtpActivity, response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<AttachmentModel>, t: Throwable) {
                Log.e(TAG, "onFailure: " + t.message)
            }
        })
    }

}