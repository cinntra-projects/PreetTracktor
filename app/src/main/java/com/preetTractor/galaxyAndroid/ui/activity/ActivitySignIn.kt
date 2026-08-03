package com.preetTractor.galaxyAndroid.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.data.AttachmentModel
import com.preetTractor.galaxyAndroid.data.LoginSignUpModel
import com.preetTractor.galaxyAndroid.databinding.ActivitySignInBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.otpAndMpinScreen.OtpActivity
import com.preetTractor.galaxyAndroid.ui.activity.otpAndMpinScreen.TermsConditionInSIgnInActivity
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivitySignIn : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    var selectedCountry = "India"
    lateinit var viewModel: MainViewModel

    companion object {
        private const val TAG = "SignInActivity"
         val instance = this
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PrefsByShubh.ClearSession()
        Prefs.clear()
        getFCMToken()

        binding.countryPickerAlternate.setOnCountryChangeListener(CountryCodePicker.OnCountryChangeListener {
            selectedCountry = binding.countryPickerAlternate.getSelectedCountryName()
        })


        Log.e(TAG, "mpin_value: " + PrefsByShubh.getMPINValue())
        Log.e("Prefrence", "${PrefsByShubh.getMPINValue()}")

        if (PrefsByShubh.getMPINValue().equals("")) {
            binding.sentOtpBtn.text = "Send OTP"
        } else {
            binding.sentOtpBtn.text = "Sign In"
        }


        PrefsByShubh.setFromWhere("login")

        setUpViewModel()

        binding.spinKitLoader.visibility = View.GONE

        binding.tvTermsCond.setOnClickListener {
            Intent(this, TermsConditionInSIgnInActivity::class.java).also {
                startActivity(it)
            }
        }


        binding.sentOtpBtn.setOnClickListener {

            if (binding.edtMobileNo.text.toString().equals("")) {
                Globals.warningMessage(this, "Enter Mobile")
            } else if (!"India".equals(selectedCountry, ignoreCase = true)) {
                Toast.makeText(this, "Please Choose India", Toast.LENGTH_SHORT).show()

            } else {
                if (binding.checkBoXTerm.isChecked) {
                    binding.spinKitLoader.visibility = View.VISIBLE

                    var hashmap = HashMap<String, String>()
                    hashmap["mobile"] = binding.edtMobileNo.text.toString() //8809069134

                    binding.sentOtpBtn.isEnabled = false
                    binding.sentOtpBtn.isClickable = false
                    callSignInApi(hashmap)
                } else {
                    Globals.warningMessage(this, "Please accept terms & condition")
                }


            }


        }
    }


    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM", "Fetching FCM token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                token?.let {
                    Log.d("FCM", "FCM Token: $token")
                    saveTokenToSharedPreferences(token)
                }
            }
    }

    private fun saveTokenToSharedPreferences(token: String) {

        PrefsByShubh.setFirebaseFCMToken(token)
    }

    fun callSignInApi(hashmap: HashMap<String, String>) {
        binding.spinKitLoader.visibility = View.VISIBLE
        val call: Call<LoginSignUpModel> = ApiClient().service(this).loginSignUpApi(hashmap)
        call.enqueue(object : Callback<LoginSignUpModel?> {
            override fun onResponse(
                call: Call<LoginSignUpModel?>,
                response: Response<LoginSignUpModel?>
            ) {
                try {
                    if (response.code() == 200) {
                        if (response.body()?.status == 200) {
                            callAttachmentAllApi()
                            binding.sentOtpBtn.isEnabled = true
                            binding.sentOtpBtn.isClickable = true
                            Globals.loginIntoAnotherDevice = false
                            if (response.body()?.data!!.isNotEmpty()) {
                                binding.spinKitLoader.visibility = View.GONE

                                Prefs.putString(Globals.OTP, response.body()?.data!![0].otp)

                                PrefsByShubh.setMobileNo(binding.edtMobileNo.text.toString())


                              /*  if (PrefsByShubh.getMPINValue().equals("")) {
                                    var intent: Intent =
                                        Intent(this@ActivitySignIn, OtpActivity::class.java)
                                    startActivity(intent)

                                } else {
                                    Prefs.putString(
                                        Globals.REDIRECT_TO_MPIN_DIALOG,
                                        "SHOW_MPIN_DIALOG"
                                    )

                                    val i = Intent(this@ActivitySignIn, MainActivity::class.java)
                                    //val i = Intent(this@ActivitySignIn, BeautyAdvisorMainActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(i)
                                    finish()

                                }*/
                                var intent: Intent =
                                    Intent(this@ActivitySignIn, OtpActivity::class.java)
                                startActivity(intent)

                            }
                        } else if (response.body()?.status == 400) {
                            binding.sentOtpBtn.isEnabled = true
                            binding.sentOtpBtn.isClickable = true
                            binding.spinKitLoader.visibility = View.GONE
                            binding.sentOtpBtn.isEnabled = false
                            binding.sentOtpBtn.isClickable = false

                            /* var jsonObject = JsonObject()
                             jsonObject.addProperty("mobile", binding.edtMobileNo.text.toString())
                             bindLogoutObserver(jsonObject)*/
                            alertOnSameLoginNo()
//                        Global.warningMessage(this@SignInActivity, response.body()?.errors.toString())
                        } else if (response.body()?.status!! == 404) {
                            binding.sentOtpBtn.isEnabled = true
                            binding.sentOtpBtn.isClickable = true
                            binding.spinKitLoader.visibility = View.GONE
                            Globals.warningMessage(
                                this@ActivitySignIn,
                                response.body()?.errors.toString()
                            )
                        } else {
                            binding.sentOtpBtn.isEnabled = true
                            binding.sentOtpBtn.isClickable = true
                            binding.spinKitLoader.visibility = View.GONE
                            Globals.errorMessage(
                                this@ActivitySignIn,
                                response.body()?.errors.toString()
                            )
                        }
                    } else {
                        binding.sentOtpBtn.isEnabled = true
                        binding.sentOtpBtn.isClickable = true
                        binding.spinKitLoader.visibility = View.GONE
                        Globals.errorMessage(
                            this@ActivitySignIn,
                            response.body()?.errors.toString()
                        )
                    }

                } catch (e: Exception) {
                    binding.sentOtpBtn.isEnabled = true
                    binding.sentOtpBtn.isClickable = true
                    binding.spinKitLoader.visibility = View.GONE
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<LoginSignUpModel?>, t: Throwable) {
                binding.sentOtpBtn.isEnabled = true
                binding.sentOtpBtn.isClickable = true
                binding.spinKitLoader.visibility = View.GONE
                Globals.errorMessage(this@ActivitySignIn, t.message.toString())
            }
        })
    }

    fun alertOnSameLoginNo() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage("Already logged in with another device. Logout From all other Devices?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()

            binding.sentOtpBtn.isEnabled = false
            binding.sentOtpBtn.isClickable = false

            var jsonObject = JsonObject()
            jsonObject.addProperty("mobile", binding.edtMobileNo.text.toString())
            jsonObject.addProperty("device_id", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID).toString())
            bindLogoutObserver(jsonObject)

//            viewModel.logoutApi(jsonObject, this)
//            bindLogoutObserver(jsonObject)


        }
        builder.setNegativeButton("No") { dialog, _ ->
            binding.sentOtpBtn.isEnabled = true
            binding.sentOtpBtn.isClickable = true
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun callAttachmentAllApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
        val call: Call<AttachmentModel> =
            RetrofitClient.apiService.getNewAllAttachmentApi(jsonObject)
        call.enqueue(object : Callback<AttachmentModel> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<AttachmentModel>, response: Response<AttachmentModel>
            ) {
                val responseBody = response.body() // Store response body in a variable

                if (response.isSuccessful && responseBody != null) { // Check if response is successful and body is not null
                    if (responseBody.status == 200) {
                        if (!responseBody.data.isNullOrEmpty()) { // Check if `data` is not null or empty
                            // Role assignment locally
                            PrefsByShubh.putString("role", responseBody.data[0].employee_detail.role_name)
                            PrefsByShubh.putString("role_id", responseBody.data[0].employee_detail.role)
                            Log.i(
                                "PREF_SHUBH",
                                "ActivitySignIn Role id: ${responseBody.data[0].employee_detail.role_name} (${responseBody.data[0].employee_detail.id})"
                            )
                        }
                    } else if (responseBody.status == 201) {
                        Toast.makeText(this@ActivitySignIn, responseBody.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("ActivitySignIn", "Unexpected status: ${responseBody.status}")
                    }
                } else {
                    val errorMessage = response.message() ?: "Unknown error occurred"
                    Log.e("ActivitySignIn", "Response unsuccessful or body is null. Error: $errorMessage")
                }

            }

            override fun onFailure(call: Call<AttachmentModel>, t: Throwable) {
                Log.e(TAG, "onFailure: " + t.message)
            }
        })
    }

    fun bindLogoutObserver(hashmap: JsonObject) {
        binding.spinKitLoader.visibility = View.VISIBLE
        val call: Call<LoginSignUpModel> = ApiClient().service(this).callLogoutApi(hashmap)
        call.enqueue(object : Callback<LoginSignUpModel?> {
            override fun onResponse(
                call: Call<LoginSignUpModel?>,
                response: Response<LoginSignUpModel?>
            ) {
                try {
                    if (response.body()?.status == 200) {
                        PrefsByShubh.putString("role", "")
                        binding.spinKitLoader.visibility = View.GONE

                        var hashmap = HashMap<String, String>()
                        hashmap["mobile"] = binding.edtMobileNo.text.toString() //8809069134

                        binding.sentOtpBtn.isEnabled = false
                        binding.sentOtpBtn.isClickable = false
                        callSignInApi(hashmap)


                    } else if (response.body()?.status == 401) {
                   //     PrefsByShubh.ClearSession()
                        PrefsByShubh.putString("role", "")
                        Globals.logoutScreen(this@ActivitySignIn)

                    } else {
                        binding.spinKitLoader.visibility = View.GONE
                        Globals.warningMessage(this@ActivitySignIn, response.body()!!.message)
                    }
                } catch (e: Exception) {
                    binding.sentOtpBtn.isEnabled = true
                    binding.sentOtpBtn.isClickable = true
                    binding.spinKitLoader.visibility = View.GONE
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<LoginSignUpModel?>, t: Throwable) {
                binding.sentOtpBtn.isEnabled = true
                binding.sentOtpBtn.isClickable = true
                binding.spinKitLoader.visibility = View.GONE
                Globals.errorMessage(this@ActivitySignIn, t.message.toString())
            }
        })
    }
}