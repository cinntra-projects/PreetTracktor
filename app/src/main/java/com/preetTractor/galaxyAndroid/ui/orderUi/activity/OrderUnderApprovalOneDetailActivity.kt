package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.databinding.ActivityOrderUnderApprovalOneDetailBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.orderUi.model.OrderOneDetailModel
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.ItemPendingInOrderOneAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class OrderUnderApprovalOneDetailActivity : AppCompatActivity() {
      lateinit var binding: ActivityOrderUnderApprovalOneDetailBinding
      lateinit var viewModel: MainViewModel
      //lateinit var sessionManagement: SessionManagement
      var orderId = ""
      var where = ""

      lateinit var adapter: ItemPendingInOrderOneAdapter

      companion object {
            private const val TAG = "OrderPendingOneDetailAc"
      }


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
            binding = ActivityOrderUnderApprovalOneDetailBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setUpViewModel()
            // supportActionBar?.hide()
            //sessionManagement = SessionManagement(this)
            orderId = intent.getStringExtra("ID").toString()
            where = intent.getStringExtra("where").toString()
            binding.toolbarPendingList.back.setOnClickListener {
                  finish()
            }

            if (Globals.checkForInternet(this)) {
                  if (where.equals("under")) {
                        binding.apply {
                              toolbarPendingList.mainContainer.setBackgroundColor(Color.parseColor("#FFA726"))
                              linearCustomerDetails.setBackgroundResource(R.drawable.rounded_bottom_orange_color)


                              tvCustomerNameShubh.setTextColor(resources.getColor(R.color.white))
                              tvDateShubh.setTextColor(resources.getColor(R.color.white))
                              tvOrderHashShubh.setTextColor(resources.getColor(R.color.white))
                              tvRefHshShubh.setTextColor(resources.getColor(R.color.white))
                              tvUnderapproval.visibility = View.VISIBLE
                        }

                        viewModel.requestOrderOneApi(JsonObject().apply {
                              addProperty(APiPayloadKeys.id, orderId)
                        }, this)

                        subscribeToUnderapproveObserver()
                  } else {
                        binding.apply {
                              toolbarPendingList.mainContainer.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                              linearCustomerDetails.setBackgroundResource(R.drawable.rounded_bottom)
                              tvCustomerNameShubh.setTextColor(resources.getColor(R.color.black_text))
                              tvDateShubh.setTextColor(resources.getColor(R.color.black_text))
                              tvOrderHashShubh.setTextColor(resources.getColor(R.color.black_text))
                              tvRefHshShubh.setTextColor(resources.getColor(R.color.black_text))
                              tvUnderapproval.visibility = View.GONE
                        }

                        viewModel.orderOneApi(JsonObject().apply {
                              addProperty(APiPayloadKeys.id, orderId)
                        }, this)

                        subscribeToObserver()
                  }

            }


      }

      private fun subscribeToObserver() {
            viewModel.orderOneDetailData.observe(this, Event.EventObserver(onError = {
                  binding.shimmerLayout.stopShimmer()
                  binding.apply {
                        shimmerLayout.stopShimmer()
                        scrolView.visibility = View.VISIBLE
                        shimmerLayout.visibility = View.INVISIBLE
                  }

                 Globals.warningMessage(this, it)
                  Log.e(TAG, "subscribeToObserver: $it")
            }, onLoading = {
                  binding.shimmerLayout.startShimmer()
            }, { response ->

                  binding.apply {
                        shimmerLayout.stopShimmer()
                        scrolView.visibility = View.VISIBLE
                        shimmerLayout.visibility = View.GONE
                  }


                  if (response.status==200) {
                        if (response.data.isNotEmpty()) {
                              setupRecyclerview(response.data[0].DocumentLines)
                              setupData(response.data[0])
                        }


                  } else if (response.status==201) {
                       Globals.warningMessage(this, response.message)
                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                        PrefsByShubh.clear()
                       Globals.logoutScreen(this)

                  } else {
                       Globals.warningMessage(this, response.message)
                  }


            }))
      }


      private fun subscribeToUnderapproveObserver() {
            viewModel.requestOrderOneDetailData.observe(this, Event.EventObserver(onError = {
                  binding.shimmerLayout.stopShimmer()
                  binding.apply {
                        shimmerLayout.stopShimmer()
                        scrolView.visibility = View.VISIBLE
                        shimmerLayout.visibility = View.INVISIBLE
                  }

                 Globals.warningMessage(this, it)
            }, onLoading = {
                  binding.shimmerLayout.startShimmer()
            }, { response ->

                  binding.apply {
                        shimmerLayout.stopShimmer()
                        scrolView.visibility = View.VISIBLE
                        shimmerLayout.visibility = View.GONE
                  }


                  if (response.status==200) {
                        if (response.data.isNotEmpty()) {
                              setupRecyclerview(response.data[0].DocumentLines)
                              setupData(response.data[0])
                        }


                  } else if (response.status==201) {
                       Globals.warningMessage(this, response.message)
                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                        PrefsByShubh.clear()
                       Globals.logoutScreen(this)

                  } else {
                       Globals.warningMessage(this, response.message)
                  }


            }))
      }

      private fun setupData(one: OrderOneDetailModel.Data) {
            binding.toolbarPendingList.headTitle.text = "Voucher no: " + one.DocEntry
            binding.tvCustomerNameShubh.setText(one.CardCode + " " + one.CardName)
            binding.tvDateShubh.text = "" + Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(one.CreateDate)
            binding.tvOrderHashShubh.text = "Order #: $orderId,$orderId"
            binding.tvRefHshShubh.text = "Ref #: $orderId"
            binding.tvNetAmountTotalsShubh.setText(Globals.numberToK(one.NetTotal))

            if (one.Comments.isEmpty()) {
                  binding.tvNarationShubh.text = "N/A"
            } else {
                  binding.tvNarationShubh.setText(one.Comments)
            }


            binding.tvGrossTotalsShubh.setText(Globals.numberToK(one.GrossTotal))
            binding.tvFinalDestinationShubh.text = "N/A"
            binding.tvOrderTerms.setText(one.DeliveryMode)
            binding.tvDueDate.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(one.DocDueDate))
            binding.tvOrderRef.text = "No Reference"


      }

      private fun setupRecyclerview(documentLines: List<OrderOneDetailModel.DocumentLine>) {

            /*  if (!data[0].U_UTL_ITMCT.equals("All")){
                  data.add(0, CategoryItemResponseModel.Data("All"))
              }*/
            adapter = ItemPendingInOrderOneAdapter(
                  this,
                  documentLines as ArrayList<OrderOneDetailModel.DocumentLine>
            )
            //  adapter.submitList(documentLines)

            binding.rvBillsItemShubh.adapter = adapter
            binding.rvBillsItemShubh.layoutManager =
                  LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            adapter.notifyDataSetChanged()


      }
}