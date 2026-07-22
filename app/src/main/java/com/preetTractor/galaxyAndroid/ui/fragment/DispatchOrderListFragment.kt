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
import com.preetTractor.galaxyAndroid.databinding.FragmentDispatchOrderListBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.OrderDetailsActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.PendingOrderPagingAdapter
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs

class DispatchOrderListFragment : Fragment() {
    lateinit var binding: FragmentDispatchOrderListBinding
    lateinit var viewModel: MainViewModel
    var pageNo = 1
    var isScrollingpage = false
    lateinit var layoutManager: LinearLayoutManager
    var pendingOrderPagingAdapter = PendingOrderPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDispatchOrderListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as CustomerDetailActivity).viewModel
        initViews()
        clickListeners()
    }

    private fun initViews() {
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun clickListeners() {
        pendingOrderPagingAdapter.setOnItemClickListener { data, pos ->
            val i = Intent(requireContext(), OrderDetailsActivity::class.java)
            i.putExtra("data", data)
            i.putExtra("where", "under")
            startActivity(i)
        }

        /*pendingOrderPagingAdapter.setOnItemEditDeleteClickListener { dataSoRequestAllFilter, i ->
            Intent(requireContext(), UpdateCartActivity::class.java).also {
                  it.putExtra("where", "update")
                  it.putExtra("ID",dataSoRequestAllFilter.id)
                  startActivity(it)
            }
      }

      pendingOrderPagingAdapter.setOnItemDeleteClickListener { dataSoRequestAllFilter, i ->
            Globals.showAlertDialog(
                  requireContext(),
                  "Delete Request",
                  "Are you sure you want to delete?",
                  "Delete",
                  "Cancel",
                  onDelete = {
                        viewModel.requestOrderDeleteApi(JsonObject().apply {
                              addProperty(APiPayloadKeys.id, dataSoRequestAllFilter.id)
                        }, requireContext())
                        //subscribeToDeleteObserver()
                  },
                  onCancel = {
                  },
                  iconImg = R.drawable.ic_delete
            )
      }*/
    }

    private fun setupRecyclerViewInitial() {
        //todo bind adapter here--
        binding!!.rvPendingSOOrder.layoutManager = layoutManager
        binding!!.rvPendingSOOrder.adapter = pendingOrderPagingAdapter
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

        viewModel.getOrderDispatchListingAllFilter(JsonObject().apply {
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

    private fun orderObserver() {
        viewModel.orderListResponse.observe(this, Event.EventObserver(
            onError = {
                //alertDialog!!.dismiss()
                binding.spinKitLoader.visibility = View.GONE
                Globals.warningMessage(requireContext(), it)
            }, onLoading = {
                //alertDialog!!.show()
                binding.spinKitLoader.visibility = View.VISIBLE
            }, { response ->
                //alertDialog!!.dismiss()

                if (response.status == 200) {
                    binding.spinKitLoader.visibility = View.GONE
                    binding.shimmerLayout.stopShimmer()
                    binding.rvPendingSOOrder.visibility = View.VISIBLE
                    binding.shimmerLayout.visibility = View.INVISIBLE
                    //todo set dealer, special and additional discount
                    if (response.data.isNotEmpty()) {

                        pendingOrderPagingAdapter.setItems(response.data)

                    } else {
                        binding.ivNoDataFound.visibility = View.VISIBLE
                    }
                } else if (response.status == 201) {
                    response.message.let { Globals.warningMessage(requireContext(), it) }
                } else if (response.status == 401) {
                    //sessionManagement.ClearSession()
                    PrefsByShubh.ClearSession()
                    Globals.logoutScreen(requireContext())
                }
            })
        )
    }

    private fun observeApiChanges() {
        //todo success data bind --
        viewModel.orderFilterWithPaging.observe(this) { items ->
            binding.shimmerLayout.stopShimmer()
            binding!!.rvPendingSOOrder.visibility = View.VISIBLE
            binding!!.shimmerLayout.visibility = View.INVISIBLE
            // binding.swipeRefresh.setRefreshing(false)
            if (items.isEmpty() && pageNo == 1) {
                pendingOrderPagingAdapter.clearAllData()
                binding!!.ivNoDataFound.visibility = View.VISIBLE
            } else if (pageNo == 1 && items.isNotEmpty()) {
                pendingOrderPagingAdapter.clearAllData()
                binding!!.ivNoDataFound.visibility = View.GONE
            } else {
                binding!!.ivNoDataFound.visibility = View.GONE
            }
            pendingOrderPagingAdapter.setItems(items)
            //  binding!!.spinKitLoader!!.visibility = View.GONE
        }

        //todo loading --
        viewModel.loadingOrderFilterWithPaging.observe(this) { loading ->
            if (loading) {
                binding.shimmerLayout.startShimmer()
                binding!!.spinKitLoader!!.visibility = View.VISIBLE
            } else {
                binding.shimmerLayout.stopShimmer()
                binding!!.spinKitLoader!!.visibility = View.GONE
            }

        }

        //todo show error--
        viewModel.errorOrderFilterWithPaging.observe(this) { error ->
            binding.shimmerLayout.stopShimmer()
            // binding.swipeRefresh.setRefreshing(false)
            binding!!.spinKitLoader!!.visibility = View.GONE

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