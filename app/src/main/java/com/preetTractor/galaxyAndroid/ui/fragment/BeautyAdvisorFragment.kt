package com.preetTractor.galaxyAndroid.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.databinding.FragmentBeautyAdvisorBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.ui.activity.MainActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.AddOrderActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.PreviousOrderDetailsActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.BAPendingOrderPagingAdapter
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs

class BeautyAdvisorFragment : Fragment() {

      private lateinit var binding: FragmentBeautyAdvisorBinding
      lateinit var viewModel: MainViewModel
      var pageNo = 1
      var isScrollingpage = false
      lateinit var layoutManager: LinearLayoutManager
      var pendingOrderPagingAdapter = BAPendingOrderPagingAdapter()
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
      }

      override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
      ): View? {
            // Inflate the layout for this fragment
            binding = FragmentBeautyAdvisorBinding.inflate(inflater, container, false)
            return binding.root
      }

      override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initViews()
            clickListener()
      }

      private fun initViews() {
            //setOrderAdapter()
            viewModel = (activity as MainActivity).viewModel
            layoutManager = LinearLayoutManager(requireContext())
      }

      private fun clickListener() {
            binding.apply {

                  swipeRefreshLayout.setOnRefreshListener {
                        swipeRefreshLayout.isRefreshing = true
                        setupRequestForItemList()
                        swipeRefreshLayout.isRefreshing=false

                  }
                  tvBtnAddOrder.setOnClickListener {
                        //Toast.makeText(requireContext(), "Add Order Clicked", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(), AddOrderActivity::class.java))
                  }

                  pendingOrderPagingAdapter.setOnItemClickListener { data, pos ->
                        val i = Intent(requireContext(), PreviousOrderDetailsActivity::class.java)
                        i.putExtra("data", data)
                        i.putExtra("where", "under")
                        startActivity(i)
                  }
            }
      }

      private fun setupRecyclerViewInitial() {
            //todo bind adapter here--
            binding.rvPreviousOrders.layoutManager = layoutManager
            binding.rvPreviousOrders.adapter = pendingOrderPagingAdapter
      }

      private fun setUpRecyclerViewPaging() {
            //todo set recyclerview add scroll page
            binding.rvPreviousOrders.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val firstVisibleItemPosition =
                              layoutManager.findFirstVisibleItemPosition() // first item position
                        val visibleItemCount = layoutManager.childCount // total number of visible items
                        val totalItemCount = layoutManager.itemCount // total number of items in the adapter

                        // Check if scrolling down and reached the end of the list
                        if (isScrollingpage && dy > 0 && visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                              pageNo++
                              setupRequestForItemList()
                              isScrollingpage = false
                        } else {
                              recyclerView.setPadding(0, 0, 0, 0)
                        }
                  }

                  override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                              // User is scrolling
                              isScrollingpage = true
                        }
                  }
            })

      }

      private fun setupRequestForItemList() {

            viewModel.getBAOrderListingAllFilter(JsonObject().apply {
                  addProperty("SalesPersonCode", PrefsByShubh.getSalesEmployeeCode())
                  addProperty("PageNo", pageNo)
                  addProperty("CardCode", Prefs.getString(Globals.CARD_CODE_BP_ONE))
                  addProperty("maxItem", 10)
                  addProperty("order_by_field", "id")
                  addProperty("order_by_value", "desc")
                  addProperty("SearchText", "")
                  add("field", JsonObject())
            }, requireContext())
            //orderObserver()
      }

      private fun observeApiChanges() {
            //todo success data bind --
            viewModel.orderFilterWithPaging.observe(this) { items ->
                  binding.shimmerLayout.stopShimmer()
                  binding.rvPreviousOrders.visibility = View.VISIBLE
                  binding.shimmerLayout.visibility = View.INVISIBLE
                  // binding.swipeRefresh.setRefreshing(false)
                  if (items.isEmpty() && pageNo == 1) {
                        pendingOrderPagingAdapter.clearAllData()
                        binding.ivNoDataFound.visibility = View.VISIBLE
                  } else if (pageNo == 1 && items.isNotEmpty()) {
                        pendingOrderPagingAdapter.clearAllData()
                        binding.ivNoDataFound.visibility = View.GONE
                  } else {
                        binding.ivNoDataFound.visibility = View.GONE
                  }
                  pendingOrderPagingAdapter.setItems(items)
                  //  binding.spinKitLoader!!.visibility = View.GONE
            }

            //todo loading --
            viewModel.loadingOrderFilterWithPaging.observe(this) { loading ->
                  if (loading) {
                        binding.shimmerLayout.startShimmer()
                        binding.spinKitLoader!!.visibility = View.VISIBLE
                  } else {
                        binding.shimmerLayout.stopShimmer()
                        binding.spinKitLoader!!.visibility = View.GONE
                  }

            }

            //todo show error--
            viewModel.errorOrderFilterWithPaging.observe(this) { error ->
                  binding.shimmerLayout.stopShimmer()
                  // binding.swipeRefresh.setRefreshing(false)
                  binding.spinKitLoader!!.visibility = View.GONE

                  if (error != null) {
                        Toast.makeText(requireContext(), error!!, Toast.LENGTH_SHORT).show()
                        // Show error message
                  }
            }
      }

      override fun onResume() {
            super.onResume()
            setupRecyclerViewInitial()
            setUpRecyclerViewPaging()
            if (Globals.checkForInternet(requireContext())) {
                  setupRequestForItemList()
                  observeApiChanges()
            }
      }

}