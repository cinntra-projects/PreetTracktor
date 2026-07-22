package com.preetTractor.galaxyAndroid.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.databinding.ActivityUserScreenBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.*
import com.preetTractor.galaxyAndroid.ui.recyclerview.TeamUserAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserScreenActivity : BaseActivity() {

    private lateinit var binding: ActivityUserScreenBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: TeamUserAdapter

    private var fromWhere = ""
    private var headingTitle = ""

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) = Unit

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {

            searchRunnable?.let {
                searchHandler.removeCallbacks(it)
            }

            searchRunnable = Runnable {

                if (::adapter.isInitialized) {
                    adapter.filterList(s.toString())
                }
            }

            searchHandler.postDelayed(searchRunnable!!, 500)
        }

        override fun afterTextChanged(s: Editable?) = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()

        setUpViewModel()

        initViews()

        bindObservers()

        callAllExpenseListTypeApi()
    }

    private fun getIntentData() {

        fromWhere = intent.getStringExtra(Constant.WHERE_INTENT).orEmpty()

        headingTitle = intent.getStringExtra(Constant.HEADING_TITLE).orEmpty()
    }

    private fun initViews() {

        supportActionBar?.hide()

        binding.tvNameOfEmployee.text = headingTitle

        binding.rvUsers.layoutManager = LinearLayoutManager(this)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.edtSearchActual.addTextChangedListener(textWatcher)
    }

    private fun setUpViewModel() {

        val dispatchers: CoroutineDispatcher = Dispatchers.Main

        val mainRepos = DefaultMainRepositories() as MainRepos

        val fanxApi: ApisInterface = ApiClient().service(this)

        val factory = MainViewModelProvider(
            application,
            mainRepos,
            dispatchers,
            fanxApi
        )

        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun callAllExpenseListTypeApi() {

        if (!Globals.checkForInternet(this)) return

        val jsonObject = JsonObject().apply {

            addProperty(
                "SalesEmployeeCode",
                PrefsByShubh.getEmpCode()
            )
        }

        viewModel.getListingOfTeamUser(jsonObject, this)
    }

    private fun bindObservers() {

        viewModel.listingGetTeamUser.observe(
            this,
            Event.EventObserver(

                onLoading = {
                    binding.spinKitLoader.visibility = View.VISIBLE
                },

                onError = {
                    binding.spinKitLoader.visibility = View.GONE
                },

                onSuccess = { response ->

                    binding.spinKitLoader.visibility = View.GONE

                    if (response.status == 200) {

                        val filteredList = response.data.filter {

                            it.SalesEmployeeCode == PrefsByShubh.getEmpCode()
                        }

                        adapter = TeamUserAdapter(
                            filteredList,
                            this,
                            fromWhere
                        )

                        binding.rvUsers.adapter = adapter

                        handleRecyclerClicks()
                    }

                    Globals.successMessage(this, response.message)
                }
            )
        )
    }

    private fun handleRecyclerClicks() {

        adapter.setOnItemForwardClickListener { dataTeamList, _ ->

            when (fromWhere) {

                Constant.WHERE_INTENT_VALUE_STATUS -> {

                    openScreen(
                        AttendanceBackGroundListActivity::class.java,
                        "sales" to dataTeamList.SalesEmployeeCode,
                        "name" to dataTeamList.SalesEmployeeName,
                        "teamStatusId" to dataTeamList.id.toString()
                    )
                }

                Constant.WHERE_INTENT_VALUE_LEAVE -> {

                    openScreen(
                        LeaveActivity::class.java,
                        Constant.WHERE_INTENT_VALUE_SALES to dataTeamList.SalesEmployeeCode,
                        Constant.WHERE_INTENT_VALUE_SALES_NAME to dataTeamList.SalesEmployeeName,
                        "itemId" to dataTeamList.id.toString()
                    )
                }

                Constant.WHERE_INTENT_VALUE_EXPENSE -> {

                    openScreen(
                        ExpenseRequestActivity::class.java,
                        Constant.WHERE_INTENT_VALUE_SALES to dataTeamList.SalesEmployeeCode,
                        Constant.WHERE_INTENT_VALUE_SALES_NAME to dataTeamList.SalesEmployeeName,
                        "expenseItemId" to dataTeamList.id.toString()
                    )
                }

                Constant.WHERE_INTENT_VALUE_ATTENDANCE -> {

                    PrefsByShubh.putString(
                        Constant.SALESEMPLOYEECODE,
                        dataTeamList.SalesEmployeeCode
                    )

                    PrefsByShubh.putString(
                        Constant.SALESEMPLOYEENAME,
                        dataTeamList.SalesEmployeeName
                    )

                    PrefsByShubh.putString(
                        Constant.FLAG,
                        "_FROM_ATTENDANCE"
                    )

                    openScreen(
                        AttendanceActivity::class.java,
                        Constant.WHERE_INTENT_VALUE_SALES to dataTeamList.SalesEmployeeCode,
                        Constant.WHERE_INTENT_VALUE_SALES_NAME to dataTeamList.SalesEmployeeName,
                        "attendanceItemId" to dataTeamList.id.toString(),
                        "checkFlag" to "TeamsModule"
                    )
                }

                Constant.WHERE_INTENT_VALUE_BEAT_PLAN -> {

                    openScreen(
                        BeatPlanActivity::class.java,
                        Constant.WHERE_INTENT_VALUE_SALES to dataTeamList.id.toString(),
                        Constant.WHERE_INTENT_VALUE_SALES_NAME to dataTeamList.SalesEmployeeName,
                        "itemBeatPlanId" to dataTeamList.id
                    )
                }
            }
        }
    }

    private fun openScreen(
        clazz: Class<*>,
        vararg extras: Pair<String, Any>
    ) {

        startActivity(
            Intent(this, clazz).apply {

                extras.forEach { pair ->

                    when (val value = pair.second) {

                        is String -> putExtra(pair.first, value)

                        is Int -> putExtra(pair.first, value)

                        is Boolean -> putExtra(pair.first, value)
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.edtSearchActual.removeTextChangedListener(textWatcher)

        searchRunnable?.let {
            searchHandler.removeCallbacks(it)
        }
    }
}