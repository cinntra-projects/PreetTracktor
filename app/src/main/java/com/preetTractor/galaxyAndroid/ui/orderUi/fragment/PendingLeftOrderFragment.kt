package com.preetTractor.galaxyAndroid.ui.orderUi.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.databinding.FragmentPendingOrderInnerSecondBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.orderUi.model.PendingByOrderModel
import com.preetTractor.galaxyAndroid.sessionManagement.SessionManagement
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.OrderPendingOneDetailActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.PendingOrderWiseActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.PendingItemByOrderAdapter


class PendingLeftOrderFragment : Fragment() {
    lateinit var binding: FragmentPendingOrderInnerSecondBinding
    lateinit var viewModel: MainViewModel
    lateinit var sessionManagement: SessionManagement
val args:PendingLeftOrderFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPendingOrderInnerSecondBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "PendingLeftOrderFragmen"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as PendingOrderWiseActivity).viewModel
        sessionManagement = SessionManagement(requireActivity())


        if (Globals.checkForInternet(requireActivity())) {
            viewModel.pendingByOrder(JsonObject().apply {
                addProperty(APiPayloadKeys.OrderID, args.pending)
            }, requireActivity())
            subscribeToObserver()
        }

    }


    private fun subscribeToObserver() {
        viewModel.pendingByOrderListData.observe(viewLifecycleOwner, Event.EventObserver(onError = {
           Globals.warningMessage(requireActivity(), it)
            binding.spinKitLoader.visibility = View.GONE
            binding.apply {
                shimmerLayout.stopShimmer()
            }

        }, onLoading = {
            binding.spinKitLoader.visibility = View.VISIBLE
            binding.apply {
                shimmerLayout.startShimmer()
            }

        }, { response ->
            binding.apply {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility=View.GONE
                rvDispatchOrder.visibility=View.VISIBLE
            }
            binding.spinKitLoader.visibility = View.GONE

            if (response.status.equals(200)) {
                if (response.data.isNotEmpty()) {
                    setupRecyclerview(response.data)
                }


            } else if (response.status.equals(201)) {
             Globals.warningMessage(
                    requireActivity(),
                    response.message
                )
            } else if (response.status == 401) {
                sessionManagement.ClearSession()
                Globals.logoutScreen(requireActivity())

            } else {
             Globals.warningMessage(
                    requireContext(),
                    response.message
                )
            }

        }))
    }


    private fun setupRecyclerview(data: List<PendingByOrderModel.Data>) {
        var adapter = PendingItemByOrderAdapter(requireActivity())
        /*  if (!data[0].U_UTL_ITMCT.equals("All")){
              data.add(0, CategoryItemResponseModel.Data("All"))
          }*/
        adapter.submitList(data)

        binding.rvDispatchOrder.adapter = adapter
        binding.rvDispatchOrder.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        adapter.notifyDataSetChanged()
        //todo calling subcategory
        adapter.setOnItemClickListener { data, pos ->
            val i = Intent(context, OrderPendingOneDetailActivity::class.java)

            i.putExtra("ID", "" + data.OrderID)

            startActivity(i)


        }


    }
}