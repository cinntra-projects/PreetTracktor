package com.preetTractor.galaxyAndroid.ui.activity.customer.fragment

import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.localdata.ImageModel
import com.preetTractor.galaxyAndroid.data.model.customer.DataOutletPicsFromCustomer
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseOutletPicsFromCustomer
import com.preetTractor.galaxyAndroid.databinding.DialogOpenGalleryAndCameraBinding
import com.preetTractor.galaxyAndroid.databinding.FragmentOutletPicBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.hideKeyboard
import com.preetTractor.galaxyAndroid.helper.LocationPermissionHelper
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity.Companion.cardCode
import com.preetTractor.galaxyAndroid.ui.recyclerview.ImagesAdapter
import com.preetTractor.galaxyAndroid.ui.recyclerview.OutletPicAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*





class OutletPicFragment : Fragment() {
    lateinit var binding: FragmentOutletPicBinding
    var booleanAddOutletPics = true
    var builder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOutletPicBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    private lateinit var imagesAdapter: ImagesAdapter
    private val imageList = mutableListOf<ImageModel>()

    companion object {
        private const val TAG = "OutletPicFragment"
        private const val PICK_IMAGE = 9321
        private const val REQUEST_IMAGE_MAKE_MODEL_PHOTO = 9954


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        builder = AlertDialog.Builder(requireContext())
        builder!!.setView(com.preetTractor.galaxyAndroid.R.layout.progress_dialog_alert)
            .setCancelable(false)
        alertDialog = builder!!.create()
        initialStatus()
        clickedEvents()
    }


    private fun initialStatus() {
        apiOutletPicCalling()
        binding.rvSelectedImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        imagesAdapter = ImagesAdapter(imageList) { imageModel ->
            imagesAdapter.deleteImage(imageModel)
        }

        binding.rvSelectedImages.adapter = imagesAdapter
    }

    private fun addImageToList(uri: Uri, path: String) {
        imageList.add(ImageModel(uri, path))
        imagesAdapter.notifyDataSetChanged()

        Log.e(TAG, "addImageToList: $imageList")


    }

    private fun clickedEvents() {
        binding.addOutletPics.setOnClickListener {
            if (booleanAddOutletPics) {
                booleanAddOutletPics = !booleanAddOutletPics
                binding.cardOutlet.visibility =
                    if (booleanAddOutletPics) View.GONE else View.VISIBLE
                //   binding.tvAdd.alpha = if (!booleanAddLeave) .5f else 1f
            } else {

            }
        }

        binding.btnCancel.setOnClickListener {
            hideKeyboard()
            booleanAddOutletPics = !booleanAddOutletPics
            binding.cardOutlet.visibility = if (booleanAddOutletPics) View.GONE else View.VISIBLE
//            binding.tvAdd.alpha = if (!booleanAddLeave || !booleanEditLeave) .5f else 1f
            /*   if (booleanEditLeave) {
                   binding.btnSave.text = "Save"
                   binding.cardLeave.visibility = View.GONE
                   binding.tvAdd.alpha = 1f
                   booleanEditLeave = false
               }*/

            binding.apply {
                etRemark.setText("")
            }

        }

        binding.btnSave.setOnClickListener {
            hideKeyboard()
            if (binding.etRemark.text.toString().trim().isNotEmpty() && imageList.isNotEmpty()) {
                createOutletPicRequest()
            } else {
                Toast.makeText(requireContext(), "Please fill form properly", Toast.LENGTH_SHORT)
                    .show()
            }
            // createNoteRequest()
        }
        binding.apply {
            ivImageSelector.setOnClickListener {
                openDialog()
            }
        }


    }

