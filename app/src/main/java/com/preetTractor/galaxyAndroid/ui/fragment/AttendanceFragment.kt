package com.preetTractor.galaxyAndroid.ui.fragment

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.DataOuterAttendanceListing
import com.preetTractor.galaxyAndroid.data.ResponseOuterAttendanceListing
import com.preetTractor.galaxyAndroid.databinding.FragmentAttendanceBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setTint
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.recyclerview.AttendanceUserOuterListingAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AttendanceFragment : Fragment() {
    lateinit var binding: FragmentAttendanceBinding
    private lateinit var adapter: AttendanceUserOuterListingAdapter
    private var allItemList = ArrayList<DataOuterAttendanceListing>()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var fromDateStr: String
    private lateinit var toDateStr: String
    var salesEmpoyeeCode = ""
    var salesEmpoyeeName = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendanceBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "AttendanceFragment"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (requireActivity().intent.hasExtra(Constant.WHERE_INTENT_VALUE_SALES)) {
            salesEmpoyeeCode =
                requireActivity().intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES)
                    .toString()
            salesEmpoyeeName =
                requireActivity().intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES_NAME)
                    .toString()

        } else {

        }


        getListing()
        onCliked()


    }

    private fun onCliked() {
        binding.inputFromDate.setText(Globals.getFirstDateofMonth())
        binding.inputToDate.setText(Globals.getTodaysDate())

        binding.inputFromDate.setOnClickListener {
            Globals.openDatePicker(binding.tvDate) { formattedDate ->
                // Set the formatted date (yyyy-MM-dd) in the EditText
                fromDate = formattedDate
                binding.inputFromDate.setText(
                    Globals.convert_yyyy_MM_dd_into_dd_MM_yyyy(
                        formattedDate
                    )
                )
                getListing()
            }
        }


        binding.inputToDate.setOnClickListener {
            Globals.openDatePicker(binding.tvDate) { formattedDate ->
                // Set the formatted date (yyyy-MM-dd) in the EditText
                toDate = formattedDate
                binding.inputToDate.setText(Globals.convert_yyyy_MM_dd_into_dd_MM_yyyy(formattedDate))
                getListing()
            }
        }

        /*   binding.inputFromDate.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", Date())
           binding.inputToDate.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", Date())
           Log.e(TAG, "DAte ${binding.inputFromDate.text}")
           binding.tvFromDate.setOnClickListener {
               Log.e(
                   TAG,
                   "DAte ${
                       dateStringConvertToDesiredFormat(
                           binding.inputFromDate.text.toString(),"dd/MM/yyyy",
                           "yyyy-MM-dd"
                       )
                   }"
               )
           }*/


    }

    var fromDate: String =
        Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(Globals.getFirstDateofMonth()!!)
    var toDate: String = Globals.getTodaysDatervrsfrmt()!!

    private fun getListing() {
        binding.progressBar2.visibility = View.VISIBLE
        val hde = JsonObject()

        if (PrefsByShubh.getString(Constant.FLAG, "").equals("_FROM_ATTENDANCE")) {
            Log.e(TAG, "getListing: _FROM_ATTENDANCE")
            hde.apply {
                addProperty(
                    "SalesEmployeeCode", PrefsByShubh.getString(Constant.SALESEMPLOYEECODE, "")
                )
                addProperty("From_Date", fromDate)
                addProperty("To_Date", toDate)
            }
        } else {
            Log.e(TAG, "getListing: NOT_FROM_ATTENDANCE")
            hde.apply {
                addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
                addProperty("From_Date", fromDate)
                addProperty("To_Date", toDate)
            }
        }

        val call = RetrofitClient.apiService.galaxyOuterattendanceList(hde)

        call.enqueue(object : Callback<ResponseOuterAttendanceListing> {
            override fun onResponse(
                call: Call<ResponseOuterAttendanceListing>,
                response: Response<ResponseOuterAttendanceListing>
            ) {
                binding.progressBar2.visibility = View.GONE
                response.body()?.let {
                    if (it.status.equals("200", ignoreCase = true)) {

                        if (it.data.isEmpty()) {
                            allItemList.clear()

                            binding.noDataFoundLayout.ivNoDataFound.visibility = View.VISIBLE
                        } else {
                            binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
                            allItemList.clear()
                            allItemList.addAll(it.data)
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter =
                                AttendanceUserOuterListingAdapter(allItemList, requireContext())

                            binding.rvOuterList.layoutManager = layoutManager
                            binding.rvOuterList.adapter = adapter
                            adapter.notifyDataSetChanged()
                            adapter.setOnItemClickListener { dataOuterAttendanceListing, i ->
                                val bundle = Bundle().apply {
                                    putSerializable("data", dataOuterAttendanceListing)
                                }
                                Log.e(TAG, "onResponseBackground: ${"sudhir"}")
                                findNavController().navigate(R.id.attendanceInnerFragment, bundle)
                            }

                            adapter.setOnItemViewsClickListener { _url, _msg ->
                                showPopup(_url, _msg)
                                Log.e(TAG, "On Clicked Image url: $_url")
                                Log.e(TAG, "On Clicked Image msg : $_msg")
                            }

                        }

                    }
                }
            }

            override fun onFailure(call: Call<ResponseOuterAttendanceListing>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                binding.progressBar2.visibility = View.GONE
                binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
            }
        })


    }

    private fun showPopup(_url: String, _msg: String) {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.custom_dialogs)

        val closeButton = dialog.findViewById<ImageButton>(R.id.ivClose)
        val tvLocation = dialog.findViewById<TextView>(R.id.tvLocation)
        val ivLocation = dialog.findViewById<ImageView>(R.id.iv_location)
        val ivUser = dialog.findViewById<ImageView>(R.id.iv_User)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar2)

        // Show progress bar initially
        progressBar.visibility = View.VISIBLE

        tvLocation.text = _url
        ivLocation.setTint(R.color.red)

        if (_msg.contains("ADDRESS", ignoreCase = true)) {
            tvLocation.visibility = View.VISIBLE
            ivLocation.visibility = View.VISIBLE
            ivUser.visibility = View.GONE
        } else {
            tvLocation.visibility = View.GONE
            ivLocation.visibility = View.GONE
            ivUser.visibility = View.VISIBLE
        }

        // Use Glide with a listener
        Glide.with(requireContext()).load(BuildConfig.IMAGE_URL + _url)
            .placeholder(R.drawable.ic_user).error(R.drawable.ic_user)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Hide progress bar when loading fails
                    progressBar.visibility = View.GONE
                    return false // Allow Glide to handle the error drawable
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Hide progress bar when the image is loaded successfully
                    progressBar.visibility = View.GONE
                    return false // Allow Glide to display the image
                }
            }).into(ivUser)


        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}