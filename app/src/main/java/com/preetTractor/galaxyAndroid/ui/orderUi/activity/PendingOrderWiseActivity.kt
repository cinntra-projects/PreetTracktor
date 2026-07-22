package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.databinding.ActivityPendingOrderWiseBinding
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PendingOrderWiseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPendingOrderWiseBinding
    private lateinit var navController: NavController
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
        binding = ActivityPendingOrderWiseBinding.inflate(layoutInflater)
        setUpViewModel()
        initViews()
        clickListeners()
        setContentView(binding.root)

    }

    private fun clickListeners() {
        binding.apply {
            ibBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun initViews() {
        // Setup NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentPendingOrderWiseContainer) as NavHostFragment
        navController = navHostFragment.navController
    }
}