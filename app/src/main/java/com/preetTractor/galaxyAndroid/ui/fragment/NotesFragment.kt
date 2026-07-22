package com.preetTractor.galaxyAndroid.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.localdata.ImageModel
import com.preetTractor.galaxyAndroid.data.model.notes.DataAllNotes
import com.preetTractor.galaxyAndroid.data.model.notes.ResponseAllNotes
import com.preetTractor.galaxyAndroid.databinding.FragmentNotesBinding
import com.preetTractor.galaxyAndroid.helper.*
import com.preetTractor.galaxyAndroid.helper.Globals.hideKeyboard
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.CustomSpinnerAdapter
import com.preetTractor.galaxyAndroid.ui.recyclerview.ImagesAdapter
import com.preetTractor.galaxyAndroid.ui.recyclerview.NoteListingAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class NotesFragment : Fragment() {
    lateinit var binding: FragmentNotesBinding
    var booleanAddNotes = true
    lateinit var spinnerAdapter: SpinnerAdapter
    val spinnerItems = listOf(
        CustomSpinnerAdapter.SpinnerItem("No Order Feedback"),
        CustomSpinnerAdapter.SpinnerItem("Marketing"),
        CustomSpinnerAdapter.SpinnerItem("Other"),
    )

    var fab: FloatingActionButton? = null
    var cardCode: String = ""
    var noteListingAdapter: NoteListingAdapter? = null
    var beatPlanId = ""

    var customerCardCode = ""
    var customerModuleFlag = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotesBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        private const val TAG = "NotesFragment"
        private const val REQUEST_CODE_SELECT_IMAGES = 1111
    }

    // If you want to store the date in a string variable
    var dateString: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateString = Globals.getTodaysDatervrsfrmt()

        builder = AlertDialog.Builder(requireContext())
        builder!!.setView(R.layout.progress_dialog_alert)
            .setCancelable(false)
        alertDialog = builder!!.create()


        fab = requireActivity().findViewById<FloatingActionButton>(R.id.addNotes)
        cardCode = requireActivity().intent.getStringExtra(Constant.WHERE_CARDCODE).toString()
        beatPlanId = requireActivity().intent.getStringExtra(Constant.WHERE_BEATPLAN_ID).toString()

        customerCardCode = requireActivity().intent.getStringExtra(Constant.CustomerCardCode).toString()
        customerModuleFlag = requireActivity().intent.getStringExtra(Constant.flagCustomerModule).toString()

        fab?.setOnClickListener {
            if (booleanAddNotes) {
                booleanAddNotes = !booleanAddNotes
                binding.cardLeave.visibility = if (booleanAddNotes) View.GONE else View.VISIBLE
                //   binding.tvAdd.alpha = if (!booleanAddLeave) .5f else 1f
            } else {

            }
        }


        intialStatus()
        clickedEvents()
        binding.tvDate.setOnClickListener {
            Globals.openDatePicker(binding.tvDate) { formattedDate ->
                // Set the formatted date (yyyy-MM-dd) in the EditText
                dateString = formattedDate
                binding.tvDate.text = Globals.dateStringConvertToDesiredFormat(
                    formattedDate,
                    "yyyy-MM-dd",
                    "dd-MM-yyyy"
                )
                apiNotesCalling()
            }
        }

        apiNotesCalling()
    }

    private var allNotesList = ArrayList<DataAllNotes>()

    private fun apiNotesCalling() {
        /*  {
              "BeatPlan_id": "1",
              ''CardCode":"12344",
              "Date":"2023-02-01"
          }*/
        binding.progressbarNotes.visibility = View.VISIBLE

        val hde = JsonObject()
        hde.addProperty("Date", dateString)

        if(customerModuleFlag.equals("CustomerModule")){
            hde.addProperty("BeatPlan_id", "")
            hde.addProperty("CardCode", customerCardCode)
        }
        else{
            hde.addProperty("BeatPlan_id", beatPlanId)
            hde.addProperty("CardCode", cardCode)
        }

        val call = RetrofitClient.apiService.getAllNotes(hde)

        call.enqueue(object : Callback<ResponseAllNotes> {
            override fun onResponse(
                call: Call<ResponseAllNotes>,
                response: Response<ResponseAllNotes>
            ) {

                response.body()?.let {
                    binding.progressbarNotes.visibility = View.GONE

                    if (it.status == 200) {
                        allNotesList.clear()
                        if (it.data.isNotEmpty()) {
                            binding.noDataFound.visibility = View.GONE
                            allNotesList = it.data as ArrayList<DataAllNotes>


                        } else {
                            allNotesList = it.data as ArrayList<DataAllNotes>



                            binding.noDataFound.visibility = View.VISIBLE

                        }
                        Log.e(TAG, "onResponse: $allNotesList")
                        if (isAdded) {
                            Log.e(TAG, "onResponse: $allNotesList")
                            noteListingAdapter = NoteListingAdapter(allNotesList, requireActivity())
                            binding.rvNotes.adapter = noteListingAdapter
                            binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
                            noteListingAdapter!!.notifyDataSetChanged()
                        }

//                        noteListingAdapter = NoteListingAdapter(allNotesList, requireActivity())



                    } else if (it.status == 201) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseAllNotes>, t: Throwable) {
                binding.progressbarNotes.visibility = View.GONE
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()

            }
        })
    }


    private fun clickedEvents() {
        binding.etTypeSelected.setOnClickListener { binding.spinner.performClick() }

        binding.btnCancel.setOnClickListener {

            binding.etRemark.text!!.clear()
            imageList.clear()
            hideKeyboard()
            booleanAddNotes = !booleanAddNotes
            binding.cardLeave.visibility = if (booleanAddNotes) View.GONE else View.VISIBLE
//            binding.tvAdd.alpha = if (!booleanAddLeave || !booleanEditLeave) .5f else 1f
            /*   if (booleanEditLeave) {
                   binding.btnSave.text = "Save"
                   binding.cardLeave.visibility = View.GONE
                   binding.tvAdd.alpha = 1f
                   booleanEditLeave = false
               }*/

        }

        binding.btnSave.setOnClickListener {
            hideKeyboard()
            if( binding.etRemark.text.toString().isNotEmpty())
                createNoteRequest()
            else
                Toast.makeText(requireContext(), "Enter remark", Toast.LENGTH_SHORT).show()
        }
    }



    var leaveTypeStr = ""
    var leaveReasonStr = ""
    private lateinit var imagesAdapter: ImagesAdapter
    private val imageList = mutableListOf<ImageModel>()

    private fun intialStatus() {
        binding.edtSearchActual.clearFocus()

        binding.rvAttachedPhotos.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        imagesAdapter = ImagesAdapter(imageList) { imageModel ->
            imagesAdapter.deleteImage(imageModel)
        }

        binding.rvAttachedPhotos.adapter = imagesAdapter
        binding.tvAddAttachment.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(requireActivity())) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${requireActivity().packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //  showTrackingAlertDialog()
            } else {
                openGalleryForImages()
            }

        }


        /* try {
             noteListingAdapter = NoteListingAdapter(allNotesList, requireActivity())
         } catch (e: Exception) {
         }*/

        binding.cardLeave.visibility = if (booleanAddNotes) View.GONE else View.VISIBLE
        // binding.tvAdd.alpha = if (!booleanAddLeave) .5f else 1f
        binding.btnSave.text = "Save"

        //  binding.tvDate.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", null)


        /*binding.tvDate.setOnClickListener {

        }*/

        binding.tvDate.text = Globals.getTodaysDate()

        /*binding.tvDate.setOnClickListener {

        }*/



        spinnerAdapter = CustomSpinnerAdapter(requireContext(), spinnerItems)
        binding.spinner.adapter = spinnerAdapter
        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = spinnerItems[position]
                    leaveTypeStr = selectedItem.text
                    binding.etTypeSelected.text = leaveTypeStr
