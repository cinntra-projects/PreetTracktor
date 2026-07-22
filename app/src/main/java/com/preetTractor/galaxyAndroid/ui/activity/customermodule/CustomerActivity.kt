package com.preetTractor.galaxyAndroid.ui.activity.customermodule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.databinding.ActivityCustomerBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.BaseActivity
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.adapter.CustomersAdapterDetals
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.BPListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CustomerActivity : BaseActivity() {
    private lateinit var binding: ActivityCustomerBinding
    private lateinit var bpDataAdapter: CustomersAdapterDetals
    private val bpDataList = mutableListOf<BPListResponse>()

    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false

    private val searchQueryFlow = MutableStateFlow("")
    private var searchJob: Job? = null // To cancel previous searches

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addCustomer.setOnClickListener {
            val intent = Intent(this@CustomerActivity, AddBPCustomer::class.java)
            startActivity(intent)
        }

        supportActionBar?.apply {
            title = "Business Partner"
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        setupRecyclerView()
        setupSearchView()
        setupSwipeRefresh() // Add this

        // Fetch initial data
        callAllPageApi()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        resetPagination()
        callAllPageApi()
        binding.swipeRefresh.isRefreshing = false // Stop the refresh animation
    }

    private fun setupRecyclerView() {
        binding.customerListing.layoutManager = LinearLayoutManager(this)
        bpDataAdapter = CustomersAdapterDetals(bpDataList)
        binding.customerListing.adapter = bpDataAdapter

        binding.customerListing.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Load more when user reaches near the bottom (3 items before last)
                if (!isLoading && !isLastPage && lastVisibleItemPosition >= totalItemCount - 3) {
                    currentPage++
                    callAllPageApi()
                }
            }
        })
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (searchJob == null) { // Start observing only when user types for the first time
                    observeSearchQuery()
                }
                searchQueryFlow.value = newText ?: ""
                return true
            }
        })
    }

    private fun observeSearchQuery() {
        searchJob?.cancel() // Cancel previous search job
        searchJob = CoroutineScope(Dispatchers.Main).launch {
            searchQueryFlow
                .debounce(500) // Wait 500ms after user stops typing
                .distinctUntilChanged() // Avoid duplicate calls for same text
                .collect { query ->
                    resetPagination()
                    callAllPageApi()
                }
        }
    }

    private fun resetPagination() {
        currentPage = 1
        isLastPage = false
        isLoading = false
        bpDataList.clear()
        bpDataAdapter.notifyDataSetChanged()
    }

    private fun callAllPageApi() {
        if (isLoading || isLastPage) return

        isLoading = true
        binding.loader.loader.visibility = View.VISIBLE

        val jsonObject = JsonObject().apply {
            addProperty("SalesPersonCode", Globals.empCode)
            addProperty("PageNo", currentPage)
            addProperty("maxItem", pageSize)
            addProperty("order_by_field", "id")
            addProperty("order_by_value", "desc")
            addProperty("SearchText", searchQueryFlow.value) // Use latest search text
            add("field", JsonObject())
        }

        val token = "Token ${Globals.GalaxyVistaToken}"
        val call = RetrofitClient.apiService.getBPAllPageList(token, jsonObject)

        call?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                isLoading = false
                binding.loader.loader.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val jsonData = response.body()
                    val dataArray = jsonData?.getAsJsonArray("data")

                    if (dataArray != null && dataArray.size() > 0) {
                        val newData = mutableListOf<BPListResponse>()

                        binding.ivNoDataFound.visibility = View.GONE

                        dataArray.forEach { element ->
                            val obj = element.asJsonObject
                            val bpData = BPListResponse(
                                cardCode = obj.get("CardCode")?.asString ?: "",
                                cardName = obj.get("CardName")?.asString ?: "",
                                emailAddress = obj.get("EmailAddress")?.asString ?: "",
                                phone1 = obj.get("Phone1")?.asString ?: ""
                            )

                            if (!bpDataList.any { it.cardCode == bpData.cardCode }) {
                                newData.add(bpData)
                            }
                        }

                        if (newData.isNotEmpty()) {
                            bpDataList.addAll(newData)
                            bpDataAdapter.notifyDataSetChanged()
                        }

                        if (newData.size < pageSize) {
                            isLastPage = true // Stop pagination if fewer items returned
                        }
                    } else {
                        isLastPage = true
                        if (bpDataList.isEmpty()) {
                            binding.ivNoDataFound.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Toast.makeText(this@CustomerActivity, "API Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                isLoading = false
                binding.loader.loader.visibility = View.GONE
                Toast.makeText(this@CustomerActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        searchJob?.cancel() // Cancel coroutine to avoid memory leaks
    }
}

