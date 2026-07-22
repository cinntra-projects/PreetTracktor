package com.preetTractor.galaxyAndroid.ui.fragment

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.*
import com.preetTractor.galaxyAndroid.databinding.FragmentAttendanceInnerBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setTint
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.recyclerview.AttendanceUserInnerListingAdapter
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.preetTractor.galaxyAndroid.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AttendanceInnerFragment : Fragment() {
    lateinit var binding: FragmentAttendanceInnerBinding
    private lateinit var adapter: AttendanceUserInnerListingAdapter
    private var allItemList = ArrayList<DataInnerAttendance>()
    private lateinit var layoutManager: LinearLayoutManager

    val args: AttendanceInnerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAttendanceInnerBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "AttendanceFragment"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListing()
    }

    private fun getListing() {
        binding.progressBar2.visibility = View.VISIBLE
        val hde = JsonObject()
        if (PrefsByShubh.getString(Constant.FLAG,"").equals("_FROM_ATTENDANCE")){
            hde.apply {
                addProperty("SalesEmployeeCode", PrefsByShubh.getString(Constant.SALESEMPLOYEECODE,""))
                addProperty("Date", args.data.Create_Date)
            }
        }else{
            hde.apply {
                addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
                addProperty("Date", args.data.Create_Date)
            }
        }



        val call = RetrofitClient.apiService.galaxyInnerattendanceList(hde)

        call.enqueue(object : Callback<ResponseInnerAttendance> {
            override fun onResponse(
                call: Call<ResponseInnerAttendance>,
                response: Response<ResponseInnerAttendance>
            ) {
                binding.progressBar2.visibility = View.GONE
                response.body()?.let {
                    if (it.status.equals("200", ignoreCase = true)) {

                        allItemList.clear()
                        allItemList.addAll(it.data)
                        adapter = AttendanceUserInnerListingAdapter(
                            allItemList,
                            requireContext()
                        )

                        binding.rvOuterList.apply {
                            setHasFixedSize(true)
                        }

                        binding.rvOuterList.adapter = adapter
                        adapter.notifyDataSetChanged()
                        adapter.setOnItemClickListener { _url, _msg ->
                            showPopup(_url, _msg)
                            Log.e(TAG, "On Clicked Image url: ${_url}")
                            Log.e(TAG, "On Clicked Image msg : ${_msg}")
                        }
                        Log.e(TAG, "onResponseBackground: ${it.message}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseInnerAttendance>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                binding.progressBar2.visibility = View.GONE
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
        Glide.with(requireContext())
            .load(BuildConfig.IMAGE_URL + _url)
            .override(600,800)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Hide progress bar when loading fails
                    Log.e("GLIDE_ERROR", e?.message ?: "null")
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
            })
            .into(ivUser)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}