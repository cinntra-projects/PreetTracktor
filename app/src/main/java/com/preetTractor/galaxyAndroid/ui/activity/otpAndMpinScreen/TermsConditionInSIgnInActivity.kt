package com.preetTractor.galaxyAndroid.ui.activity.otpAndMpinScreen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.preetTractor.galaxyAndroid.databinding.ActivityTermsConditionInSignInBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TermsConditionInSIgnInActivity : AppCompatActivity() {
    lateinit var binding: ActivityTermsConditionInSignInBinding
    lateinit var viewModel: MainViewModel

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
        binding = ActivityTermsConditionInSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        binding.ivBackPress.setOnClickListener {
            finish()
        }

        viewModel.getTermsConditionDetails(this)

        bindObserver()
    }

    companion object {
        private const val TAG = "TermsConditionInSIgnInA"
    }

    private fun bindObserver() {
        viewModel.termsSignIn.observe(this, Event.EventObserver(
            onError = {
                binding.spinKitLoader.visibility = View.GONE
                Log.e(TAG, "bindRemarkObserver: $it")
            },
            onLoading = {
                binding.spinKitLoader.visibility = View.VISIBLE
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.spinKitLoader.visibility = View.GONE

                    if (response.data.size > 0) {
                        try {
                            binding.tvTitle.text = "Terms Of Use"
                            binding.textView.text = response.data[0].description

                        } catch (e: Exception) {


                        }
                    } else {

                    }


                } else if (response.status == 401) {
                    //   sessionManagement.ClearSession()
                    Globals.logoutScreen(this)

                } else {
                    binding.spinKitLoader.visibility = View.GONE
                    Globals.warningMessage(this, response.message)
                }
            }
        ))
    }
}