package com.preetTractor.galaxyAndroid.ui.orderUi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.FragmentDispatchedOrderBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForDispatch
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseDispatchList

import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.ContentAdapter
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.DispatchListPagingAdapter
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.FixedColumnDispatchedAdapter
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.ScrollableContentDispatchAdapter
import com.preetTractor.galaxyAndroid.utils.SynchronizedRecyclerView
import com.preetTractor.galaxyAndroid.utils.WebViewBottomSheetFragment

class DispatchedOrderFragment : Fragment() {

      lateinit var binding: FragmentDispatchedOrderBinding
      lateinit var viewModel: MainViewModel
      var pageNo = 1
      //lateinit var sessionManagement: SessionManagement


      var searchTextValue = ""
      lateinit var linearlayoutDispatchManager: LinearLayoutManager
      var itemListingFromSubCategoryPagingAdapter = DispatchListPagingAdapter()

      var isLoading = false
      var islastPage = false
      var isScrollingpage = false
      lateinit var dialogWeb: WebView

      lateinit var fixedColumnDispatchedAdapter: FixedColumnDispatchedAdapter
      var scrollableContentDispatchAdapter: ScrollableContentDispatchAdapter? = null
      var fixedColumnList: MutableList<LocalDataForDispatch> = mutableListOf()
      var contentScrollabledata: MutableList<List<String>> = mutableListOf()
      var contentAdapter: ContentAdapter? = null
      lateinit var builder: AlertDialog.Builder
      lateinit var alertDialog: AlertDialog


      private fun SetUPDialog() {
            builder = AlertDialog.Builder(requireContext())
            builder.setView(R.layout.progress_dialog).setCancelable(false)
            alertDialog = builder!!.create()
      }


      fun transformDataList(dataList: List<ResponseDispatchList.Data>): MutableList<List<String>> {
            return dataList.map { data ->
                  val transportName =
                        if (data.U_TransporterName.isNullOrEmpty()) "--" else data.U_TransporterName
                  listOf("₹ "+ Globals.numberToK(data.NetTotal)!!, data.DocEntry, transportName, "--")
            }.toMutableList()
      }


      fun transformFixedColumnDataList(dataList: List<ResponseDispatchList.Data>): MutableList<LocalDataForDispatch> {


            return dataList.map { data ->
                  val date = Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(data.DocDate)
                  var localDataForDispatch = LocalDataForDispatch(date!!, data.DocEntry)
                  localDataForDispatch
            }.toMutableList()
      }

      private fun setupRecyclerViewInitial() {
            fixedColumnDispatchedAdapter = FixedColumnDispatchedAdapter()

            //todo bind adapter here--
            binding!!.fixedColumnRecyclerView.layoutManager = linearlayoutDispatchManager
            binding!!.fixedColumnRecyclerView.adapter = fixedColumnDispatchedAdapter
            binding!!.fixedColumnRecyclerView.synchronizedRecyclerView =
                  binding.contentRecyclerView as SynchronizedRecyclerView


      }

      private fun observeApiChanges() {
            //todo success data bind --
            try {
                  viewModel.dispatchListWithPaging.observe(viewLifecycleOwner) { items ->
                        alertDialog.dismiss()
                        //  binding.shimmerLayout.stopShimmer()

                        // binding.swipeRefresh.setRefreshing(false)
                        if (items.isEmpty() && pageNo == 1) {
                              itemListingFromSubCategoryPagingAdapter.clearAllData()
                              fixedColumnDispatchedAdapter.clearAllData()
                              scrollableContentDispatchAdapter!!.clearAllData()
                              fixedColumnList.clear()
                              contentScrollabledata.clear()


                              // binding!!.ivNoDataFound.visibility = View.VISIBLE
                        } else if (pageNo == 1 && items.isNotEmpty()) {
                              itemListingFromSubCategoryPagingAdapter.clearAllData()
                              fixedColumnDispatchedAdapter.clearAllData()
                              scrollableContentDispatchAdapter!!.clearAllData()
                              fixedColumnList.clear()
                              contentScrollabledata.clear()
                              fixedColumnList = transformFixedColumnDataList(items)
                              contentScrollabledata = transformDataList(items)
                              //todo add heading of Date/PDF ,amount,docentry,transport
                              fixedColumnList.add(0, LocalDataForDispatch("Date/PDF", ""))
                              contentScrollabledata.add(
                                    0,
                                    listOf(
                                          "Amount",
                                          "Invoice",
                                          "Transporter",
                                          "Lr.No."
                                    )

                              )

                              fixedColumnDispatchedAdapter.setItems(fixedColumnList)
                              scrollableContentDispatchAdapter!!.setItems(contentScrollabledata)


                        } else {
                              fixedColumnList.addAll(transformFixedColumnDataList(items))
                              contentScrollabledata.addAll(transformDataList(items))
                              fixedColumnDispatchedAdapter.setItems(fixedColumnList)
                              scrollableContentDispatchAdapter!!.setItems(contentScrollabledata)

                        }



                        itemListingFromSubCategoryPagingAdapter.setItems(items)









                        binding.contentRecyclerView.apply {
                              adapter = scrollableContentDispatchAdapter
                              synchronizedRecyclerView =
                                    binding.fixedColumnRecyclerView as SynchronizedRecyclerView
                        }
                        fixedColumnDispatchedAdapter.notifyDataSetChanged()
                        scrollableContentDispatchAdapter?.notifyDataSetChanged()

                  }
            } catch (e: Exception) {


            }

            //todo loading --
            try {
                  viewModel.loadingdispatchListWithPaging.observe(viewLifecycleOwner) { loading ->
                        if (loading) {
                              //  binding.shimmerLayout.startShimmer()
                              //  binding!!.spinKitLoader!!.visibility = View.VISIBLE
                              alertDialog.show()
                        } else {
                              alertDialog.dismiss()
                              //  binding.shimmerLayout.stopShimmer()
                              //   binding!!.spinKitLoader!!.visibility = View.GONE
                        }

                  }
            } catch (e: Exception) {
            }

            //todo show error--
            try {
                  viewModel.errordispatchListWithPaging.observe(viewLifecycleOwner) { error ->
                        // binding.shimmerLayout.stopShimmer()
                        // binding.swipeRefresh.setRefreshing(false)
                        //  binding!!.spinKitLoader!!.visibility = View.GONE
                        alertDialog.dismiss()
                        if (error != null) {
                              Toast.makeText(requireContext(), error!!, Toast.LENGTH_SHORT).show()
                              //  Log.e(TAG, "observeApiChanges: $error")
                              // Show error message
                        }
                  }
            } catch (e: Exception) {
            }
      }

