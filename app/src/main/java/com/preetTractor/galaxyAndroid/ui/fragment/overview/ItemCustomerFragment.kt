package com.preetTractor.galaxyAndroid.ui.fragment.overview

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.data.model.customer.DataItemDashboard
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseItemListCustomerDashboard
import com.preetTractor.galaxyAndroid.databinding.FragmentItemCustomerBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.hideKeyboard
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.ItemStockAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import java.io.IOException


class ItemCustomerFragment : Fragment() {
    lateinit var binding: FragmentItemCustomerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemCustomerBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "ItemCustomerFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callApi()


        binding.rvItemList.addOnScrollListener(scrollListener)

        binding.edtSearchActual.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
              CoroutineScope(Dispatchers.Main).launch {
                  delay(1000)
                  hideKeyboard()
                  pageNo=1
                  searchTextValue=p0.toString()

                  callApi()
              }




            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }

    var searchTextValue = ""
    var pageNo = 1
    var AllitemsList = ArrayList<DataItemDashboard>()

    var layoutManager: LinearLayoutManager? = null

    var isLoading = false
    var islastPage = false
    var isScrollingpage = false

    var scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            val firstVisibleitempositon = layoutManager!!.findFirstVisibleItemPosition() //first item
            val visibleItemCount = layoutManager!!.childCount //total number of visible item
            val totalItemCount = layoutManager!!.itemCount //total number of item
            val isNotLoadingAndNotLastPage = !isLoading && !islastPage
            val isAtLastItem = firstVisibleitempositon + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleitempositon >= 0
            val isTotaolMoreThanVisible: Boolean = totalItemCount >= 20
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isNotAtBeginning && isAtLastItem && isTotaolMoreThanVisible && isScrollingpage
            if (isScrollingpage && visibleItemCount + firstVisibleitempositon == totalItemCount) {
                binding.progressBar.setVisibility(View.VISIBLE)
                if (Globals.checkForInternet(requireContext())) {
                    pageNo++
                    callAllPagesApi()
                }
                isScrollingpage = false
            } else {
                // Log.d(TAG, "onScrolled:not paginate");
                recyclerView.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //it means we are scrolling
                isScrollingpage = true
            }
        }
    }
    var adapter: ItemStockAdapter? = null


    private fun callApi() {


        binding.progressBar.visibility = View.VISIBLE

        Thread {

            val hde = JsonObject().apply {
                addProperty("CardCode", CustomerDetailActivity.cardCode)
                addProperty("SearchText", searchTextValue)
                addProperty("FromDate", Globals.firstDateOfFinancialYear())
                addProperty("ToDate", Globals.lastDateOfFinancialYear())
                addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
                addProperty("MaxSize", 20)
                addProperty("PageNo", pageNo)
            }


            val call: Call<ResponseItemListCustomerDashboard> =
                RetrofitClient.apiService.getItemListingForCustomersOverview(hde)

            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).post {
                        if (response.code() == 200) {
                            AllitemsList.clear()
                            if (response.body()?.data?.isNotEmpty() == true) {
                                binding.noDatafound.visibility = View.GONE
                                AllitemsList.addAll(response.body()?.data ?: emptyList())
                                adapter = ItemStockAdapter(requireContext(), AllitemsList, "")
                                adapter!!.AllData(AllitemsList)
                                layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    RecyclerView.VERTICAL,
                                    false
                                )
                                binding.rvItemList.layoutManager = layoutManager
                                binding.rvItemList.adapter = adapter


                            } else {
                                binding.noDatafound.visibility = View.VISIBLE
                            }
                            binding.progressBar.visibility = View.GONE
                            adapter?.notifyDataSetChanged()

                        }
                    }
                } else {

                    binding.progressBar.visibility = View.GONE
                }
            } catch (e: IOException) {

                // Handle the exception
            }
        }.start()
    }

    private fun callAllPagesApi() {
        Thread {
            val hde = JsonObject().apply {
                addProperty("CardCode", CustomerDetailActivity.cardCode)
                addProperty("SearchText", searchTextValue)
                addProperty("FromDate", Globals.firstDateOfFinancialYear())
                addProperty("ToDate", Globals.lastDateOfFinancialYear())
                addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
                addProperty("MaxSize", 20)
                addProperty("PageNo", pageNo)
            }

            val call: Call<ResponseItemListCustomerDashboard> =
                RetrofitClient.apiService.getItemListingForCustomersOverview(hde)


            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).post {
                        if (response.code() == 200) {
                            response.body()?.data?.let {
                                AllitemsList.addAll(it)
                            }
                            adapter!!.AllData(AllitemsList)
                            binding.progressBar.visibility = View.GONE

                            // Uncomment this if database logic is needed
                            /*
                            if (StockGroupCode.isEmpty() && searchTextValue.isEmpty() && filterByName.isEmpty() && filterByAmount.isEmpty()) {
                                db.myDataDao().insertAll(response.body()?.data)
                            }
                            */
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            } catch (e: IOException) {
                // Handle the exception
            }
        }.start()
    }

}