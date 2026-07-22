package com.preetTractor.galaxyAndroid.ui.activity

import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.AttachmentModel
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.databinding.DialogOpenGalleryAndCameraBinding
import com.preetTractor.galaxyAndroid.databinding.ProfileActivityBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.LocationPermissionHelper
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.retrofit.WebSocketManager
import com.preetTractor.galaxyAndroid.ui.activity.splashScreen.SplashActivity
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class ProfileActivity : BaseActivity() {
    lateinit var binding: ProfileActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callGalaxyProfile()

        binding.apply {
//            tvAppVersion.text = BuildConfig.FORCED_VERSION_NAME

            logout.setOnClickListener {
                if (!PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
                    openLogoutDialog()
                }else{
                    openCheckOutWarningDialog()
                }
            }

            backPress.setOnClickListener {
                finish()
            }

            ivPictureClick.setOnClickListener {
                openDialog()
            }
        }

    }

    private fun openCheckOutWarningDialog() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "Kindly do check out first",
            Snackbar.LENGTH_INDEFINITE
        )

        snackbar.setAction("OK") {
            snackbar.dismiss()
        }
        snackbar.setActionTextColor(getResources().getColor(R. color. colorPrimary))
        snackbar.show()
    }

    companion object {
        private const val TAG = "ProfileActivity"
        private const val PICK_IMAGE = 9329
        private const val REQUEST_IMAGE_MAKE_MODEL_PHOTO = 9955

    }

    private fun openLogoutDialog() {
        AlertDialog.Builder(this).setTitle("Logout Confirmation")
            .setMessage("Are you sure you want to logout?").setPositiveButton("Yes") { dialog, _ ->
                // Handle logout logic here
                callLogoutApi()
                dialog.dismiss()

            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Close the dialog
            }.show()


    }


    private fun callLogoutApi() {
        val jsonObject = JsonObject().apply {
            addProperty("mobile", PrefsByShubh.getMobileNO())
            addProperty("device_id", Settings.Secure.getString(this@ProfileActivity.getContentResolver(), Settings.Secure.ANDROID_ID).toString())
        }

        val call = RetrofitClient.apiService.logout(jsonObject)

        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>, response: Response<ResponseGlobal>
            ) {
                response.body()?.let { body ->
                    when (response.code()) {
                        200 -> {
                            Log.e(TAG, "onResponse: ${body.message}")

                            if (body.status == 200) {

                                PrefsByShubh.putString("role", "")
                                PrefsByShubh.clear()
                                Prefs.clear()

                                val intent =
                                    Intent(this@ProfileActivity, SplashActivity::class.java).apply {
                                        flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                startActivity(intent)
                            } else if (body.status == 401) {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Session Expired, Please Login Again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                PrefsByShubh.clear()
                                Prefs.clear()
                                //sessionManagement.ClearSession()
                                WebSocketManager.clearEvents()
                                WebSocketManager.disconnect()
                                Globals.loginIntoAnotherDevice = false

                                val intent =
                                    Intent(this@ProfileActivity, ActivitySignIn::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        201 -> {
                            Toast.makeText(this@ProfileActivity, body.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {
                            Toast.makeText(this@ProfileActivity, body.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } ?: run {
                    Log.e(TAG, "Response body is null")
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


    private fun callGalaxyProfile() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
        val call: Call<AttachmentModel> =
            RetrofitClient.apiService.getNewAllAttachmentApi(jsonObject)
        call.enqueue(object : Callback<AttachmentModel> {
            override fun onResponse(
                call: Call<AttachmentModel>, response: Response<AttachmentModel>
            ) {
                if (response.body()!!.status == 200) {
                    Log.e(TAG, "onResponse: " + response.body()!!.message)
                    if (response.body()!!.data.isNotEmpty()) {
                        binding.apply {
                            //tvDesignation.setText(response.body()!!.data[0].employee_detail.role)
                            tvUserName.text = response.body()!!.data[0].employee_detail.SalesEmployeeName
                            Log.e(
                                TAG,
                                "onResponse: " + response.body()!!.data[0].employee_detail.role_name
                            )
                            Prefs.putString(
                                Globals.role_name,
                                response.body()!!.data[0].employee_detail.role_name
                            )

                            //empId.setText(response.body()!!.data[0].employee_detail.EmployeeID)
                            tvEmail.text = response.body()!!.data[0].employee_detail.Email
                            tvMobileNo.text = response.body()!!.data[0].employee_detail.Mobile
                            tvLocation.text = response.body()!!.data[0].employee_detail.Address/*        Glide.with(this@ProfileActivity)
                                .load(BuildConfig.IMAGE_URL + response.body()!!.data[0].profileImage)
                                .into(nameIcon)*/


                            Picasso.get()
                                .load(BuildConfig.IMAGE_URL + response.body()!!.data[0].profileImage)
                                .placeholder(R.drawable.ic_user).error(R.drawable.ic_user)
                                .into(ivProfilePic)
                        }

                    }
                } else if (response.code() == 201) {
                    Toast.makeText(
                        this@ProfileActivity, response.body()!!.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AttachmentModel>, t: Throwable) {
                Log.e(TAG, "onFailure: " + t.message)
            }
        })
    }


    private fun uploadProfilePic(body: MultipartBody) {

        Toast.makeText(
            this@ProfileActivity, "Uploading Profile", Toast.LENGTH_SHORT
        ).show()

        val call: Call<ResponseGlobal> = RetrofitClient.apiService.uploadProfilePic(body)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {
                if (response.body()!!.status == 200) {
                    Log.e(TAG, "onResponse: " + response.body()!!.message)
                    Toast.makeText(
                        this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT
                    ).show()
                    callGalaxyProfile()
                } else if (response.code() == 201) {
                    Toast.makeText(
                        this@ProfileActivity, response.body()!!.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Log.e(TAG, "onFailure: " + t.message)
            }
        })
    }

    private var _bindingDialog: DialogOpenGalleryAndCameraBinding? = null
    private val bindingDialog get() = _bindingDialog!!
    private fun openDialog() {
        val dialog = Dialog(this)
        _bindingDialog = DialogOpenGalleryAndCameraBinding.inflate(layoutInflater)
        val layoutInflater = LayoutInflater.from(this)

        dialog.setContentView(bindingDialog.root)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )






        bindingDialog.btnCancelDialog.setOnClickListener {
            dialog.dismiss()
        }

        bindingDialog.tvTakePhoto.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            } else {
                dispatchMakeModelPictureIntent()
            }
            dialog.dismiss()
        }


        bindingDialog.tvOpengallery.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${packageName}")
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

    var photoURI: Uri? = null

    override fun dispatchMakeModelPictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                var photoFile: File? = try {
                    createImageFile()
                } catch (ex: java.io.IOException) {
                    Toast.makeText(
                        this, "Error occurred while creating the file", Toast.LENGTH_SHORT
                    ).show()
                    null
                }
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this, "${BuildConfig.APPLICATION_ID}.FileProvider", it
                    )
//                    val photoURI: Uri = Uri.fromFile(it) //todo ==> using Uri.fromFile to create the URI for the photo file, which leads to a FileUriExposedException on Android 7.0 and above
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(
                        takePictureIntent, REQUEST_IMAGE_MAKE_MODEL_PHOTO
                    )
                }
            }
        }
    }

    override var currentPhotoPath = ""
    override var makeModelPhoto = ""
    override lateinit var fileMakeModelPhotoUri: Uri

    override fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/AnGService"
        )
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        val image = File.createTempFile(imageFileName, ".png", storageDir)

        currentPhotoPath = image.absolutePath


        return image
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE -> {
                if (data != null) {
                    var uri = data.data


                    val extras: Bundle? = data.extras
                    val selectedImage: Uri? = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = contentResolver.query(
                        selectedImage!!, filePathColumn, null, null, null
                    )
                    cursor?.moveToFirst()
                    val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
                    var currentImagePath = cursor.getString(columnIndex)
                    if (cursor != null) {
                        cursor.close()
                    }

                    Log.e(TAG, "onActivityResult: $currentImagePath")
                    //  addImageToList(uri!!, currentImagePath!!)
                    // Check image size
                    val file = File(currentImagePath)
                    val fileSizeInMB = file.length() / (1024 * 1024)

                    if (fileSizeInMB > 3) {
                        Toast.makeText(this, "Image size must not exceed 3 MB.", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    val builder: MultipartBody.Builder = MultipartBody.Builder()
                    builder.setType(MultipartBody.FORM)


                    builder.addFormDataPart(
                        "SalesEmployeeCode", Globals.SalesEmployeeCode
                    ) //todo static


                    /* val file: File =
                         Globals.compressImageFile(File(currentImagePath))*/
                    var isImageAdded = false
                    try {
                        val file: File = File(currentImagePath)

                        builder.addFormDataPart(
                            "Image",
                            file.name,
                            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )

                        isImageAdded = true

                    } catch (e: Exception) {

                    }


                    val requestBody: MultipartBody = builder.build()/*    // Check if "Image" key exists in the parts
                    val isImageKeyPresent = requestBody.parts.any { part ->
                        part.headers?.get("Content-Disposition")?.contains("name=\"Image\"") == true
                    }

                    if (!isImageKeyPresent) {
                        Toast.makeText(this, "No Image Selected.", Toast.LENGTH_SHORT).show()
                    }else{
                        uploadProfilePic(requestBody)
                    }*/

                    uploadProfilePic(requestBody)


                }


            }

            REQUEST_IMAGE_MAKE_MODEL_PHOTO -> {

                val imgFile = File(makeModelPhoto)
                if (imgFile.exists()) {
                    fileMakeModelPhotoUri = Uri.fromFile(imgFile)
                    currentPhotoPath = fileMakeModelPhotoUri.path!!
                    Log.e("fileUri---", fileMakeModelPhotoUri.toString())
                }


                Log.e(TAG, "onActivityResultCUSTOMERPHOTO>>>>>>>>>: $currentPhotoPath")
                val builder: MultipartBody.Builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)


                builder.addFormDataPart(
                    "SalesEmployeeCode", Globals.SalesEmployeeCode
                ) //todo static


                try {
                    val file: File = Globals.compressImageFile(this,File(currentPhotoPath))

                    builder.addFormDataPart(
                        "Image",
                        file.name,
                        file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    )
                } catch (e: Exception) {

                    Log.e(TAG, "onActivityResult: ERROR")
                }


                val requestBody: MultipartBody = builder.build()


                /* if (currentPhotoPath.isNotEmpty()){
                     uploadProfilePic(requestBody)
                 }*/

                // Check if "Image" key exists in the parts
                val isImageKeyPresent = requestBody.parts.any { part ->
                    part.headers?.get("Content-Disposition")?.contains("name=\"Image\"") == true
                }

                if (!isImageKeyPresent) {
                    Toast.makeText(this, "No Image Selected.", Toast.LENGTH_SHORT).show()
                } else {
                    uploadProfilePic(requestBody)
                }

            }
        }
    }
}