      /*   private fun setUpRecyclerViewPaging() {
             //todo set recyclerview add scroll page
             binding.fixedColumnRecyclerView.addOnScrollListener(object :
                 RecyclerView.OnScrollListener() {
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

         }*/

      private fun setupRequestForItemList() {
            viewModel.getDispatchListPagingApi(JsonObject().apply {
                  addProperty(APiPayloadKeys.MaxSize, "All")
                  addProperty(APiPayloadKeys.PageNo, pageNo)
                  addProperty(APiPayloadKeys.CardCode, PrefsByShubh.getCardCode()!! /*sessionManagement.getCardCode()*/)
                  addProperty(APiPayloadKeys.FromDate, Globals.firstDateOfFinancialYear())
                  addProperty(APiPayloadKeys.ToDate, Globals.lastDateOfFinancialYear())
            }, requireActivity())

      }


      private fun setUpRecyclerViewPaging() {
            val scrollListener = object : RecyclerView.OnScrollListener() {
                  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        synchronizeScroll(recyclerView, dx, dy)
                        checkForPagination(recyclerView, dy)
                  }

                  override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                              isScrollingpage = true
                        }
                  }
            }

            binding.fixedColumnRecyclerView.addOnScrollListener(scrollListener)
            binding.contentRecyclerView.addOnScrollListener(scrollListener)
      }

      private fun synchronizeScroll(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (recyclerView === binding.fixedColumnRecyclerView) {
                  binding.contentRecyclerView.scrollBy(dx, dy)
            } else {
                  binding.fixedColumnRecyclerView.scrollBy(dx, dy)
            }
      }

      private fun checkForPagination(recyclerView: RecyclerView, dy: Int) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            if (isScrollingpage && dy > 0 && visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                  pageNo++
                  setupRequestForItemList()
                  isScrollingpage = false
            }
      }


      override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
      ): View? {
            // Inflate the layout for this fragment
            binding = FragmentDispatchedOrderBinding.inflate(layoutInflater)
            return binding.root
      }

      companion object {
            // private const val TAG = "PlaceOrderFragment"
      }

      var url = ""
      override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            viewModel = (activity as CustomerDetailActivity).viewModel
            //sessionManagement = SessionManagement(requireActivity())
            try {
                  linearlayoutDispatchManager = LinearLayoutManager(requireContext())
            } catch (e: Exception) {
            }
            dialogWeb = WebView(requireActivity())
            setupRecyclerViewInitial()
            //  setUpRecyclerViewPaging()

            //todo dialog setup
            SetUPDialog()

            fixedColumnDispatchedAdapter.setOnItemClickListener { data, i ->
                  if (data.DocEntry.equals("")) {

                  } else {
                        url = Globals.invoiceUrl + "id=" + data.DocEntry
                        shareLedgerData()
                  }


            }

            scrollableContentDispatchAdapter =
                  ScrollableContentDispatchAdapter()

            if (Globals.checkForInternet(requireActivity())) {
                  setupRequestForItemList()
                  observeApiChanges()
            }


      }

      var title = ""
      private fun shareLedgerData() {
            title = "Share"
            if (url != null) {
                  val addPhotoBottomDialogFragment: WebViewBottomSheetFragment =
                        WebViewBottomSheetFragment.newInstance(dialogWeb, url!!, title)
                  addPhotoBottomDialogFragment.show(childFragmentManager, "")
            }

      }
}