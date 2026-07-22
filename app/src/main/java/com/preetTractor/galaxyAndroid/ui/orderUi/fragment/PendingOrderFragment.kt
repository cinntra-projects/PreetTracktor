package com.preetTractor.galaxyAndroid.ui.orderUi.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.FragmentPendingOrderBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.OrderUnderApprovalActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.PendingOrderWiseActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.DeliveryNotePendingOrderListPagingAdapter

class PendingOrderFragment : Fragment() {

      lateinit var binding: FragmentPendingOrderBinding
      lateinit var viewModel: MainViewModel
      private lateinit var navController: NavController

      var pageNo = 1
      //lateinit var sessionManagement: SessionManagement


      var searchTextValue = ""
      lateinit var layoutManager: LinearLayoutManager
      var itemListingFromSubCategoryPagingAdapter = DeliveryNotePendingOrderListPagingAdapter()

      var isLoading = false
      var islastPage = false
      var isScrollingpage = false
      var orderByAmt = ""
      var orderByName = ""
      var orderByQty = ""


      private var isQuantityFilterSelected: Boolean = false
      private var isAmountFilterSelected: Boolean = false


      private fun setupRecyclerViewInitial() {
            //todo bind adapter here--
            binding!!.rvPendingOrder.layoutManager = layoutManager
            binding!!.rvPendingOrder.adapter = itemListingFromSubCategoryPagingAdapter
      }

      private fun observeApiChanges() {
            //todo success data bind --
            viewModel.deliveryNotePendingListWithPaging.observe(viewLifecycleOwner) { items ->
                  binding.shimmerLayout.stopShimmer()
                  binding!!.rvPendingOrder.visibility = View.VISIBLE
                  binding!!.shimmerLayout.visibility = View.INVISIBLE

                  binding.spinKitLoader.visibility = View.GONE
                  // binding.swipeRefresh.setRefreshing(false)
                  if (items.isEmpty() && pageNo == 1) {
                        itemListingFromSubCategoryPagingAdapter.clearAllData()
                        binding!!.ivNoDataFound.visibility = View.VISIBLE
                  } else if (pageNo == 1 && items.isNotEmpty()) {
                        itemListingFromSubCategoryPagingAdapter.clearAllData()
                        binding!!.ivNoDataFound.visibility = View.INVISIBLE
                  } else {
                        binding!!.ivNoDataFound.visibility = View.INVISIBLE
                  }
                  itemListingFromSubCategoryPagingAdapter.setItems(items)
                  //  binding!!.spinKitLoader!!.visibility = View.GONE
            }

            //todo loading --
            viewModel.loadingdeliveryNotePendingListWithPaging.observe(viewLifecycleOwner) { loading ->
                  if (loading) {
                        binding.shimmerLayout.startShimmer()
                        binding!!.spinKitLoader!!.visibility = View.VISIBLE
                  } else {
                        binding.shimmerLayout.stopShimmer()
                        binding!!.spinKitLoader!!.visibility = View.GONE
                  }

            }

            //todo show error--
            viewModel.errordeliveryNotePendingListWithPaging.observe(viewLifecycleOwner) { error ->

                  binding.shimmerLayout.stopShimmer()
                  // binding.swipeRefresh.setRefreshing(false)
                  binding!!.spinKitLoader!!.visibility = View.GONE

                  if (error != null) {
                        Toast.makeText(requireContext(), error!!, Toast.LENGTH_SHORT).show()
                        // Show error message
                  }
            }
      }


      private fun setupRequestForItemList() {
            viewModel.getDeliveryNotePendingOrderItemAll(JsonObject().apply {
                  addProperty(APiPayloadKeys.MaxSize, 20)
                  addProperty(APiPayloadKeys.PageNo, pageNo)
                  addProperty(APiPayloadKeys.CardCode, PrefsByShubh.getCardCode() /*sessionManagement.getCardCode()*/)
                  addProperty(APiPayloadKeys.OrderByAmt, orderByAmt)
                  addProperty(APiPayloadKeys.OrderByName, orderByName)
                  addProperty(APiPayloadKeys.OrderByQty, orderByQty)
            }, requireActivity())

      }