    private var _bindingDialog: DialogOpenGalleryAndCameraBinding? = null
    private val bindingDialog get() = _bindingDialog!!
    private fun openDialog() {
        val dialog = Dialog(requireContext())
        _bindingDialog = DialogOpenGalleryAndCameraBinding.inflate(layoutInflater)
        val layoutInflater = LayoutInflater.from(requireContext())

        dialog.setContentView(bindingDialog.root)
        dialog.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        bindingDialog.btnCancelDialog.setOnClickListener {
            dialog.dismiss()
        }

        bindingDialog.tvTakePhoto.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(requireActivity())) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${requireActivity().packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            } else {
                dispatchMakeModelPictureIntent()
            }
            dialog.dismiss()
        }


        bindingDialog.tvOpengallery.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(requireActivity())) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${requireActivity().packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //  showTrackingAlertDialog()
            } else {
                openGallery()
            }

            dialog.dismiss()
        }

        dialog.show()
    }


    private fun openGallery() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        i.type = "image/*"
        startActivityForResult(i, PICK_IMAGE)
    }

    var selectedImagePath = ""
    var currentPhotoPath = ""
    var makeModelPhoto = ""
    lateinit var fileMakeModelPhotoUri: Uri


    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE -> {
                if (data!=null){
                    var uri = data?.data


                    val extras: Bundle? = data?.getExtras()
                    val selectedImage: Uri? = data!!.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? =
                        requireContext().contentResolver.query(
                            selectedImage!!,
                            filePathColumn,
                            null,
                            null,
                            null
                        )
                    cursor?.moveToFirst()
                    val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
                    var currentImagePath = cursor.getString(columnIndex)
                    if (cursor != null) {
                        cursor.close()
                    }
                    Log.e(TAG, "onActivityResult: $currentImagePath")
                    addImageToList(uri!!, currentImagePath!!)
                }


            }

            REQUEST_IMAGE_MAKE_MODEL_PHOTO -> {

                    //  val compressedImageFile = compressImageFile(File(currentPhotoPath))

                    val imgFile = File(makeModelPhoto!!)
                    if (imgFile.exists()) {
                        fileMakeModelPhotoUri = Uri.fromFile(imgFile)
                        currentPhotoPath = fileMakeModelPhotoUri.path!!
                        Log.e("fileUri---", fileMakeModelPhotoUri.toString())
                    }

                    //  addImageToList(fileMakeModelPhotoUri!!, currentPhotoPath!!)
                    Log.e(TAG, "onActivityResultCUSTOMERPHOTO>>>>>>>>>: $currentPhotoPath")


                    addImageToList(photoURI!!, currentPhotoPath)



            }
        }

        if (requestCode == REQUEST_IMAGE_MAKE_MODEL_PHOTO) {
            if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // Clear currentPhotoPath if the user canceled the camera
//                currentPhotoPath = ""
                imageList.clear()
            }
        }
    }

    var photoURI: Uri? = null

    fun dispatchMakeModelPictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                var photoFile: File? = try {
                    createImageFile()
                } catch (ex: java.io.IOException) {
                    Toast.makeText(
                        requireContext(),
                        "Error occurred while creating the file",
                        Toast.LENGTH_SHORT
                    ).show()
                    null
                }
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        requireContext(), "${BuildConfig.APPLICATION_ID}.FileProvider", it
                    )