//                    Toast.makeText(requireContext(), "Selected item: ${selectedItem.text}", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

        leaveReasonStr = binding.etRemark.text.toString()


    }


    private fun openGalleryForImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // Allows selecting multiple file types
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf")) // Restrict to images & PDFs
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selections
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES)
    }



//    private fun openGalleryForImages() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = "image/*"
//            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        }
//        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == RESULT_OK) {
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    // val path = getImagePath(imageUri)
                    val path = FileUtilsPdf.getPathFromUri(requireContext(),imageUri)
                    addImageToList(imageUri, path!!)
                }
            } else {
                data?.data?.let { uri ->
                    val path =  FileUtilsPdf.getPathFromUri(requireContext(),uri)
                    addImageToList(uri, path!!)
                }
            }
        }
    }


    private fun getImagePath(uri: Uri): String? {
        var path: String? = null
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                path = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return path
    }



    /*   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
           super.onActivityResult(requestCode, resultCode, data)
           if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
               val clipData = data.clipData // Get the clip data for multiple images
               if (clipData != null) {
                   val selectedImages = mutableListOf<Uri>()
                   for (i in 0 until clipData.itemCount) {
                       val imageUri: Uri = clipData.getItemAt(i).uri
                       selectedImages.add(imageUri) // Add the URI to the list
                       val imagePath = getPathFromUri(imageUri) // Get the path from URI
                       addImageToList(imageUri, imagePath!!)
                       Log.e(TAG, "Image path: $imagePath")
                   }

               } else {
                   // Handle single image selection
                   val imageUri: Uri? = data.data
                   if (imageUri != null) {
                       val imagePath = getPathFromUri(imageUri)
                       addImageToList(imageUri, imagePath!!)
                       Log.e(TAG, "Single Image path: $imagePath")

                   }
               }
           }
       }*/



    // Function to get the path from URI
    private fun getPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(columnIndex)
            cursor.close()
            path
        } else {
            null
        }
    }


    private fun addImageToList(uri: Uri, path: String) {
        imageList.add(ImageModel(uri, path))
        imagesAdapter.notifyDataSetChanged()

        Log.e(TAG, "addImageToList: $imageList")
    }

    var cardName = ""
    var builder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    private fun createNoteRequest() {
        leaveReasonStr = binding.etRemark.text.toString()
        val builder: MultipartBody.Builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("BeatPlan_id", beatPlanId)
        builder.addFormDataPart("CardCode", cardCode)
        builder.addFormDataPart("CardName", CustomerDetailActivity.cardName)
        builder.addFormDataPart("Title", leaveTypeStr)
        builder.addFormDataPart("Remark", leaveReasonStr)
        builder.addFormDataPart(
            "CreatedBy", Globals.SalesEmployeeCode
        ) //todo static

        builder.addFormDataPart("Create_Date", Globals.getTodaysDatervrsfrmt()!!)
        builder.addFormDataPart("Create_Time", Globals.getTCurrentTime_hh_mm_ss_aa()!!)
        /*  builder.addFormDataPart(
              "File",
              "",
              RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
          )*/


        if (imageList.isNotEmpty()) {
            Log.d("ApiCreateNotePayload", "createNoteRequest: working")
            for (i in imageList.indices) {
                val file: File =File(imageList[i].path.toString())
                builder.addFormDataPart(
                    "File",
                    file.name,
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                )
            }
        } else {
            Log.d("ApiCreateNotePayload", "createNoteRequest: workingelse")
            builder.addFormDataPart(
                "File",
                "",
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
            )
        }



        /*       try {
                   val file: File = File(currentPhotoPath)
                   builder.addFormDataPart(
                       "File",
                       compressedImageFile.name,
                       RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                   )
               } catch (e: java.lang.Exception) {
                   builder.addFormDataPart(
                       "File",
                       "",
                       RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
                   )
                   e.printStackTrace()
               }*/
        Log.d("ApiCreateNotePayload", "createNoteRequest: $builder")

        val requestBody: MultipartBody = builder.build()
        createNotApi(requestBody)

    }
    private fun createNotApi(requestBody: MultipartBody) {
        Log.d("ApiCreateNotePayload", "createNotApi: $requestBody")
        alertDialog!!.show()
        val call: Call<ResponseGlobal> = RetrofitClient.apiService.createNote(requestBody)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>, response: Response<ResponseGlobal>
            ) {
                if (response != null) {
                    alertDialog!!.dismiss()
                    if (response.body()!!.status == 200) {
                        try {
                            booleanAddNotes = !booleanAddNotes
                            binding.cardLeave.visibility =
                                if (booleanAddNotes) View.GONE else View.VISIBLE
                            Toast.makeText(
                                requireContext(),
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            binding.etRemark.text!!.clear()
                            imageList.clear()
                            imagesAdapter.notifyDataSetChanged()

                            apiNotesCalling()

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