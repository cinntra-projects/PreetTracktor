package com.preetTractor.galaxyAndroid.ui.activity;

import static com.preetTractor.galaxyAndroid.helper.Globals.SelectedItems;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;
import com.preetTractor.galaxyAndroid.R;
import com.preetTractor.galaxyAndroid.adapter.AssignToAdapter;
import com.preetTractor.galaxyAndroid.adapter.DynamicTypeAdapter;
import com.preetTractor.galaxyAndroid.adapter.LeadDropDownAdapter;
import com.preetTractor.galaxyAndroid.adapter.StateAutoAdapter;
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient;
import com.preetTractor.galaxyAndroid.data.DynamicField.DynamicFieldResponse;
import com.preetTractor.galaxyAndroid.data.DynamicField.DynamicFieldResponseData;
import com.preetTractor.galaxyAndroid.data.LeadResponse;
import com.preetTractor.galaxyAndroid.data.LeadTypeData;
import com.preetTractor.galaxyAndroid.data.LeadTypeResponse;
import com.preetTractor.galaxyAndroid.data.SaleEmployeeResponse;
import com.preetTractor.galaxyAndroid.data.SalesEmployeeItem;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.FieldData;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.UserAccessData;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.UserAccessManagementResponse;
import com.preetTractor.galaxyAndroid.data.UserAccessStorageHelper;
import com.preetTractor.galaxyAndroid.databinding.CreateLeadFromBinding;
import com.preetTractor.galaxyAndroid.helper.Globals;
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh;
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient;
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.StateData;
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.StateRespose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddLead extends BaseActivity {
    public int ITEMSVIEWCODE = 10000;


    String status = "Follow up";
    String leadtype = "";
    String sourcetype = "";
    ArrayList<LeadTypeData> leadTypeData = new ArrayList<>();
    ArrayList<LeadTypeData> sourceData = new ArrayList<>();
    com.preetTractor.galaxyAndroid.databinding.CreateLeadFromBinding binding;


    private RecyclerView recyclerView;
    private DynamicTypeAdapter dynamicTypeAdapter;
    private ArrayList<DynamicFieldResponseData> dynamicFieldResponseDataList;

    // todo Declare a boolean variable to track mandatory validation
    private boolean isCompanyNameMandatory = true;
    private boolean isContactPersonMandatory = true;
    private boolean isPersonDesignationMandatory = false;
    private boolean isPhoneNumberMandatory = true;
    private boolean isEmailMandatory = false;
    private boolean isLocationMandatory = true;
    private boolean isSourceSpinnerInputLayoutMandatory = true;
    private boolean isProductInterestInputLayoutMandatory = false;
    private boolean iSetNumOfEmployeeMandatory = false;
    private boolean isTurnOverMandatory = false;
    private boolean isLeadTypeInputLayoutMandatory = false;
    private boolean isAssignedToInputLayoutMandatory = true;
    private boolean isRemarkMandatory = false;
    private boolean isStatusMandatory = true;
    String  token = "Token " + Globals.GalaxyVistaToken;
    @Override
    protected void onResume() {
        super.onResume();
        binding.itemCount.setText("Item (" + SelectedItems.size() + ")");

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CreateLeadFromBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // ButterKnife.bind(this);
        binding.headerBottomRounded.headTitle.setText("Add Lead");
        SelectedItems.clear();

        callStaticFieldsDynamicApiValue();

        callleadTypeApi();

        callStateAPi();
        callAssignToApi();

        callSourceApi();

        eventmanager();

        callDynamicFieldApi();


    }

    private void callStaticFieldsDynamicApiValue() {

        // ✅ Read stored response from file
        UserAccessManagementResponse storedResponse = UserAccessStorageHelper.readJsonFromFile(this);

        // ✅ Print stored response
        if (storedResponse != null) {
            Log.d("ACTIVITY2_RESPONSE", "=== Retrieved Stored Response ===");

            for (UserAccessData data : storedResponse.getData()) {
                if (data.getModule_name().equals("Lead")) {

                    // ✅ Check if fieldData list exists
                    if (data.getData() != null) {
                        for (FieldData field : data.getData()) {
                            String label = field.getLabel();
                            boolean isMandatory = field.isMandatory();
                            SpannableStringBuilder spannable = new SpannableStringBuilder(label);

                            if (isMandatory) {
                                label += " *";
                                spannable = new SpannableStringBuilder(label);
                                spannable.setSpan(new ForegroundColorSpan(Color.RED), label.length() - 1, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }

                            switch (field.getKey()) {
                                case "companyName":
                                    isCompanyNameMandatory = isMandatory;
                                    binding.companyNameText.setText(spannable);
                                    binding.companyname.setHint(field.getLabel());
                                    break;

                                case "contactPerson":
                                    isContactPersonMandatory = isMandatory;
                                    binding.personNameText.setText(spannable);
                                    binding.personName.setHint(field.getLabel());
                                    break;

                                case "designation":
                                    isPersonDesignationMandatory = isMandatory;
                                    binding.personDesignationText.setText(spannable);
                                    binding.designation.setHint(field.getLabel());
                                    break;

                                case "phoneNumber":
                                    isPhoneNumberMandatory = isMandatory;
                                    binding.phoneText.setText(spannable);
                                    binding.contactNo.setHint(field.getLabel());
                                    break;

                                case "email":
                                    isEmailMandatory = isMandatory;
                                    binding.emailText.setText(spannable);
                                    binding.email.setHint(field.getLabel());
                                    break;

                                case "location":
                                    isLocationMandatory = isMandatory;
                                    binding.locationText.setText(spannable);
                                    binding.location.setHint(field.getLabel());
                                    break;

                                case "source":
                                    isSourceSpinnerInputLayoutMandatory = isMandatory;
                                    binding.sourceText.setText(spannable);
                                    binding.sourceSpinnerInputLayout.setHint(field.getLabel());
                                    break;

                                case "productInterest":
                                    isProductInterestInputLayoutMandatory = isMandatory;
                                    binding.productInterestText.setText(spannable);
                                    binding.etProductInterest.setHint(field.getLabel());
                                    break;

                                case "numOfEmployee":
                                    iSetNumOfEmployeeMandatory = isMandatory;
                                    binding.numOfEmployeeText.setText(spannable);
                                    binding.etNumOfEmployee.setHint(field.getLabel());
                                    break;

                                case "turnover":
                                    isTurnOverMandatory = isMandatory;
                                    binding.turnoverText.setText(spannable);
                                    binding.etTurnOver.setHint(field.getLabel());
                                    break;

                                case "lead_type":
                                    isLeadTypeInputLayoutMandatory = isMandatory;
                                    binding.leadPriorityText.setText(spannable);
                                    binding.leadTypeInputLayout.setHint(field.getLabel());
                                    break;

                                case "status":
                                    isStatusMandatory = isMandatory;
                                    binding.statusText.setText(spannable);
                                    break;

                                case "assignedTo":
                                    isAssignedToInputLayoutMandatory = isMandatory;
                                    binding.assignedToText.setText(spannable);
                                    binding.assignedToInputLayout.setHint(field.getLabel());
                                    break;

                                case "message":
                                    isRemarkMandatory = isMandatory;
                                    binding.remarksText.setText(spannable);
                                    binding.comment.setHint(field.getLabel());
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


    private void callDynamicFieldApi() {

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.dynamicFieldRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data list
        dynamicFieldResponseDataList = new ArrayList<>();

        fetchDynamicFields();

        // Initialize adapter and set it to RecyclerView
        List<Map.Entry<String, String>> emptyList = new ArrayList<>();
        dynamicTypeAdapter = new DynamicTypeAdapter("",emptyList, dynamicFieldResponseDataList, this);
        recyclerView.setAdapter(dynamicTypeAdapter);
    }


    private void fetchDynamicFields() {

        Call<DynamicFieldResponse> call =
                 RetrofitClient.INSTANCE.getApiService().getDynamicFields(token,"lead");
        call.enqueue(new Callback<DynamicFieldResponse>() {
            @Override
            public void onResponse(Call<DynamicFieldResponse> call, Response<DynamicFieldResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int status = response.body().getStatus();
                    Log.d("FetchDynamicFields", "Response status: " + status);

                    switch (status) {
                        case 200:
                            dynamicFieldResponseDataList.clear();
                            dynamicFieldResponseDataList.addAll(response.body().getData());
                            Log.d("FetchDynamicFields", "Data size: " + dynamicFieldResponseDataList.size());
                            dynamicTypeAdapter.notifyDataSetChanged();
                            break;

                        case 201:
                        case 500:
                            String message = response.body().getMessage();
                            Toast.makeText(AddLead.this, message, Toast.LENGTH_LONG).show();
                            break;

                        default:
                            handleErrorResponse(response);
                            break;
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            private void handleErrorResponse(Response<DynamicFieldResponse> response) {
                try {
                    if (response.errorBody() != null) {
                        String error = response.errorBody().string();
                        Gson gson = new GsonBuilder().create();
                        StateRespose errorResponse = gson.fromJson(error, StateRespose.class);
                        String errorMessage = errorResponse.getMessage() != null ? errorResponse.getMessage() : "An error occurred.";
                        Toast.makeText(AddLead.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("FetchDynamicFields", "Error response: " + errorMessage);
                    }
                } catch (IOException e) {
                    Log.e("FetchDynamicFields", "Error reading error response: " + e.getMessage());
                    Toast.makeText(AddLead.this, "Failed to parse error response.", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<DynamicFieldResponse> call, Throwable t) {
                Log.e("FetchDynamicFields", "API call failed: " + t.getMessage());
                Toast.makeText(AddLead.this, "Failed to fetch data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    ArrayList<SalesEmployeeItem> assignToList_gl = new ArrayList<>();

    //todo calling assign to api here---
    private void callAssignToApi() {

        SalesEmployeeItem employeeValue = new SalesEmployeeItem();
        employeeValue.setSalesEmployeeCode(Globals.SalesEmployeeCode);
        Call<SaleEmployeeResponse> call = RetrofitClient.INSTANCE.getApiService().getSalesEmplyeeDataList(token,employeeValue);
        call.enqueue(new Callback<SaleEmployeeResponse>() {
            @Override
            public void onResponse(Call<SaleEmployeeResponse> call, Response<SaleEmployeeResponse> response) {
                assert response.body() != null;
                if (response.body().getStatus() == 200) {
                    assignToList_gl.clear();
                    if (!response.body().getValue().isEmpty()) {
                        assignToList_gl.addAll(response.body().getValue());
                        AssignToAdapter stateAdapter = new AssignToAdapter(AddLead.this, R.layout.drop_down_textview, assignToList_gl);

                        //todo set state..
                        int size = assignToList_gl.size() - 1;
                        String defaultUsername = assignToList_gl.get(size).getFirstName();
                        binding.acAssignTo.setText(defaultUsername);
                        binding.acAssignTo.setAdapter(stateAdapter);
                        stateAdapter.notifyDataSetChanged();
//                        billtoState = billStateList.get(0).getName();
//                        billtoStateCode = billStateList.get(0).getCode();
                    }

                } else{
                    Toast.makeText(AddLead.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SaleEmployeeResponse> call, Throwable t) {
                Toast.makeText(AddLead.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void callleadTypeApi() {

        Call<LeadTypeResponse> call = RetrofitClient.INSTANCE.getApiService().getLeadType(token);
        call.enqueue(new Callback<LeadTypeResponse>() {
            @Override
            public void onResponse(Call<LeadTypeResponse> call, Response<LeadTypeResponse> response) {

                if (response.code() == 200) {


                    leadTypeData.clear();
                    leadTypeData.addAll(response.body().getData());
                    Log.d("leadTypeData", leadTypeData.toString());
                    binding.leadTypeSpinner.setText(leadTypeData.get(0).getName().toString());
                    leadtype = leadTypeData.get(0).getName();
                    binding.leadTypeSpinner.setAdapter(new LeadDropDownAdapter(AddLead.this, R.layout.drop_down_textview, leadTypeData));
//                    leadtype = leadTypeData.get(0).getName();
                } else {

                }
                //  loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LeadTypeResponse> call, Throwable t) {
                // loader.setVisibility(View.GONE);
                Toast.makeText(AddLead.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    ArrayList<StateData> stateList_gl = new ArrayList<>();

    //todo calling  state api --
    private void callStateAPi() {
        StateData stateData = new StateData();
        stateData.setCountry("IN");
        Call<StateRespose> call = RetrofitClient.INSTANCE.getApiService().getStateList(stateData);
        call.enqueue(new Callback<StateRespose>() {
            @Override
            public void onResponse(Call<StateRespose> call, Response<StateRespose> response) {

                if (response.body().getStatus() == 200) {
                    stateList_gl.clear();
                    if (response.body().getData().size() > 0) {
                        stateList_gl.addAll(response.body().getData());
                        StateAutoAdapter stateAdapter = new StateAutoAdapter(AddLead.this, R.layout.drop_down_textview, stateList_gl);

                        //todo set state..
                        binding.acState.setAdapter(stateAdapter);
                        stateAdapter.notifyDataSetChanged();
//                        billtoState = billStateList.get(0).getName();
//                        billtoStateCode = billStateList.get(0).getCode();
                    }

                } else  {
                    Toast.makeText(AddLead.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StateRespose> call, Throwable t) {

                Toast.makeText(AddLead.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void callSourceApi() {


        Call<LeadTypeResponse> call = RetrofitClient.INSTANCE.getApiService().getsourceType(token);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LeadTypeResponse> call, Response<LeadTypeResponse> response) {

                if (response.body().getStatus() == 200) {
                    sourceData.clear();

                    sourceData.addAll(response.body().getData());
                    binding.sourceSpinner.setAdapter(new LeadDropDownAdapter(AddLead.this, R.layout.drop_down_textview, sourceData));
                    binding.sourceSpinner.setThreshold(1);
                    if (!response.body().getData().isEmpty()) {
                        sourcetype = response.body().getData().get(0).getName();
                        binding.sourceSpinner.setText(sourcetype);
                    }

//                    sourcetype = sourceData.get(0).getName();

                } else {
                 /*   //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                        Toast.makeText(AddLead.this, mError.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }*/
                }
                binding.loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LeadTypeResponse> call, Throwable t) {
                binding.loader.setVisibility(View.GONE);
                Toast.makeText(AddLead.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

      /*  sourceData.clear();
        sourceData.addAll(MainActivity.leadSourceListFromLocal);
        binding.sourceSpinner.setAdapter(new LeadTypeAdapter(AddLead.this, sourceData));
        sourcetype = sourceData.get(0).getName();*///todo comment


    }

    String selectedCategory = "";

    String StateName = "";
    String StateCode = "";
    String AssignName = "";
    String AssignCode = "";
    String ProductInterestName = "";


    List<String> productInterestList_gl = Arrays.asList(Globals.productInterestList_gl);

    private void eventmanager() {

        //todo set bill to item click of autocomplete state
        binding.acState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (stateList_gl.size() > 0) {
                    StateName = stateList_gl.get(position).getName();
                    StateCode = stateList_gl.get(position).getCode();

                    binding.acState.setText(stateList_gl.get(position).getName());
                }

            }
        });


        //todo set assign to item click of autocomplete
        binding.acAssignTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (assignToList_gl.size() > 0) {
                    AssignName = assignToList_gl.get(position).getFirstName();
                    AssignCode = assignToList_gl.get(position).getSalesEmployeeCode();

                    binding.acAssignTo.setText(assignToList_gl.get(position).getFirstName());
                }

            }
        });


        //todo bind product interest adapter with item click--
        ArrayAdapter<String> productInterestAdapter = new ArrayAdapter<>(AddLead.this, R.layout.drop_down_textview, productInterestList_gl);
        binding.acProductInterest.setAdapter(productInterestAdapter);


        binding.acProductInterest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (productInterestList_gl.size() > 0) {
                    binding.acProductInterest.setText(productInterestList_gl.get(position));
                    ProductInterestName = productInterestList_gl.get(position);

                    if (ProductInterestName == "Other") {
                        binding.productDetailLayout.setVisibility(View.VISIBLE);
                    } else {
                        binding.productDetailLayout.setVisibility(View.GONE);
                    }

                    ArrayAdapter<String> productInterestAdapter = new ArrayAdapter<>(AddLead.this, R.layout.drop_down_textview, productInterestList_gl);
                    binding.acProductInterest.setAdapter(productInterestAdapter);


                }
            }
        });

        binding.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String validationMessage = dynamicTypeAdapter.validateFields();



                if (!validationMessage.isEmpty()) {
                    Toast.makeText(AddLead.this, validationMessage, Toast.LENGTH_SHORT).show();

                } else {
                    addcreatelead();
                }
            }
        });

        binding.headerBottomRounded.backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Leadstatus", parent.getSelectedItem().toString());
                status = parent.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                status = parent.getSelectedItem().toString();
            }
        });


        binding.leadTypeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("leadPriorityData", leadTypeData.get(position).getName());
                leadtype = leadTypeData.get(position).getName();
                binding.leadTypeSpinner.setText(leadTypeData.get(position).getName());
            }
        });


        binding.sourceSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sourcetype = sourceData.get(position).getName();
                binding.sourceSpinner.setText(sourceData.get(position).getName());
            }
        });


        // Retrieve items from strings.xml
        String[] itemList = getResources().getStringArray(R.array.category);
        selectedCategory = itemList[0];

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectCategorySpinner.setAdapter(adapter);

        binding.selectCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = (String) parentView.getItemAtPosition(position);
                selectedCategory = selectedItem;
                //  Toast.makeText(AddLead.this, "Selected Item: " + selectedItem + "\nPosition: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

                // Do nothing here if no item is selected
            }
        });

    }

    private void addcreatelead() {
        if (validateFields()) {

            JsonObject lv = new JsonObject();

            lv.addProperty("date", Globals.getTodaysDatervrsfrmt());
            lv.addProperty("location", binding.location.getText().toString());
            lv.addProperty("companyName", binding.companyname.getText().toString());
            lv.addProperty("source", sourcetype);
            lv.addProperty("contactPerson", binding.personName.getText().toString());
            lv.addProperty("phoneNumber", binding.contactNo.getText().toString());
            lv.addProperty("message", binding.comment.getText().toString());
            lv.addProperty("email", binding.email.getText().toString());
            lv.addProperty("productInterest", binding.etProductInterest.getText().toString());
            lv.addProperty("assignedTo", !AssignCode.isEmpty() ? AssignCode : Globals.SalesEmployeeCode);
            lv.addProperty("timestamp", Globals.getTimestamp());
            lv.addProperty("employeeId", Globals.SalesEmployeeCode);
            lv.addProperty("numOfEmployee", binding.etNumOfEmployee.getText().toString().trim().isEmpty() ? "0" : binding.etNumOfEmployee.getText().toString());
            lv.addProperty("turnover", binding.etTurnOver.getText().toString());
            lv.addProperty("designation", binding.designation.getText().toString());
            lv.addProperty("status", status);
            lv.addProperty("leadType", leadtype);
            lv.addProperty("Attach", "");
            lv.addProperty("Caption", "");

            // Dynamic fields setup
            List<DynamicFieldResponseData> dynamicFieldsData = dynamicTypeAdapter.getUpdatedFields();

            for (DynamicFieldResponseData field : dynamicFieldsData) {
                // Add each dynamic field directly to the JSON object
                lv.addProperty(field.getField_name(), field.getField_value());
            }


            JsonArray payloadArray = new JsonArray();
            payloadArray.add(lv);

                binding.loader.setVisibility(View.VISIBLE);
                binding.create.setEnabled(false);
                callcreateLeadApi(payloadArray);

        }
    }

    // Validation function based on mandatory fields
    private boolean validateFields() {
        boolean isValid = true;

        if (isCompanyNameMandatory && binding.companyname.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Enter " + binding.companyname.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }

         if (isContactPersonMandatory && binding.personName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter " + binding.personName.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

         if (isPersonDesignationMandatory && binding.designation.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter " + binding.designation.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

         if (isPhoneNumberMandatory) {
            String phone = binding.contactNo.getText().toString().trim();
            if (phone.isEmpty()) {
                binding.contactNo.requestFocus();
                binding.contactNo.setError("Phone no. is Required");
                Toast.makeText(this, "Enter " + binding.contactNo.getHint(), Toast.LENGTH_SHORT).show();
                isValid = false;
            } else if (phone.length() != 10) {
                binding.contactNo.requestFocus();
                binding.contactNo.setError("Enter Valid Contact No");
                Toast.makeText(this, "Enter Valid " + binding.contactNo.getHint(), Toast.LENGTH_SHORT).show();
                isValid = false;
            }
             return isValid;
        }

         if (isEmailMandatory) {
            String email = binding.email.getText().toString().trim();
            if (email.isEmpty()) {
                binding.email.requestFocus();
                binding.email.setError("Enter " + binding.email.getHint());
                Toast.makeText(this, "Enter " + binding.email.getHint(), Toast.LENGTH_SHORT).show();
                isValid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.email.requestFocus();
                binding.email.setError("Invalid Email Address");
                Toast.makeText(this, "Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
             return isValid;
        }

         if (isLocationMandatory && binding.location.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Enter " + binding.location.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

         if (isSourceSpinnerInputLayoutMandatory && sourcetype.trim().isEmpty()) {
            Toast.makeText(this, "Select Source", Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

         if (isStatusMandatory && status.trim().isEmpty()) {
            String statusText = binding.statusText.getText().toString().replace("*", "").trim();

            Toast.makeText(this, "Select Status", Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }


         if (iSetNumOfEmployeeMandatory && binding.etNumOfEmployee.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Enter " + binding.etNumOfEmployee.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

         if (isTurnOverMandatory && binding.etTurnOver.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Enter " + binding.etTurnOver.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

         if (isLeadTypeInputLayoutMandatory && leadtype.trim().isEmpty()) {
            Toast.makeText(this, "Select " + binding.leadTypeSpinner.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

         if (isAssignedToInputLayoutMandatory && binding.acAssignTo.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Enter " + binding.assignedToInputLayout.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

         if (isRemarkMandatory && binding.comment.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Enter " + binding.comment.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

        // ✅ Product Interest Validation
         if (isProductInterestInputLayoutMandatory && binding.productInterestText.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Select " + binding.productInterestInputLayout.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
             return isValid;
        }

        return isValid;
    }





    private void callcreateLeadApi(JsonArray lv) {

        Call<LeadResponse> call = RetrofitClient.INSTANCE.getApiService().createLead(token,lv);
        call.enqueue(new Callback<LeadResponse>() {
            @Override
            public void onResponse(Call<LeadResponse> call, Response<LeadResponse> response) {

                if (response.body().getStatus() == 200) {
                    binding.create.setEnabled(true);
                    if (response.body().getMessage().equalsIgnoreCase("successful")) {

                        SelectedItems.clear();
                        Toast.makeText(AddLead.this, "Add Successfully", Toast.LENGTH_LONG).show();

                        onBackPressed();
                    } else {
                        Toast.makeText(AddLead.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else if (response.body().getStatus() == 201) {
                    Toast.makeText(AddLead.this, response.body().getMessage(), Toast.LENGTH_LONG).show();


                } else {
                    binding.create.setEnabled(true);
                    Toast.makeText(AddLead.this, response.message(), Toast.LENGTH_LONG).show();

                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());Gson
                }
                binding.loader.setVisibility(View.GONE);
                binding.create.setEnabled(true);
            }

            @Override
            public void onFailure(Call<LeadResponse> call, Throwable t) {
                binding.loader.setVisibility(View.GONE);
                binding.create.setEnabled(true);
                Toast.makeText(AddLead.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validation(EditText personName, EditText companyName, EditText contact_no, String sourceTypeInner, String statusinner, EditText email) {

        if (companyName.getText().toString().trim().isEmpty()) {
            companyName.requestFocus();
            companyName.setError("Enter Company Name");
            Toast.makeText(this, "Select " + companyName.getHint(), Toast.LENGTH_SHORT).show();
            return false;
        } else if (personName.getText().toString().trim().isEmpty()) {
            personName.requestFocus();
            personName.setError("Enter Person Name");
            Toast.makeText(this, "Enter " + personName.getHint(), Toast.LENGTH_SHORT).show();
            return false;
        } else if (contact_no.getText().toString().trim().isEmpty()) {
            contact_no.requestFocus();
            contact_no.setError("Phone no. is Required");
            Toast.makeText(this, "Select " + contact_no.getHint(), Toast.LENGTH_SHORT).show();
            return false;
        } else if (contact_no.getText().toString().trim().length() != 10) {
            contact_no.requestFocus();
            contact_no.setError("Enter Valid Contact No");
            Toast.makeText(this, "Enter " + contact_no.getHint(), Toast.LENGTH_SHORT).show();
            return false;
        } else if (sourceTypeInner.trim().isEmpty()) {
            Toast.makeText(this, "Select Source Type Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (statusinner.trim().isEmpty()) {
            Toast.makeText(this, "Select Status", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!email.getText().toString().trim().isEmpty()) {
            if (!isvalidateemail(email.getText().toString())) {  // Fixed email validation call
                email.requestFocus();
                email.setError("This email address is not valid");
                return false;
            }
        }

        return true;
    }

    // ✅ Fixed Email Validation Method
    private boolean isvalidateemail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isvalidateemail() {
        String checkEmail = binding.email.getText().toString();
        boolean hasSpecialEmail = Patterns.EMAIL_ADDRESS.matcher(checkEmail).matches();
        if (!hasSpecialEmail) {
            binding.email.setError("This email address is not valid");
            return true;
        }
        return false;
    }

}