      private fun setUpRecyclerViewPaging() {
            //todo set recyclerview add scroll page
            binding.rvPendingOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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


      override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
      ): View? {
            // Inflate the layout for this fragment
            binding = FragmentPendingOrderBinding.inflate(layoutInflater)
            return binding.root
      }

      companion object {
            // private const val TAG = "PlaceOrderFragment"
      }

      override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            viewModel = (activity as CustomerDetailActivity).viewModel
             //navController = requireActivity().findNavController(R.id.fragmentContainerCustomerActivity)
           /* val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerCustomerActivity) as? NavHostFragment
            if (navHostFragment == null) {
                  Log.e("NavHostFragment", "NavHostFragment is null. Check layout IDs.")
                  return
            }*/

            //val navController = navHostFragment.navController
            //sessionManagement = SessionManagement(requireActivity())
            layoutManager = LinearLayoutManager(requireActivity())
            setupRecyclerViewInitial()
            setUpRecyclerViewPaging()
            binding.pendingFilter.setOnClickListener {
                  showPopupMenu(binding.pendingFilter)

            }

            binding.headerAmount.setOnClickListener {
                  if (!isAmountFilterSelected) {
                        isAmountFilterSelected = true
                        orderByAmt = "desc"
                        //  orderByQty="desc"

                  } else {
                        isAmountFilterSelected = false
                        orderByAmt = "asc"
                        //  orderByQty="asc"
                  }
                  setupRequestForItemList()
            }

            binding.headerQuantity.setOnClickListener {
                  if (!isQuantityFilterSelected) {
                        isQuantityFilterSelected = true
                        orderByAmt = ""
                        orderByQty="desc"
                  } else {
                        isQuantityFilterSelected = false
                        orderByAmt = ""
                        orderByQty="asc"
                  }
                  setupRequestForItemList()
            }

            binding.btnOrderWise.setOnClickListener {
                  Intent(requireContext(), PendingOrderWiseActivity::class.java).also {
                        it.putExtra("itemcode","")
                      startActivity(it)
                  }

            }


            binding.btnUnderApproval.setOnClickListener {

                  startActivity(Intent(requireActivity(), OrderUnderApprovalActivity::class.java))
            }

            itemListingFromSubCategoryPagingAdapter.setOnItemClickListener { data, i ->
                  val bundle = Bundle().apply {
                        putString("data", data.ItemCode)
                  }

                  //findNavController().navigate(R.id.pendingOrderInnerFirstFragment, bundle)
            }

            if (Globals.checkForInternet(requireActivity())) {
                  setupRequestForItemList()
                  observeApiChanges()
            }
      }

      private fun showPopupMenu(view: View) {
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.filter_menu_pending_order, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                  when (menuItem.itemId) {
                        R.id.menuAToz -> {
                              // Handle A to Z action
                              orderByName = "a-z"
                              orderByAmt = ""
                              observeApiChanges()
                              setupRequestForItemList()
                              true
                        }
                        R.id.menuZtoA -> {
                              // Handle Z to A action
                              orderByName = "z-a"
                              orderByAmt = ""
                              observeApiChanges()
                              setupRequestForItemList()
                              true
                        }
                        R.id.menuAmountDesc -> {
                              // Handle Amount Desc action
                              orderByAmt = "desc"
                              orderByName = ""
                              observeApiChanges()
                              setupRequestForItemList()
                              true
                        }
                        R.id.menuAmountAsc -> {
                              // Handle Amount Asc action
                              orderByAmt = "asc"
                              orderByName = ""
                              observeApiChanges()
                              setupRequestForItemList()
                              true
                        }
                        R.id.menuAllFilter -> {
                              // Handle Clear All Filter action
                              orderByName = ""
                              orderByAmt = ""
                              observeApiChanges()
                              setupRequestForItemList()
                              true
                        }
                        else -> false
                  }
            }

            popupMenu.show()
      }
}