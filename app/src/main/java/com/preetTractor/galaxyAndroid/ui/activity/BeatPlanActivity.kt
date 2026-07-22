package com.preetTractor.galaxyAndroid.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ActivityBeatPlanBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BeatPlanActivity : BaseActivity() {

    lateinit var binding: ActivityBeatPlanBinding
    private lateinit var navController: NavController
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBeatPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setupActionBarWithNavController(
            navController,
            AppBarConfiguration(setOf())
        )

        supportActionBar?.title = getString(R.string.my_plan)

        setUpViewModel()
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }
    override fun onSupportNavigateUp(): Boolean {

        onBackPressedDispatcher.onBackPressed()

        return true
    }
}