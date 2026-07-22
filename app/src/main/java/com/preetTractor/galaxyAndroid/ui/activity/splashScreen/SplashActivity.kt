package com.preetTractor.galaxyAndroid.ui.activity.splashScreen

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.LoginSignUpModel
import com.preetTractor.galaxyAndroid.databinding.ActivitySplashBinding
import com.preetTractor.galaxyAndroid.databinding.EnterMpinCustomPopupAlertBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.ui.activity.ActivitySignIn
import com.preetTractor.galaxyAndroid.ui.activity.MainActivity
import com.github.ybq.android.spinkit.SpinKitView
import com.preetTractor.galaxyAndroid.ui.activity.otpAndMpinScreen.ForgotMPINActivity
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.data.AttachmentModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.helper.Globals.checkInternet
import com.preetTractor.galaxyAndroid.retrofit.WebSocketManager
import `in`.aabhasjindal.otptextview.OTPListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    lateinit var viewModel: MainViewModel


    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        binding.headerIcon.apply {
            alpha = 0f
            scaleX = 0.7f
            scaleY = 0.7f

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1200)
                .start()
        }

        binding.shineView.post {

            val screenWidth = binding.root.width.toFloat()

            binding.shineView.translationX = -300f

            binding.shineView.animate()
                .translationX(screenWidth + 300f)
                .setDuration(1500)
                .setStartDelay(500)
                .withEndAction {

                    if (PrefsByShubh.getMPINValue().isNullOrEmpty()) {
                        startActivity(
                            Intent(
                                this@SplashActivity,
                                ActivitySignIn::class.java
                            )
                        )
                        finish()
                    } else {
                        showMpinPopup()
                    }
                }
                .start()
        }

    }


    private fun showMpinPopup() {
        val builder = AlertDialog.Builder(this@SplashActivity, R.style.CustomAlertDialog).create()
        val bindingBottomSheet: EnterMpinCustomPopupAlertBinding =
            EnterMpinCustomPopupAlertBinding.inflate(layoutInflater)
        builder.setContentView(bindingBottomSheet.root)

        builder.setView(bindingBottomSheet.root)


        bindingBottomSheet.spinKitLoader.visibility = View.GONE

        bindingBottomSheet.ivCancel.setOnClickListener {
            builder.cancel()
//            showAdPopup() //todo comment for now
        }

        bindingBottomSheet.tvForgotMpin.setOnClickListener {

            var intent = Intent(this@SplashActivity, ForgotMPINActivity::class.java)
            startActivity(intent)
        }

        bindingBottomSheet.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            override fun onOTPComplete(otp: String) {
                if (PrefsByShubh.getMPINValue().equals(otp, ignoreCase = true)) {

                    bindingBottomSheet.spinKitLoader.visibility = View.VISIBLE
                    var hashmap = HashMap<String, String>()
//                    hashmap["mobile"] = PrefsByShubh.getMobileNO().toString()
                    hashmap["SalesEmployeeCode"] = PrefsByShubh.getSalesEmployeeCode().toString()
                    hashmap["type"] = "other"
                    hashmap["timestamp"] = Globals.getTodaysDatervrsfrmt() + " " + Globals.getTCurrentTime()
                    checkInternet(this@SplashActivity) { isConnected, isFast ->
                        if (isConnected) {

                            if (isFast) {
                                // ✅ good internet → continue API call
                                callSignInWithSamePhoneNo(hashmap, bindingBottomSheet.spinKitLoader, builder)
                            } else {
                                Toast.makeText(this@SplashActivity,"Internet is slow", Toast.LENGTH_SHORT).show()
                                // ⚠️ slow internet → still allow but show warning
                                callSignInWithSamePhoneNo(hashmap, bindingBottomSheet.spinKitLoader, builder)
                            }


                        }
                    }
//                    showAdPopup()

                    Prefs.putBoolean(Globals.MPIN_DIALOG, true)

                } else {
                    Toast.makeText(
                        this@SplashActivity,
                        "Please Enter Correct Pin",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        builder.setCanceledOnTouchOutside(false)


        builder.setCancelable(true)
        builder.show()

    }


    //todo sign in api call here--
    fun callSignInWithSamePhoneNo(
        hashmap: HashMap<String, String>,
        spinKitLoader: SpinKitView,
        builder: AlertDialog
    ) {
        val call: Call<LoginSignUpModel> = ApiClient().service(this).loginWithSameNumber(hashmap)
        call.enqueue(object : Callback<LoginSignUpModel?> {
            override fun onResponse(
                call: Call<LoginSignUpModel?>,
                response: Response<LoginSignUpModel?>
            ) {
                try {
                    if (response.body()?.status!! == 200) {
                        spinKitLoader.visibility = View.GONE
                        callAttachmentAllApi(PrefsByShubh.getSalesEmployeeCode().toString())
                        /*val i = Intent(this@SplashActivity, MainActivity::class.java)
                        //val i = Intent(this@SplashActivity, BeautyAdvisorMainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(i)
                        finish()*/

                        WebSocketManager.connect(PrefsByShubh.getMobileNO().toString())
                        builder.cancel()
//                        Globals.successMessage(applicationContext,
//                            "Verified Successfully")


                    } else if (response.body()?.status!! == 400) {
                        spinKitLoader.visibility = View.GONE
                        Globals.warningMessage(
                            this@SplashActivity,
                            response.body()?.errors.toString()
                        )
                    } else if (response.body()?.status!! == 401) {

                        spinKitLoader.visibility = View.GONE
                        Globals.warningMessage(
                            this@SplashActivity,
                            "Session Expired, Please Login Again"
                        )
                        spinKitLoader.visibility = View.GONE

                        Prefs.clear()
                        var intent = Intent(this@SplashActivity, ActivitySignIn::class.java)
                        startActivity(intent)
                        finish()
                        PrefsByShubh.ClearSession()

                    } else {
                        spinKitLoader.visibility = View.GONE
                        Globals.errorMessage(
                            this@SplashActivity,
                            response.body()?.errors.toString()
                        )
                    }
                } catch (e: Exception) {
                    spinKitLoader.visibility = View.GONE
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<LoginSignUpModel?>, t: Throwable) {
                spinKitLoader.visibility = View.GONE
                Globals.errorMessage(this@SplashActivity, t.message.toString())
            }
        })
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
                        val i = Intent(this@SplashActivity, MainActivity::class.java)
                        //val i = Intent(this@SplashActivity, BeautyAdvisorMainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(i)
                        finish()
                        Log.i("PREF_SHUBH","ActivitySignIn Role id: ${response.body()!!.data[0].employee_detail.role_name}(${response.body()!!.data[0].employee_detail.id})")
                    }

                    if (!response.body()!!.info.isNullOrEmpty()) {
                        val infoModule = response.body()!!.info[0]
                        Log.d("userInfo12", "emailTA: ${infoModule.email}, password: ${infoModule.password}, fcm: ${infoModule.FCM}, app_id: ${infoModule.app_id}")

                        PrefsByShubh.setUserEmail(infoModule.email)
                        PrefsByShubh.setUserPassowrd(infoModule.password)
                        PrefsByShubh.setUserFCM(infoModule.FCM)
                        PrefsByShubh.setUserAppId(infoModule.app_id)




                            Log.d("userInfo12", "email: ${PrefsByShubh.getUserEmail()}, password: ${PrefsByShubh.getUserPassword()}, fcm: ${PrefsByShubh.getUserFCM()}, app_id: ${PrefsByShubh.getUserAppId()}")
                    } else {
                        Log.e("userInfo12", "Error: 'data' list is empty or null")
                    }

                } else if (response.code() == 201) {
                    Toast.makeText(this@SplashActivity, response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<AttachmentModel>, t: Throwable) {
            }
        })
    }






}