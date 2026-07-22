package com.preetTractor.galaxyAndroid.ui.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pixplicity.easyprefs.library.Prefs;
import com.preetTractor.galaxyAndroid.R;
import com.preetTractor.galaxyAndroid.adapter.DynamicFieldsShowAdapter;
import com.preetTractor.galaxyAndroid.adapter.PreviousImageViewAdapter;
import com.preetTractor.galaxyAndroid.data.AttachDocument;
import com.preetTractor.galaxyAndroid.data.DynamicFieldsKeysResponse;
import com.preetTractor.galaxyAndroid.data.DynamicFieldsListModelClass;
import com.preetTractor.galaxyAndroid.data.LeadDocumentResponse;
import com.preetTractor.galaxyAndroid.data.LeadResponse;
import com.preetTractor.galaxyAndroid.data.LeadTypeData;
import com.preetTractor.galaxyAndroid.data.LeadValue;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.FieldData;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.UserAccessData;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.UserAccessManagementResponse;
import com.preetTractor.galaxyAndroid.data.UserAccessStorageHelper;
import com.preetTractor.galaxyAndroid.databinding.LeadInfoBinding;
import com.preetTractor.galaxyAndroid.helper.Globals;
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh;
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient;
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.AddBPCustomer;
import com.preetTractor.galaxyAndroid.utils.GalleryUtils;
import com.preetTractor.galaxyAndroid.utils.ImageSelector;
import com.preetTractor.galaxyAndroid.utils.PermissionUtils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeadInformation extends Fragment implements View.OnClickListener, PreviousImageViewAdapter.DeleteItemClickListener {

    private final int REQUEST_CODE_CHOOSE = 1001;

    private ImageSelector imageSelector;

    LeadValue leadValue;
    Context leadsActivity;
    List<LeadValue> leadValues = new ArrayList<>();
    String[] leadstatus = new String[4];
    String status = "";
    String leadtype = "";
    Integer id;
    ArrayList<LeadTypeData> leadTypeData = new ArrayList<>();
    Context context;

    LeadInfoBinding binding;
    DynamicFieldsShowAdapter dynamicFieldsShowAdapter;
    private static final HashMap<String, String> ListOneDetailKeyValueMap = new HashMap<>();


    //todo tarun1 new
    HashMap<String, String> dynamicKeysFieldValuesMap = new HashMap<>();
    List<Map.Entry<String, String>> FinalDynamicFieldsKeyValueMap = new ArrayList<>();
    String  token = "Token " + Globals.GalaxyVistaToken;

    public LeadInformation(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle b = getArguments();
            if (b.getString("From").equalsIgnoreCase("Lead")) {
                leadValue = (LeadValue) b.getParcelable(Globals.LeadDetails);
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                id = leadValue.getId();
            } else {
                id = Integer.parseInt(b.getString("From", "2"));
            }
        }


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }


    @Override
    public void onDetach() {
        super.onDetach();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = LeadInfoBinding.inflate(inflater, container, false);


        callStaticFieldsDynamicApiValue();


        //  View v=inflater.inflate(R.layout.lead_info, container, false);
        // ButterKnife.bind(this,v);
        binding.headerBottomRounded.headTitle.setText("Lead Detail");
        binding.headerBottomRounded.backPress.setOnClickListener(this);
        binding.createBp.setOnClickListener(this);
        binding.history.setOnClickListener(this);
        binding.attachment.setOnClickListener(this);
        leadstatus = getResources().getStringArray(R.array.lead_status);

        // eventManager();
            callApi(id);
            callLeadOneApi(id);
            callAttachmentApi(id);





        return binding.getRoot();
    }


    private void callStaticFieldsDynamicApiValue() {

        // ✅ Read stored response from file
        UserAccessManagementResponse storedResponse = UserAccessStorageHelper.readJsonFromFile(requireContext());

        // ✅ Print stored response
        if (storedResponse != null) {
            Log.d("ACTIVITY2_RESPONSE", "=== Retrieved Stored Response ===");

            for (UserAccessData data : storedResponse.getData()) {
                if (data.getModule_name().equals("Lead")) {

                    // ✅ Check if fieldData list exists
                    if (data.getData() != null) {
                        for (FieldData field : data.getData()) {

                            switch (field.getKey()) {
                                case "companyName":
                                    binding.companyNameText.setText(field.getLabel());
                                    break;

                                case "contactPerson":
                                    binding.personNameText.setText(field.getLabel());
                                    break;

                                case "designation":

                                    binding.personDesignationText.setText(field.getLabel());
                                    break;

                                case "phoneNumber":

                                    binding.phoneText.setText(field.getLabel());
                                    break;

                                case "email":

                                    binding.emailText.setText(field.getLabel());
                                    break;

                                case "location":

                                    binding.locationText.setText(field.getLabel());
                                    break;

                                case "source":

                                    binding.sourceText.setText(field.getLabel());
                                    break;

                                case "productInterest":

                                    binding.productInterestText.setText(field.getLabel());
                                    break;

                               /* case "numOfEmployee":

                                    binding.numOfEmployeeText.setText(field.getLabel());
                                    break;*/

                                case "turnover":

                                    binding.turnoverText.setText(field.getLabel());
                                    break;

                                case "lead_type":

                                    binding.leadPriorityText.setText(field.getLabel());
                                    break;

                                case "status":

                                    binding.statusText.setText(field.getLabel());
                                    break;

                                case "assignedTo":

                                    binding.assignedToText.setText(field.getLabel());
                                    break;

                                case "message":

                                    binding.remarksText.setText(field.getLabel());
                                    break;
                            }

                            Log.d("ACTIVITY2_RESPONSE", "Field Name: " + field.getKey() + ", Value: " + field.getLabel() + ", Mandatory: " + field.isMandatory());
                        }

                    } else {
                        Log.e("ACTIVITY2_RESPONSE", "❌ No field data found for Lead module.");
                    }

                    Log.d("ACTIVITY2_RESPONSE", "Module: " + data.getModule_name());
                }
            }
        }
        else {
            Log.e("ACTIVITY2_RESPONSE", "❌ Failed to retrieve stored response.");
        }
    }



    // todo code for dynamic fields (Tarun Sharma)
    private void setDynamicFields() {
        for (Map.Entry<String, String> entry : dynamicKeysFieldValuesMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String cleanedKey = "data[0]." + key;

            if (ListOneDetailKeyValueMap.containsKey(cleanedKey)) {
                FinalDynamicFieldsKeyValueMap.add(Map.entry(value, ListOneDetailKeyValueMap.get(cleanedKey)));
            }
        }

        for (Map.Entry<String, String> entry : FinalDynamicFieldsKeyValueMap) {
            System.out.println("Key2: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        // Initialize and set the adapter
        dynamicFieldsShowAdapter = new DynamicFieldsShowAdapter(FinalDynamicFieldsKeyValueMap, 16f, 16f, 45);
        Context context = getContext();
        if (context == null) {
            return;
        }

        binding.showDynamicFields.setLayoutManager(
                new LinearLayoutManager(context)
        );
        binding.showDynamicFields.setAdapter(dynamicFieldsShowAdapter);

    }


    private void callAttachmentApi(Integer id) {

        // Request permissions
        PermissionUtils.requestCameraAndPhotoPermissions(requireActivity());

        HashMap<String, Integer> ld = new HashMap<>();
        ld.put("lead_id", id);
        Call<LeadDocumentResponse> call = RetrofitClient.INSTANCE.getApiService().particularleadattachment(token,ld);
        call.enqueue(new Callback<LeadDocumentResponse>() {
            @Override
            public void onResponse(Call<LeadDocumentResponse> call, Response<LeadDocumentResponse> response) {
                if (response != null) {
                    if (response.body() != null) {
                        setAttachData(response.body().getData());
                    }

                }
            }

            @Override
            public void onFailure(Call<LeadDocumentResponse> call, Throwable t) {
                Log.e("Api_failure===>", t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.handlePermissionResult(requestCode, permissions, grantResults, requireActivity());
    }

    private void setAttachData(List<AttachDocument> data) {
        PreviousImageViewAdapter adapter = new PreviousImageViewAdapter(getContext(), data, "LeadDetail");
        binding.prevattachment.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
        binding.prevattachment.setAdapter(adapter);
        adapter.setOnDeleteItemClick(this);
        adapter.notifyDataSetChanged();
    }


    //todo call delete over ride function---
    @Override
    public void onDeleteItemClick(int attachId, Dialog dialog) {
        callAttachmentDeleteApi(attachId, dialog);
    }


    //todo call delete attachment api here---
    private void callAttachmentDeleteApi(int attachId, Dialog dialog) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", attachId);
        jsonObject.addProperty("lead_id", id);

        Call<LeadDocumentResponse> call = RetrofitClient.INSTANCE.getApiService().deleteLeadAttachment(jsonObject);
        call.enqueue(new Callback<LeadDocumentResponse>() {
            @Override
            public void onResponse(Call<LeadDocumentResponse> call, Response<LeadDocumentResponse> response) {

                if (response.code() == 200) {
                    if (response.body().getStatus() == 200) {
                        callAttachmentApi(id);
                        dialog.dismiss();
                        Log.d("DeleteAttachResponse =>", "onResponse: Successful");
                    } else {
                        Log.d("DeleteAttachNot200St", "onResponse: QuotAttachmentNot200Status");
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<LeadDocumentResponse> call, Throwable t) {
                Log.e("TAG_Attachment_Api", "onFailure: AttachmentAPi");
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // todo code for dynamic fields (Tarun Sharma)
    private void callDynamicFieldsApi() {

        // Make the API call
        Call<DynamicFieldsListModelClass> call = RetrofitClient.INSTANCE.getApiService().getDynamicFieldList(token,"lead");

        call.enqueue(new Callback<DynamicFieldsListModelClass>() {
            @Override
            public void onResponse(Call<DynamicFieldsListModelClass> call, Response<DynamicFieldsListModelClass> response) {
                if (response.isSuccessful() && response.body() != null) {

                    for (DynamicFieldsKeysResponse field : response.body().getData()) {
                        dynamicKeysFieldValuesMap.put(field.getField_name(), field.getField_value());
                    }

                    setDynamicFields();



                    Log.d("Field Values", dynamicKeysFieldValuesMap.toString());
                } else {
                    Log.e("API_ERROR", "Response unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DynamicFieldsListModelClass> call, Throwable t) {
                Log.e("API_ERROR", "Failed to connect: " + t.getMessage());
            }
        });
    }

    private void callApi(int id) {
        LeadValue lv = new LeadValue();
        lv.setId(id);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        Call<LeadResponse> call = RetrofitClient.INSTANCE.getApiService().particularlead(token,jsonObject);
        call.enqueue(new Callback<LeadResponse>() {
            @Override
            public void onResponse(Call<LeadResponse> call, Response<LeadResponse> response) {
                if (response != null) {
                    if (response.body() != null) {
                        leadValues = response.body().getData();
                        setData(leadValues.get(0));

                    }
                }
            }

            @Override
            public void onFailure(Call<LeadResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // TODO: Updated by Tarun for dynamic functionalities
    public void callLeadOneApi(int id) {
        // Prepare dynamic parameters
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);


        // Make the API call
        Call<ResponseBody> call = RetrofitClient.INSTANCE.getApiService().particularLeadNew(token,params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonString = response.body().string();
                        parseJsonDynamically(jsonString, "");
                        printHashMap();
                        callDynamicFieldsApi();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Failed to post data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }

    // todo code for dynamic fields (Tarun Sharma)
    public static void parseJsonDynamically(String jsonString, String parentKey) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            // Iterate through keys dynamically
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);

                String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key; // For nested keys

                if (value instanceof JSONObject) {
                    parseJsonDynamically(value.toString(), fullKey); // Recursive call for nested objects
                } else if (value instanceof JSONArray) {
                    JSONArray array = (JSONArray) value;

                    for (int i = 0; i < array.length(); i++) {
                        Object arrayValue = array.get(i);
                        if (arrayValue instanceof JSONObject) {
                            parseJsonDynamically(arrayValue.toString(), fullKey + "[" + i + "]"); // Recursive for objects in the array
                        } else {
                            // Handle array values and ensure they're not JSONObject.NULL
                            ListOneDetailKeyValueMap.put(fullKey + "[" + i + "]", arrayValue != JSONObject.NULL ? arrayValue.toString() : "null");
                        }
                    }
                } else {
                    // Handle normal key-value pairs and ensure null values are represented as "null"
                    ListOneDetailKeyValueMap.put(fullKey, value != JSONObject.NULL ? value.toString() : "null");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // todo code for dynamic fields (Tarun Sharma)
    public static void printHashMap() {
        System.out.println("HashMap Contents:");
        for (Map.Entry<String, String> entry : ListOneDetailKeyValueMap.entrySet()) {
            System.out.println("Tarun: " + entry.getKey() + " : " + entry.getValue());
        }
    }



    private void setData(LeadValue lv) {

        if (lv.getStatus().equalsIgnoreCase("Qualified")) {
            binding.createBp.setVisibility(View.VISIBLE);
        } else {
            binding.createBp.setVisibility(View.GONE);
        }
        leadtype = lv.getLeadType();
        status = lv.getStatus();
        //   status_spinner.setSelection(getStatuspos(lv.getStatus()));
        binding.companyName.setText(lv.getCompanyName());
        binding.contactPersonValue.setText(lv.getContactPerson());
        binding.phoneNumber.setText(lv.getPhoneNumber());
        if (lv.getEmail().isEmpty()) {
            binding.emailValue.setText("NA");
        } else {
            binding.emailValue.setText(lv.getEmail());
        }


        if (lv.getTurnover().isEmpty()) {
            binding.tvTurnOver.setText("\u20B9 0");
        } else {

            binding.tvTurnOver.setText("\u20B9 " + lv.getTurnover());
        }


        binding.dateValue.setText(Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(lv.getDate()));

        binding.tvCategory.setText("\u20B9 " + lv.getTurnover());
        //  binding.tvProjectAmount.setText(lv.getProjectAmount());
        binding.tvLeadstatus.setText(lv.getStatus());
        binding.tvLeadSource.setText(lv.getSource());
        binding.tvLeadPriority.setText(lv.getLeadType());
     /*   if (lv.getCustomerName().isEmpty()) {
            binding.tvCustomerName.setText("NA");
        } else {
            binding.tvCustomerName.setText(lv.getCustomerName());
        }*/
        if (lv.getLocation().isEmpty()) {
            binding.tvAddress.setText("NA");
        } else {
            binding.tvAddress.setText(lv.getLocation());
        }

        if (lv.getEmployeeId() != null) {
            if (lv.getEmployeeId().getFirstName().isEmpty()) {
                binding.tvCreatedBy.setText("NA");
            } else {
                binding.tvCreatedBy.setText(lv.getEmployeeId().getFirstName());
            }

        } else {
            binding.tvCreatedBy.setText("NA");
        }
        if (lv.getAssignedTo() != null) {
            if (lv.getAssignedTo().getFirstName().isEmpty()) {
                binding.tvAssignTo.setText("NA");
            } else {
                binding.tvAssignTo.setText(lv.getAssignedTo().getFirstName());
            }

        } else {
            binding.tvAssignTo.setText("NA");
        }

        if (lv.getMessage().isEmpty()) {
            binding.tvRemarks.setText("NA");
        } else {
            binding.tvRemarks.setText(lv.getMessage());
        }

        if (lv.getProductInterest().isEmpty()) {
            binding.productInterest.setText("NA");
        } else {
            binding.productInterest.setText(lv.getProductInterest());
        }


        if (lv.getDesignation().isEmpty()) {
            binding.designation.setText("NA");
        } else {
            binding.designation.setText(lv.getDesignation());
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_press:
                getActivity().onBackPressed();
                break;
            case R.id.create_bp:
                Prefs.putString(Globals.AddBp, "Lead");
                Intent intent = new Intent(context, AddBPCustomer.class);
                intent.putExtra(Globals.AddBp, leadValue);
                context.startActivity(intent);
                break;
            case R.id.history:

                Bundle bundle = new Bundle();
                bundle.putParcelable(Globals.Lead_Data, leadValue);
                LeadFollowUpFragment chatterFragment = new LeadFollowUpFragment();
                chatterFragment.setArguments(bundle);
                FragmentTransaction chattransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                chattransaction.add(R.id.customer_lead, chatterFragment).addToBackStack(null);
                chattransaction.commit();
                break;

            case R.id.attachment:
              /*  if (PermissionUtils.checkPermission(getActivity())) {
                    //    openGallery();


                }*/
                // intentDispatcher();
              /*  imageSelector = new ImageSelector();

                imageSelector.openImageSelector(getActivity());*/

                Intent imgintent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                imgintent.setType("image/*");

                imagePickerLauncher.launch(imgintent);
                break;

        }
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {

                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                // Handle the selected image URI here
                                Log.d("checkImage", imageUri.toString());
                                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                                Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColumn, null, null, null);

                                if (cursor != null) {
                                    cursor.moveToFirst();
                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                    picturePath = cursor.getString(columnIndex);
                                    cursor.close();

                                }

                                binding.loader.setVisibility(View.VISIBLE);
                                updateattachment();
                            } else {
                                // show this if no image is selected
                                Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );
    private static final int RESULT_LOAD_IMAGE = 101;

    //todo select attachment ---
    private void intentDispatcher() {


        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE);
    }

    private void openimageuploader() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
//                            Matisse.from(getActivity())
//                                    .choose(MimeType.ofAll())
//                                    .countable(true)
//                                    .maxSelectable(5)
//                                    .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
//                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                                    .thumbnailScale(0.85f)
//                                    .imageEngine(new GlideEngine())
//                                    .showPreview(false) // Default is `true`
//                                    .forResult(REQUEST_CODE_CHOOSE);
                           /* Intent intent = new Intent();

                            // setting type to select to be image
                            intent.setType("image/*");

                            // allowing multiple image to be selected
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_CHOOSE);*/
                        } else {

                            Toast.makeText(getContext(), "Please enable permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    List<Uri> mSelected = new ArrayList<>();
    List<String> path = new ArrayList<>();
    String picturePath = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImageUri = imageSelector.getImageUriFromResult(requestCode, resultCode, data);
        if (selectedImageUri != null) {
            // Handle the selected image URI here
            Log.d("checkImage", selectedImageUri.toString());
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                Log.e("picturePath", picturePath);

            }

            binding.loader.setVisibility(View.VISIBLE);
            updateattachment();
        } else {
            // show this if no image is selected
            Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

    }

    private void updateattachment() {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("lead_id", String.valueOf(leadValues.get(0).getId()));
        builder.addFormDataPart("CreatedBy", Globals.empCode);
        builder.addFormDataPart("CreateDate", Globals.getTodaysDatervrsfrmt());
        builder.addFormDataPart("CreateTime", Globals.getTCurrentTime());

        File file = new File(picturePath);
        builder.addFormDataPart("Attach", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));

        MultipartBody requestBody = builder.build();

        Call<LeadResponse> call = RetrofitClient.INSTANCE.getApiService().updateLeadattachment(token,requestBody);
        call.enqueue(new Callback<LeadResponse>() {
            @Override
            public void onResponse(Call<LeadResponse> call, Response<LeadResponse> response) {

                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getStatus() == 200) {
                        picturePath = "";
                        Toast.makeText(requireContext(), "Add Successfully", Toast.LENGTH_LONG).show();
                        callAttachmentApi(leadValues.get(0).getId());
                    } else {
                        Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {

                }
                binding.loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LeadResponse> call, Throwable t) {
                binding.loader.setVisibility(View.GONE);
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void checkStoragePermission() {
        // Check if the permission has been granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_CHOOSE);

        } else {
            openimageuploader();

        }
    }


    private void openGallery() {
        GalleryUtils.openGallery(getActivity(), REQUEST_CODE_CHOOSE);
    }


}