//                    val photoURI: Uri = Uri.fromFile(it) //todo ==> using Uri.fromFile to create the URI for the photo file, which leads to a FileUriExposedException on Android 7.0 and above
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_MAKE_MODEL_PHOTO)
                }
            }
        }
    }

    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: requireContext().cacheDir
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val image = File.createTempFile(imageFileName, ".png", storageDir)

        currentPhotoPath = image.absolutePath


        return image
    }


    private var allPicList = ArrayList<DataOutletPicsFromCustomer>()
    var outletPicAdapter: OutletPicAdapter? = null

    private fun apiOutletPicCalling() {

        alertDialog!!.show()
        val hde = JsonObject().apply {

            // addProperty("CardCode", cardCode)
            addProperty("CardCode", cardCode)

        }
        val call = RetrofitClient.apiService.callOutletPicApi(hde)

        call.enqueue(object : Callback<ResponseOutletPicsFromCustomer> {
            override fun onResponse(
                call: Call<ResponseOutletPicsFromCustomer>,
                response: Response<ResponseOutletPicsFromCustomer>
            ) {

                response.body()?.let {
                    alertDialog!!.dismiss()

                    if (it.status == 200) {
                        allPicList.clear()
                        if (it.data.isNotEmpty()) {
                            binding.noDataFound.visibility = View.GONE
                            allPicList = it.data as ArrayList<DataOutletPicsFromCustomer>


                        } else {
                            allPicList = it.data as ArrayList<DataOutletPicsFromCustomer>



                            binding.noDataFound.visibility = View.VISIBLE

                        }
                        Log.e(TAG, "onResponse: $allPicList")
                        outletPicAdapter = OutletPicAdapter(allPicList)

                        binding.rvOutletPics.adapter = outletPicAdapter
                        binding.rvOutletPics.layoutManager = LinearLayoutManager(requireContext())
                        outletPicAdapter!!.notifyDataSetChanged()

                    } else if (it.status == 201) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseOutletPicsFromCustomer>, t: Throwable) {
                alertDialog!!.dismiss()
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()

            }
        })
    }


    private fun createOutletPicRequest() {

        val builder: MultipartBody.Builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
    /*    {
            SalesEmployeeCode:-1
            CardCode:9195
            CardName:Golden ElectroWorld
            Title:Testing data
            Remark:testing remarks....
            Create_Date:2024-06-27
            Create_Time:10:40 AM,
            File:[]
        }*/

        builder.addFormDataPart("CardCode", cardCode)
        builder.addFormDataPart("CardName", CustomerDetailActivity.cardName)
        builder.addFormDataPart("Title", "")
        builder.addFormDataPart("Remark", binding.etRemark.text.toString().trim())
        builder.addFormDataPart("SalesEmployeeCode", Globals.SalesEmployeeCode) //todo static
        builder.addFormDataPart("Create_Date", Globals.getTodaysDatervrsfrmt()!!)
        builder.addFormDataPart("Create_Time", Globals.getTCurrentTime_hh_mm_ss_aa()!!)



        if (imageList.isNotEmpty())
        {
            for (i in imageList.indices) {
                val originalFile = File(imageList[i].path.toString())

                if (originalFile.exists()) {
                    try {
                        val compressedFile = Globals.compressImageFile(requireContext(),originalFile)

                        if (compressedFile != null && compressedFile.exists()) {
                            builder.addFormDataPart(
                                "File",
                                compressedFile.name,
                                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), compressedFile)
                            )
                        } else {
                            // Compression failed
                            Log.e("File Upload", "Compression failed for file: ${originalFile.path}")
                            builder.addFormDataPart(
                                "File",
                                "",
                                "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                            )
                        }
                    } catch (e: Exception) {
                        // Handle exception during compression
                        Log.e("File Upload", "Error during compression: ${e.message}", e)
                        builder.addFormDataPart(
                            "File",
                            "",
                            "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )
                    }
                } else {
                    // File does not exist
                    Log.e("File Upload", "File does not exist: ${originalFile.path}")
                    builder.addFormDataPart(
                        "File",
                        "",
                        "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    )
                }

            }
        }
        else {
            builder.addFormDataPart(
                "File",
                "",
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
            )
        }





        val requestBody: MultipartBody = builder.build()
        createOutletPicApi(requestBody)

    }

    private fun createOutletPicApi(requestBody: MultipartBody) {
        alertDialog!!.show()
        val call: Call<ResponseGlobal> = RetrofitClient.apiService.createGalaxyOutlet(requestBody)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>, response: Response<ResponseGlobal>
            ) {
                if (response != null) {
                    alertDialog!!.dismiss()
                    if (response.body()!!.status == 200) {
                        try {
                            binding.etRemark.setText("")
                            imageList.clear()
                            imagesAdapter.notifyDataSetChanged()
                            Toast.makeText(
                                requireContext(),
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            apiOutletPicCalling()

                        } catch (e: Exception) {


                        }


                    } else if (response.body()!!.status == 201) {
                        try {
                            Toast.makeText(
                                requireContext(), response.body()!!.message, Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                alertDialog!!.dismiss()
                try {
                    Toast.makeText(requireActivity(), "" + t.message, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                }

            }
        })
    }


}

