package com.preetTractor.galaxyAndroid.ui.activity.otpAndMpinScreen

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.preetTractor.galaxyAndroid.data.LoginSignUpModel
import com.preetTractor.galaxyAndroid.databinding.ActivityMpinactivityBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.ui.activity.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.helper.Globals.checkInternet
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.retrofit.WebSocketManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MPINActivity : AppCompatActivity() {

    lateinit var binding: ActivityMpinactivityBinding

    lateinit var android_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMpinactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // supportActionBar!!.hide()

        android_id =
            Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)
        Prefs.putString(Globals.DEVICE_ID, android_id)
        binding.spinKitLoader.visibility = View.GONE

        binding.continueBtn.setOnClickListener {
            if (validateTwoEditTexts(binding.mpinEditText, binding.confirmMpinEditText)) {

//                Prefs.putString(Globals.MPIN_VALUE, binding.mpinEditText.text.toString())
                Prefs.putString(Globals.REDIRECT_TO_MPIN_DIALOG, "NO_SHOW_MPIN_DIALOG")

                PrefsByShubh.setMPINValue(binding.mpinEditText.text.toString())

                binding.spinKitLoader.visibility = View.VISIBLE
                var hashmap = HashMap<String, String>()
                hashmap["SalesEmployeeCode"] = PrefsByShubh.getSalesEmployeeCode().toString()
                hashmap["type"] = "login"
                hashmap["device_id"] = android_id
                hashmap["device_name"] = "galaxy"
                hashmap["timestamp"] = Globals.getTodaysDatervrsfrmt() + " " + Globals.getTCurrentTime()
                checkInternet(this) { isConnected, isFast ->
                    if (isConnected) {

                        if (isFast) {
                            // ✅ good internet → continue API call
                            callWithSamePhoneNo(hashmap)
                        } else {
                            // ⚠️ slow internet → still allow but show warning
                            callWithSamePhoneNo(hashmap)
                        }

                    }
                }


            } else {
                Toast.makeText(this@MPINActivity, "Please Enter MPIN Correctly", Toast.LENGTH_SHORT)
                    .show()
            }

        }


        binding.ivBackPress.setOnClickListener {
            finish()
        }

    }


    //todo sign in api call here--
    fun callWithSamePhoneNo(hashmap: HashMap<String, String>) {

        val token = "Token ${Globals.GalaxyVistaToken}"

        val call: Call<LoginSignUpModel> = ApiClient().service(this).loginWithSameNumber(hashmap)
        call.enqueue(object : Callback<LoginSignUpModel?> {
            override fun onResponse(
                call: Call<LoginSignUpModel?>,
                response: Response<LoginSignUpModel?>
            ) {
                try {
                    if (response.body()?.status!! == 200) {
                        binding.spinKitLoader.visibility = View.GONE
                        binding.continueBtn.isEnabled = false
                        binding.continueBtn.isClickable = false

                        PrefsByShubh.setToken(response.body()!!.data[0].token)

                        Prefs.putString(Globals.TOKEN, response.body()!!.data[0].token)
                        Globals.loginIntoAnotherDevice = false
                        PrefsByShubh.setFromWhere("ElseCase")

                       WebSocketManager.connect(PrefsByShubh.getMobileNO().toString())

                        Log.d("kjnkjdskbc", "onResponse: kbs kjbskjb")

                        if (!response.body()?.info.isNullOrEmpty()) {
                            val infoModule = response.body()!!.info[0]
                            Log.d("userInfo12", "emailTArun: ${infoModule.email}, password: ${infoModule.password}, fcm: ${infoModule.FCM}, app_id: ${infoModule.app_id}")

                            PrefsByShubh.setUserEmail(infoModule.email)
                            PrefsByShubh.setUserPassowrd(infoModule.password)
                            PrefsByShubh.setUserFCM(infoModule.FCM)
                            PrefsByShubh.setUserAppId(infoModule.app_id)


                            callSuperAdminApiForToken()


//                            Log.d("userInfo12", "email: ${PrefsByShubh.getUserEmail()}, password: ${PrefsByShubh.getUserPassword()}, fcm: ${PrefsByShubh.getUserFCM()}, app_id: ${PrefsByShubh.getUserAppId()}")
                        } else {
                            Log.e("userInfo12", "Error: 'data' list is empty or null")
                        }



                       val i = Intent(this@MPINActivity, MainActivity::class.java)
                        //val i = Intent(this@MPINActivity, BeautyAdvisorMainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(i)
                        finishAffinity()

                    } else if (response.body()?.status === 401) {
                        Prefs.clear()
                        Globals.logoutScreen(this@MPINActivity)
                    } else if (response.body()?.status!! == 400) {
                        binding.continueBtn.isEnabled = true
                        binding.continueBtn.isClickable = true
                        binding.spinKitLoader.visibility = View.GONE
                        Globals.warningMessage(
                            this@MPINActivity,
                            response.body()?.errors.toString()
                        )
                    } else if (response.body()?.status!! == 404) {
                        binding.continueBtn.isEnabled = true
                        binding.continueBtn.isClickable = true
                        binding.spinKitLoader.visibility = View.GONE
                        Globals.warningMessage(
                            this@MPINActivity,
                            response.body()?.errors.toString()
                        )
                    } else {
                        binding.continueBtn.isEnabled = true
                        binding.continueBtn.isClickable = true
                        binding.spinKitLoader.visibility = View.GONE
                        Globals.errorMessage(this@MPINActivity, response.body()?.errors.toString())
                    }
                } catch (e: Exception) {
                    binding.continueBtn.isEnabled = true
                    binding.continueBtn.isClickable = true
                    binding.spinKitLoader.visibility = View.GONE
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<LoginSignUpModel?>, t: Throwable) {
                binding.continueBtn.isEnabled = true
                binding.continueBtn.isClickable = true
                binding.spinKitLoader.visibility = View.GONE
                Globals.errorMessage(this@MPINActivity, t.message.toString())
            }
        })
    }

    private fun validateTwoEditTexts(
        editText1: TextInputEditText,
        editText2: TextInputEditText
    ): Boolean {
        val text1 = editText1.text.toString().trim { it <= ' ' }
        val text2 = editText2.text.toString().trim { it <= ' ' }
        return if (TextUtils.isEmpty(text1) || TextUtils.isEmpty(text2)) {
            false
        } else text1 == text2

    }


    private fun callSuperAdminApiForToken() {

        val jsonObject = JsonObject().apply {
            addProperty("email", PrefsByShubh.getUserEmail())
            addProperty("password", PrefsByShubh.getUserPassword())
            addProperty("FCM", PrefsByShubh.getFirebaseFCMToken())
            addProperty("app_id", PrefsByShubh.getUserAppId())
        }

        val call = RetrofitClient.apiService1.getLoginToken(jsonObject)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    response.body()?.let { jsonResponse ->
                        if (jsonResponse.has("status") && jsonResponse.get("status").asInt == 200) {
                            // Store token globally
                            Globals.GalaxyVistaToken = jsonResponse.get("token").asString
                            Log.d("superAdminToken", "Token Stored: ${Globals.GalaxyVistaToken}")


                        } else {
                            Log.e(
                                "superAdminToken",
                                "Login Failed: ${jsonResponse.get("message").asString}"
                            )
                        }
                    }
                } else {
                    Log.e("superAdminToken", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.e("superAdminToken", "API Failure: ${t.message}")
            }
        })


    }

}