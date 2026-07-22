package com.preetTractor.galaxyAndroid.ui.orderUi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.FragmentPendingOrderInnerFirstBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.sessionManagement.SessionManagement
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.PendingOrderWiseActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.PendingOrderInnerFirstPagingAdapter

class PendingOrderInnerFirstFragment : Fragment() {
      lateinit var binding: FragmentPendingOrderInnerFirstBinding
      lateinit var viewModel: MainViewModel
      var pageNo = 1
      lateinit var sessionManagement: SessionManagement


      var searchTextValue = ""
      lateinit var layoutManager: LinearLayoutManager
      var itemListingFromSubCategoryPagingAdapter = PendingOrderInnerFirstPagingAdapter()

      var isLoading = false
      var islastPage = false
      var isScrollingpage = false
      //val args: PendingOrderInnerFirstFragmentArgs by navArgs()


      private fun setupRecyclerViewInitial() {
            //todo bind adapter here--
            binding!!.rvDispatchOrder.layoutManager = layoutManager
            binding!!.rvDispatchOrder.adapter = itemListingFromSubCategoryPagingAdapter
      }

      private fun observeApiChanges() {
            //todo success data bind --
            viewModel.deliveryNotePendingWiseWithPaging.observe(viewLifecycleOwner) { items ->
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
            viewModel.loadingdeliveryNotePendingWiseWithPaging.observe(viewLifecycleOwner) { loading ->
                  if (loading) {
                        binding.shimmerLayout.startShimmer()
                        binding!!.spinKitLoader!!.visibility = View.VISIBLE
                  } else {
                        binding.shimmerLayout.stopShimmer()
                        binding!!.spinKitLoader!!.visibility = View.GONE
                  }

            }

            //todo show error--
            viewModel.errordeliveryNotePendingWiseWithPagingWithPaging.observe(viewLifecycleOwner) { error ->
                  binding.shimmerLayout.stopShimmer()
                  // binding.swipeRefresh.setRefreshing(false)
                  binding!!.spinKitLoader!!.visibility = View.GONE

                  if (error != null) {
                        Toast.makeText(requireContext(), error!!, Toast.LENGTH_SHORT).show()
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

      var itemCode = ""

      private fun setupRequestForItemList() {
            //itemCode=args.data
            viewModel.getDeliveryNotePendingOrderWiseInner(JsonObject().apply {
                  addProperty(APiPayloadKeys.MaxSize, "All")
                  addProperty(APiPayloadKeys.PageNo, pageNo)
                  addProperty(APiPayloadKeys.SearchText, "")
                  addProperty(APiPayloadKeys.OrderByName, "")
                  addProperty(APiPayloadKeys.OrderByAmt, "")
                  addProperty(APiPayloadKeys.ItemCode, itemCode)
                  addProperty(APiPayloadKeys.CardCode, sessionManagement.getCardCode())
                  addProperty(APiPayloadKeys.FromDate, Globals.firstDateOfFinancialYear())
                  addProperty(APiPayloadKeys.ToDate, Globals.lastDateOfFinancialYear())
            }, requireActivity())

      }

      override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
      ): View? {
            // Inflate the layout for this fragment

            binding = FragmentPendingOrderInnerFirstBinding.inflate(layoutInflater)
            return binding.root
      }

      companion object {
            private const val TAG = "PendingOrderInnerFirstF"
      }


      override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
           viewModel = (activity as PendingOrderWiseActivity).viewModel

            sessionManagement = SessionManagement(requireActivity())
            layoutManager = LinearLayoutManager(requireActivity())
            itemCode=requireActivity().intent.getStringExtra("itemcode").toString()

            setupRecyclerViewInitial()
            setUpRecyclerViewPaging()
            if (Globals.checkForInternet(requireActivity())) {
                  setupRequestForItemList()
                  observeApiChanges()
            }

            itemListingFromSubCategoryPagingAdapter.setOnItemClickListener { data, i ->
                  val bundle = Bundle().apply {
                        putString("pending", data.id)
                  }
                  findNavController().navigate(R.id.pendingLeftOrderFragment, bundle)
            }


      }
}