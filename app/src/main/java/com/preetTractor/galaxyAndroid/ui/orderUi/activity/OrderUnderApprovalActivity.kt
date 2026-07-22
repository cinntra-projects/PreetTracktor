package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import Event
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.databinding.ActivityOrderUnderApprovalBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.OrderUnderApprovalPagingAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class OrderUnderApprovalActivity : AppCompatActivity() {
      lateinit var binding: ActivityOrderUnderApprovalBinding

      lateinit var viewModel: MainViewModel
      var pageNo = 1
      //lateinit var sessionManagement: SessionManagement


      var searchTextValue = ""
      lateinit var layoutManager: LinearLayoutManager
      var orderUnderApprovalPagingAdapter = OrderUnderApprovalPagingAdapter()

      var isLoading = false
      var islastPage = false
      var isScrollingpage = false

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
            binding = ActivityOrderUnderApprovalBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setUpViewModel()
            //  supportActionBar?.hide()
            //sessionManagement = SessionManagement(this)
            layoutManager = LinearLayoutManager(this)
            binding.tvCardName.text = PrefsByShubh.getCardName()

            binding.ibBack.setOnClickListener {
                  finish()
            }

            orderUnderApprovalPagingAdapter.setOnItemClickListener { data, pos ->
                  val i = Intent(this, OrderUnderApprovalOneDetailActivity::class.java)
                  i.putExtra("ID", "" + data.id)
                  i.putExtra("where", "under")
                  startActivity(i)


            }

            orderUnderApprovalPagingAdapter.setOnItemEditDeleteClickListener { dataSoRequestAllFilter, i ->

                  Intent(this, UpdateCartActivity::class.java).also {
                        it.putExtra("where", "update")
                        it.putExtra("ID",dataSoRequestAllFilter.id)
                        startActivity(it)
                  }


            }


            orderUnderApprovalPagingAdapter.setOnItemDeleteClickListener { dataSoRequestAllFilter, i ->
                  Globals.showAlertDialog(
                        this,
                        "Delete Request",
                        "Are you sure you want to delete?",
                        "Delete",
                        "Cancel",
                        onDelete = {
                              viewModel.requestOrderDeleteApi(JsonObject().apply {
                                    addProperty(APiPayloadKeys.id, dataSoRequestAllFilter.id)
                              }, this)
                              subscribeToDeleteObserver()


                        },
                        onCancel = {


                        },
                        iconImg = R.drawable.ic_delete
                  )


            }




      }


      override fun onResume() {
            super.onResume()
            setupRecyclerViewInitial()
            setUpRecyclerViewPaging()
            if (Globals.checkForInternet(this)) {
                  setupRequestForItemList()
                  observeApiChanges()
            }
      }

      private fun subscribeToDeleteObserver() {
            viewModel.requestOrderDeleteData.observe(this, Event.EventObserver(onError = {
                  Globals.warningMessage(this, it)
            }, onLoading = {

            }, { response ->
                  if (response.status==200) {
                        Toast.makeText(
                              this,
                              "Deleted Successfully",
                              Toast.LENGTH_SHORT
                        ).show()
                        setupRequestForItemList()
                  } else if (response.status==201) {
                        Globals.warningMessage(this, response.message)
                  }


            }))
      }


      private fun setupRecyclerViewInitial() {
            //todo bind adapter here--
            binding!!.rvPendingSOOrder.layoutManager = layoutManager
            binding!!.rvPendingSOOrder.adapter = orderUnderApprovalPagingAdapter
      }

      private fun observeApiChanges() {
            //todo success data bind --
            viewModel.soRequestFIlterWithPaging.observe(this) { items ->
                  binding.shimmerLayout.stopShimmer()
                  binding!!.rvPendingSOOrder.visibility = View.VISIBLE
                  binding!!.shimmerLayout.visibility = View.INVISIBLE
                  // binding.swipeRefresh.setRefreshing(false)
                  if (items.isEmpty() && pageNo == 1) {
                        orderUnderApprovalPagingAdapter.clearAllData()
                        binding!!.ivNoDataFound.visibility = View.VISIBLE
                  } else if (pageNo == 1 && items.isNotEmpty()) {
                        orderUnderApprovalPagingAdapter.clearAllData()
                        binding!!.ivNoDataFound.visibility = View.GONE
                  } else {
                        binding!!.ivNoDataFound.visibility = View.GONE
                  }
                  orderUnderApprovalPagingAdapter.setItems(items)
                  //  binding!!.spinKitLoader!!.visibility = View.GONE
            }

            //todo loading --
            viewModel.loadingsoRequestFIlterWithPaging.observe(this) { loading ->
                  if (loading) {
                        binding.shimmerLayout.startShimmer()
                        binding!!.spinKitLoader!!.visibility = View.VISIBLE
                  } else {
                        binding.shimmerLayout.stopShimmer()
                        binding!!.spinKitLoader!!.visibility = View.GONE
                  }

            }

            //todo show error--
            viewModel.errorsoRequestFIlterPaymentsWithPaging.observe(this) { error ->
                  binding.shimmerLayout.stopShimmer()
                  // binding.swipeRefresh.setRefreshing(false)
                  binding!!.spinKitLoader!!.visibility = View.GONE

                  if (error != null) {
                        Toast.makeText(this, error!!, Toast.LENGTH_SHORT).show()
                        // Show error message
                  }
            }
      }

      private fun setUpRecyclerViewPaging() {
            //todo set recyclerview add scroll page
            binding.rvPendingSOOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

            viewModel.getSoRequestAllFilter(JsonObject().apply {
                  addProperty(APiPayloadKeys.MaxSize, 10)
                  addProperty(APiPayloadKeys.PageNo, pageNo)
                  addProperty(APiPayloadKeys.Status, "Pending")
                  addProperty(APiPayloadKeys.SalesEmployeeCode, PrefsByShubh.getSalesEmployeeCode())

            }, this)

      }
}