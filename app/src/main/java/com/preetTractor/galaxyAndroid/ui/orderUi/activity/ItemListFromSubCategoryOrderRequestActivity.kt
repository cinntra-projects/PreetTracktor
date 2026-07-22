package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.ActivityItemListFromSubCategoryOrderRequestBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider

import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.ItemListFromSubCategoryOrderRequestPagingAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ItemListFromSubCategoryOrderRequestActivity : AppCompatActivity() {
      lateinit var binding: ActivityItemListFromSubCategoryOrderRequestBinding
      lateinit var viewModel: MainViewModel
      var pageNo = 1
      //lateinit var sessionManagement: SessionManagement
      var searchTextValue = ""
      lateinit var layoutManager: LinearLayoutManager
      lateinit var itemListingFromSubCategoryPagingAdapter: ItemListFromSubCategoryOrderRequestPagingAdapter
      var isLoading = false
      var islastPage = false
      var isScrollingpage = false
      var subCatId = ""

      private fun setUpViewModel() {
            val dispatchers: CoroutineDispatcher = Dispatchers.Main
            val mainRepos = DefaultMainRepositories() as MainRepos
            val fanxApi: ApisInterface = ApiClient().service(this)
            val viewModelProviderfactory =
                  MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
            viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

      }


      private fun setupRecyclerViewInitial() {
            //todo bind adapter here--
            binding!!.rvDispatchOrder.layoutManager = layoutManager
            binding!!.rvDispatchOrder.adapter = itemListingFromSubCategoryPagingAdapter
      }

      private fun observeApiChanges() {
            //todo success data bind --
            viewModel.itemListFromSubcategoryOrderRequesteWithPaging.observe(this) { items ->
                  binding.shimmerLayout.stopShimmer()
                  binding!!.rvDispatchOrder.visibility = View.VISIBLE
                  binding!!.shimmerLayout.visibility = View.INVISIBLE
                  // binding.swipeRefresh.setRefreshing(false)
                  if (items.isEmpty() && pageNo == 1) {
                        itemListingFromSubCategoryPagingAdapter.clearAllData()
                        binding!!.ivNoDataFound.visibility = View.VISIBLE
                  } else if (pageNo == 1 && items.isNotEmpty()) {
                        itemListingFromSubCategoryPagingAdapter.clearAllData()
                        binding!!.ivNoDataFound.visibility = View.GONE
                  } else {
                        binding!!.ivNoDataFound.visibility = View.GONE
                  }
                  itemListingFromSubCategoryPagingAdapter.setItems(items)
                  //  binding!!.spinKitLoader!!.visibility = View.GONE
            }

            //todo loading --
            viewModel.loadingItemListFromSubcategoryOrderRequesteWithPaging.observe(this) { loading ->
                  if (loading) {
                        binding.shimmerLayout.startShimmer()
                        binding!!.spinKitLoader!!.visibility = View.VISIBLE
                  } else {
                        binding.shimmerLayout.stopShimmer()
                        binding!!.spinKitLoader!!.visibility = View.GONE
                  }

            }

            //todo show error--
            viewModel.errorItemListFromSubcategoryOrderRequesteWithPaging.observe(this) { error ->
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
            binding.rvDispatchOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            viewModel.getAllItemListFromSubCategoryOrderRequest(JsonObject().apply {
//                  addProperty(APiPayloadKeys.MaxItem, 20)
//                  addProperty(APiPayloadKeys.PageNo, pageNo)
                  addProperty(APiPayloadKeys.CatID, subCatId)
//                  addProperty(APiPayloadKeys.Search, searchTextValue)

            }, this)

      }

      private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                  CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        if (Globals.checkForInternet(this@ItemListFromSubCategoryOrderRequestActivity)) {

                              searchTextValue = s.toString()
                              viewModel.getAllItemListFromSubCategoryOrderRequest(JsonObject().apply {
//                                    addProperty(APiPayloadKeys.MaxItem, 20)
//                                    addProperty(APiPayloadKeys.PageNo, pageNo)
                                    addProperty(APiPayloadKeys.CatID, subCatId)
//                                    addProperty(APiPayloadKeys.Search, searchTextValue)

                              }, this@ItemListFromSubCategoryOrderRequestActivity)


                        }
                  }
            }

            override fun afterTextChanged(s: Editable?) {}
      }


      private fun bindEditText() {
            binding.edtSearch.addTextChangedListener(textWatcher)
      }

      override fun onDestroy() {
            super.onDestroy()
            // Remove the TextWatcher to avoid memory leaks
            binding.edtSearch.removeTextChangedListener(textWatcher)
      }


      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityItemListFromSubCategoryOrderRequestBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setUpViewModel()
            itemListingFromSubCategoryPagingAdapter =
                  ItemListFromSubCategoryOrderRequestPagingAdapter(binding.tvCartCounter)
            // supportActionBar?.hide()!!
            subCatId = intent.getStringExtra("id").toString()

            Log.d("sdkjbsb", "onCreate: $subCatId")
            binding.apply {
                  tvTitle.text = subCatId
                  ibBack.setOnClickListener {
                        finish()
                  }
                  ibSearch.setOnClickListener {
                        if (Globals.checkForInternet(this@ItemListFromSubCategoryOrderRequestActivity)) {
                              Toast.makeText(this@ItemListFromSubCategoryOrderRequestActivity, "Searching", Toast.LENGTH_SHORT).show()

                              searchTextValue = ""
                              viewModel.getAllItemListFromSubCategoryOrderRequest(JsonObject().apply {
//                                    addProperty(APiPayloadKeys.MaxItem, 20)
//                                    addProperty(APiPayloadKeys.PageNo, pageNo)
                                    addProperty(APiPayloadKeys.CatID, subCatId)
//                                    addProperty(APiPayloadKeys.Search, searchTextValue)

                              }, this@ItemListFromSubCategoryOrderRequestActivity)


                        }
                  }

            }
            bindEditText()
            //sessionManagement = SessionManagement(this)
            layoutManager = LinearLayoutManager(this)



            binding.btnProceedToBuy.setOnClickListener {

                  if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                        binding.tvCartCounter.text = AppConstants.cartListForOrderRequest.size.toString()
                        Intent(this, CartActivity::class.java).also {

                              startActivity(it)
                        }
                  } else {
                        Globals.warningMessage(this, "Cart is Empty")
                  }

            }


            itemListingFromSubCategoryPagingAdapter.setOnItemClickListener { data, i ->


            }



            binding.searchView.setOnClickListener {
                  if (binding.linearSearch.visibility == View.GONE) {
                        binding.linearSearch.visibility = View.VISIBLE
                  } else {
                        binding.linearSearch.visibility = View.GONE
                  }
            }


      }


      override fun onResume() {
            super.onResume()

            pageNo = 1
            setupRecyclerViewInitial()
            setUpRecyclerViewPaging()





            if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                  // binding.tvCartCounter.visibility = View.VISIBLE
                  binding.tvCartCounter.text = AppConstants.cartListForOrderRequest.size.toString()

            } else {
                  //  binding.tvCartCounter.visibility = View.INVISIBLE
                  binding.tvCartCounter.text = AppConstants.cartListForOrderRequest.size.toString()
            }

            binding.ivCollapseCart.setOnClickListener {

                  if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                        binding.tvCartCounter.text = AppConstants.cartListForOrderRequest.size.toString()
                        Intent(this, CartActivity::class.java).also {

                              startActivity(it)
                        }
                  } else {
                        Globals.warningMessage(this, "Cart is Empty")
                  }

            }



            if (Globals.checkForInternet(this)) {
                  setupRequestForItemList()
                  observeApiChanges()
            }
      }
}