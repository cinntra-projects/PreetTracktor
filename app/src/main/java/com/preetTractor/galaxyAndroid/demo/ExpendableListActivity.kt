package com.preetTractor.galaxyAndroid.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.mvvmSetUp.*
import com.preetTractor.galaxyAndroid.databinding.ActivityExpendableListBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import kotlinx.coroutines.*

class ExpendableListActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var binding : ActivityExpendableListBinding

    private val subjects = LinkedHashMap<String, GroupInfo>()
    private val deptList = ArrayList<GroupInfo>()

    private lateinit var listAdapter: CustomAdapter



    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                if (Globals.checkForInternet(this@ExpendableListActivity)) {
//                    listAdapter.filterList(s.toString())

                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }


    private fun bindEditText() {
        binding.edtSearchActual.addTextChangedListener(textWatcher)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the TextWatcher to avoid memory leaks
        binding.edtSearchActual.removeTextChangedListener(textWatcher)
    }


    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpendableListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Add data for displaying in expandable list view
        loadData()

        setUpViewModel()
        supportActionBar!!.hide()

        binding.ivBack.setOnClickListener {
            finish()
        }

        bindEditText()

//        callAllExpenseListTypeApi()


        //todo custom
        // Create the adapter by passing your ArrayList data
        listAdapter = CustomAdapter(this@ExpendableListActivity, deptList)

        // Attach the adapter to the expandable list view
        binding.simpleExpandableListView.setAdapter(listAdapter)

        // Expand all the Groups
        expandAll()

        // Set OnChildClickListener listener for child row click
        binding.simpleExpandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            // Get the group header
            val headerInfo = deptList[groupPosition]
            // Get the child info
            val detailInfo = headerInfo.productList[childPosition]
            // Display it or do something with it
            Toast.makeText(baseContext, "Clicked on :: ${headerInfo.name} / ${detailInfo.name}", Toast.LENGTH_LONG).show()
            false
        }

        // Set OnGroupClickListener listener for group heading click
        binding.simpleExpandableListView.setOnGroupClickListener { _, _, groupPosition, _ ->
            // Get the group header
            val headerInfo = deptList[groupPosition]
            // Display it or do something with it
            Toast.makeText(baseContext, "Header is :: ${headerInfo.name}", Toast.LENGTH_LONG).show()

            false
        }
    }


    companion object{
        private const val TAG = "ExpendableListActivity"
    }

    fun callAllExpenseListTypeApi() {
        if (Globals.checkForInternet(this)) {
            val jsonObject = JsonObject().apply {
                addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
            }
            Log.e(TAG, "jsonObject : ${jsonObject}")
            viewModel.getListingOfTeamUserExpandable(jsonObject, this)
            bindAllExpenseListObserver()
        }
    }


    private fun bindAllExpenseListObserver() {
        viewModel.listingGetTeamUser.observe(this, Event.EventObserver(
            onError = {
                binding.spinKitLoader.visibility = View.GONE
            },
            onSuccess = {
                binding.spinKitLoader.visibility = View.GONE
                Log.e(TAG, "bindAllExpenseListObserver: ${it.data}")
                if (it.status == 200) {

                    listAdapter = CustomAdapter(this@ExpendableListActivity, deptList)

                    // Attach the adapter to the expandable list view
                    binding.simpleExpandableListView.setAdapter(listAdapter)

                }

                Globals.successMessage(this, it.message)

            },
            onLoading = {
                binding.spinKitLoader.visibility = View.VISIBLE

            }
        ))
    }


    // Method to expand all groups
    private fun expandAll() {
        val count = listAdapter.groupCount
        for (i in 0 until count) {
            binding.simpleExpandableListView.expandGroup(i)
        }
    }

    // Method to collapse all groups
    private fun collapseAll() {
        val count = listAdapter.groupCount
        for (i in 0 until count) {
            binding.simpleExpandableListView.collapseGroup(i)
        }
    }

    // Load some initial data into our list
    private fun loadData() {
        addProduct("Android", "ListView")
        addProduct("Android", "ExpandableListView")
        addProduct("Android", "GridView")

        addProduct("Java", "PolyMorphism")
        addProduct("Java", "Collections")
    }

    // Here we maintain our products in various departments
    private fun addProduct(department: String, product: String): Int {
        var groupPosition = 0

        // Check the hash map if the group already exists
        var headerInfo = subjects[department]
        // Add the group if it doesn't exist
        if (headerInfo == null) {
            headerInfo = GroupInfo()
            headerInfo.name = department
            subjects[department] = headerInfo
            deptList.add(headerInfo)
        }

        // Get the children for the group
        val productList = headerInfo.productList
        // Size of the children list
        var listSize = productList.size
        // Add to the counter
        listSize++

        // Create a new child and add that to the group
        val detailInfo = ChildInfo()
        detailInfo.sequence = listSize.toString()
        detailInfo.name = product
        productList.add(detailInfo)
        headerInfo.productList = productList

        // Find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo)
        return groupPosition
    }

}