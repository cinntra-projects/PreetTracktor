package com.preetTractor.galaxyAndroid.ui.activity.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.databinding.ActivityTestHerarchyBinding
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestHerarchyActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestHerarchyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestHerarchyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getListing("")

/*
        // Create sample data with nested sub-items up to 4 levels
        val items = listOf(
            Itemlevel(
                name = "Level 1 Item 1",
                subItems = listOf(
                    Itemlevel(
                        name = "Level 2 Item 1",
                        subItems = listOf(
                            Itemlevel(
                                name = "Level 3 Item 1",
                                subItems = listOf(
                                    Itemlevel(name = "Level 4 Item 1")
                                )
                            ),
                            Itemlevel(name = "Level 3 Item 2")
                        )
                    ),
                    Itemlevel(name = "Level 2 Item 2")
                )
            ),
            Itemlevel(name = "Level 1 Item 2")
        )

        // Initialize RecyclerView with the adapter
        val adapter = MultiLevelAdapter(items) { item, level ->
            Toast.makeText(this, "Clicked on ${item.name} at level $level", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter*/
    }


    private fun getListing(dateStr: String) {
        val hde = JsonObject().apply {
            addProperty("SalesEmployeeCode", "28")

        }

        val call = RetrofitClient.apiService.callHerirachyListing(hde)
        call.enqueue(object : Callback<ResponseHeirarchYList> {
            override fun onResponse(
                call: Call<ResponseHeirarchYList>,
                response: Response<ResponseHeirarchYList>
            ) {
                response.body()?.let {
                    if (it.status == 200) {
                        if (response.body()!!.data.isNotEmpty()) {
                            val employees = response.body()!!.data
                            setupRecyclerView(employees)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseHeirarchYList>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")

            }
        })
    }


    companion object {
        private const val TAG = "TestHerarchyActivity"
    }


    private fun setupRecyclerView(employees: List<DataHeirarchYList>) {
        /*val adapter = EmployeeAdapter(employees) { employee, level ->
            Toast.makeText(
                this,
                "Clicked on ${employee.SalesEmployeeName} at level $level",
                Toast.LENGTH_SHORT
            ).show()
        }

        adapter.setOnForwardItemClickListener { dataHeirarchYList, i ->
            Toast.makeText(
                this,
                "Inner Forward on ${dataHeirarchYList.SalesEmployeeName} at level $i",
                Toast.LENGTH_SHORT
            ).show()


        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter*/
    }
}