package com.preetTractor.galaxyAndroid.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;
import com.preetTractor.galaxyAndroid.R;
import com.preetTractor.galaxyAndroid.adapter.AssignToAdapter;
import com.preetTractor.galaxyAndroid.adapter.DynamicTypeAdapter;
import com.preetTractor.galaxyAndroid.adapter.LeadTypeAdapter;
import com.preetTractor.galaxyAndroid.adapter.StateAutoAdapter;
import com.preetTractor.galaxyAndroid.data.DynamicField.DynamicFieldResponse;
import com.preetTractor.galaxyAndroid.data.DynamicField.DynamicFieldResponseData;
import com.preetTractor.galaxyAndroid.data.DynamicFieldsKeysResponse;
import com.preetTractor.galaxyAndroid.data.DynamicFieldsListModelClass;
import com.preetTractor.galaxyAndroid.data.EmployeeValue;
import com.preetTractor.galaxyAndroid.data.GlobalResponse;
import com.preetTractor.galaxyAndroid.data.LeadResponse;
import com.preetTractor.galaxyAndroid.data.LeadTypeData;
import com.preetTractor.galaxyAndroid.data.LeadTypeResponse;
import com.preetTractor.galaxyAndroid.data.LeadValue;
import com.preetTractor.galaxyAndroid.data.SaleEmployeeResponse;
import com.preetTractor.galaxyAndroid.data.SalesEmployeeItem;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.FieldData;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.UserAccessData;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.UserAccessManagementResponse;
import com.preetTractor.galaxyAndroid.data.UserAccessStorageHelper;
import com.preetTractor.galaxyAndroid.databinding.LeadDetailBinding;
import com.preetTractor.galaxyAndroid.helper.Globals;
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh;
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient;
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.StateData;
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.StateRespose;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeadDetail extends Fragment implements View.OnClickListener {

    LeadValue leadValue;
    Context leadsActivity;
    List<LeadValue> leadValues = new ArrayList<>();
    String[] leadstatus = new String[4];
    String status = "";
    String leadtype = "";
    Integer id;
    ArrayList<LeadTypeData> leadTypeData = new ArrayList<>();
    Context context;

    LeadDetailBinding binding;
    String selectedCategory = "";

    String StateName = "";
    String StateCode = "";
    String AssignName = "";
    String AssignCode = "";
    String ProductInterestName = "";
    String sourcetype = "";
    String  token = "Token " + Globals.GalaxyVistaToken;

    private DynamicTypeAdapter dynamicTypeAdapter;
    private ArrayList<DynamicFieldResponseData> dynamicFieldResponseDataList;

    //todo tarun1 new
    HashMap<String, String> dynamicKeysFieldValuesMap = new HashMap<>();
    List<Map.Entry<String, String>> FinalDynamicFieldsKeyValueMap = new ArrayList<>();
    private static final HashMap<String, String> ListOneDetailKeyValueMap = new HashMap<>();

    // todo Declare a boolean variable to track mandatory validation
    private boolean isCompanyNameMandatory = true;
    private boolean isContactPersonMandatory = true;
    private boolean isPersonDesignationMandatory = false;
    private boolean isPhoneNumberMandatory = true;
    private boolean isEmailMandatory = false;
    private boolean isLocationMandatory = false;
    private boolean isSourceSpinnerInputLayoutMandatory = true;
    private boolean isProductInterestInputLayoutMandatory = false;
    private boolean iSetNumOfEmployeeMandatory = false;
    private boolean isTurnOverMandatory = false;
    private boolean isLeadTypeInputLayoutMandatory = false;
    private boolean isAssignedToInputLayoutMandatory = true;
    private boolean isRemarkMandatory = false;
    private boolean isStatusMandatory = true;



    // Define the interface for callback
    public interface OnLeadUpdatedListener {
        void onLeadUpdated();
    }

    private OnLeadUpdatedListener listener;


    List<String> productInterestList_gl = Arrays.asList(Globals.productInterestList_gl);

    public LeadDetail(Context context) {
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
        if (context instanceof OnLeadUpdatedListener) {
            listener = (OnLeadUpdatedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnLeadUpdatedListener");
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = LeadDetailBinding.inflate(inflater, container, false);

        callStaticFieldsDynamicApiValue();

        binding.headerBottomRounded.headTitle.setText("Lead Detail");
        binding.headerBottomRounded.backPress.setOnClickListener(this);
        leadstatus = getResources().getStringArray(R.array.lead_status);
        binding.loader.setVisibility(View.VISIBLE);
        eventManager();
       
            callStateAPi();

            callAssignToApi();

            callSourceApi();
            


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
//                                    binding.sourceSpinnerInputLayout.setHint(field.getLabel());
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
//                                    binding.leadTypeInputLayout.setHint(field.getLabel());
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

    private void callDynamicFieldApi(List<Map.Entry<String, String>> FinalDynamicFieldsKeyValueMap1) {


        binding.dynamicFieldUpdateRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize data list
        dynamicFieldResponseDataList = new ArrayList<>();

        fetchDynamicFields();

        List<Map.Entry<String, String>> emptyList = new ArrayList<>();
        dynamicTypeAdapter = new DynamicTypeAdapter("Update", FinalDynamicFieldsKeyValueMap1, dynamicFieldResponseDataList, requireContext());

        binding.dynamicFieldUpdateRecyclerView.setAdapter(dynamicTypeAdapter);
    }



    private void fetchDynamicFields() {

        Call<DynamicFieldResponse> call = RetrofitClient.INSTANCE.getApiService().getDynamicFields(token,"lead");
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
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("FetchDynamicFields", "Error response: " + errorMessage);
                    }
                } catch (IOException e) {
                    Log.e("FetchDynamicFields", "Error reading error response: " + e.getMessage());
                    Toast.makeText(requireContext(), "Failed to parse error response.", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<DynamicFieldResponse> call, Throwable t) {
                Log.e("FetchDynamicFields", "API call failed: " + t.getMessage());
                Toast.makeText(requireContext(), "Failed to fetch data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    if (!Objects.requireNonNull(response.body().getData()).isEmpty()) {
                        stateList_gl.addAll(response.body().getData());
                        StateAutoAdapter stateAdapter = new StateAutoAdapter(getActivity(), R.layout.drop_down_textview, stateList_gl);

                        //todo set state..
                        binding.acState.setAdapter(stateAdapter);
                        stateAdapter.notifyDataSetChanged();
//                        billtoState = billStateList.get(0).getName();
//                        billtoStateCode = billStateList.get(0).getCode();
                    }

                } else if (response.body().getStatus() == 201) {
                    Toast.makeText(getActivity(), response.body().getMessage(),Toast.LENGTH_SHORT).show();
                } else if (response.body().getStatus() == 500) {
                    Toast.makeText(getActivity(), response.body().getMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    StateRespose mError = new StateRespose();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, StateRespose.class);
                        Toast.makeText(getActivity(), mError.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StateRespose> call, Throwable t) {

                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    ArrayList<SalesEmployeeItem> assignToList_gl = new ArrayList<>();

    //todo calling assign to api here---
    private void callAssignToApi() {
        String  token = "Token " + Globals.GalaxyVistaToken;
        SalesEmployeeItem employeeValue = new SalesEmployeeItem();
        employeeValue.setSalesEmployeeCode(Globals.SalesEmployeeCode);
        Call<SaleEmployeeResponse> call = RetrofitClient.INSTANCE.getApiService().getSalesEmplyeeDataList(token,employeeValue);
        call.enqueue(new Callback<SaleEmployeeResponse>() {
            @Override
            public void onResponse(Call<SaleEmployeeResponse> call, Response<SaleEmployeeResponse> response) {
                if (response.body().getStatus() == 200) {
                    assignToList_gl.clear();
                    if (response.body().getValue().size() > 0) {
                        assignToList_gl.addAll(response.body().getValue());
                        AssignToAdapter stateAdapter = new AssignToAdapter(getActivity(), R.layout.drop_down_textview, assignToList_gl);

                        //todo set state..
                        binding.acAssignTo.setAdapter(stateAdapter);
                        stateAdapter.notifyDataSetChanged();
//                        billtoState = billStateList.get(0).getName();
//                        billtoStateCode = billStateList.get(0).getCode();
                    }

                } else if (response.body().getStatus() == 201) {
                    Toast.makeText(getActivity(), response.body().getMessage(),Toast.LENGTH_SHORT).show();
                } else if (response.body().getStatus() == 500) {
                    Toast.makeText(getActivity(), response.body().getMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    StateRespose mError = new StateRespose();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, StateRespose.class);
                        Toast.makeText(getActivity(), mError.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SaleEmployeeResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void callleadTypeApi(String type) {

        Call<LeadTypeResponse> call = RetrofitClient.INSTANCE.getApiService().getLeadType(token);
        call.enqueue(new Callback<LeadTypeResponse>() {
            @Override
            public void onResponse(Call<LeadTypeResponse> call, Response<LeadTypeResponse> response) {

                if (response.code() == 200) {
                    leadTypeData.clear();
                    leadTypeData.addAll(response.body().getData());
                    binding.leadTypeSpinner.setAdapter(new LeadTypeAdapter(requireContext(), leadTypeData));
                    binding.leadTypeSpinner.setSelection(Globals.getleadType(leadTypeData, type));
                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                }
                //  loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LeadTypeResponse> call, Throwable t) {
                // loader.setVisibility(View.GONE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    ArrayList<LeadTypeData> sourceData = new ArrayList<>();

    private void callSourceApi() {


        Call<LeadTypeResponse> call = RetrofitClient.INSTANCE.getApiService().getsourceType(token);
        call.enqueue(new Callback<LeadTypeResponse>() {
            @Override
            public void onResponse(Call<LeadTypeResponse> call, Response<LeadTypeResponse> response) {

                if (response.body().getStatus() == 200) {
                    sourceData.clear();

                    sourceData.addAll(response.body().getData());
                    binding.sourceSpinner.setAdapter(new LeadTypeAdapter(getActivity(), sourceData));
//                    sourcetype = sourceData.get(0).getName();

                    callApi(id);
                    callLeadOneApi(id);

                }
                binding.loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LeadTypeResponse> call, Throwable t) {
                binding.loader.setVisibility(View.GONE);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void eventManager() {

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
        ArrayAdapter<String> productInterestAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_textview, productInterestList_gl);
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


                    //todo bind product interest adapter with item click--
                    ArrayAdapter<String> productInterestAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_textview, productInterestList_gl);
                    binding.acProductInterest.setAdapter(productInterestAdapter);
                }
            }
        });


        binding.sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sourcetype = sourceData.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sourcetype = sourceData.get(0).getName();
            }
        });

        // Retrieve items from strings.xml

        String[] cateogoryList = getResources().getStringArray(R.array.category);

        selectedCategory = cateogoryList[0];

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cateogoryList);
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

        // todo send payload in JsonObject
        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){

                // Create a JsonObject to hold the payload
                    JsonObject payload = new JsonObject();
                    payload.addProperty("id", id);
                    payload.addProperty("companyName", binding.companyname.getText().toString());
                    payload.addProperty("contactPerson", binding.personName.getText().toString());
                    payload.addProperty("phoneNumber", binding.contactNo.getText().toString());
                    payload.addProperty("email", binding.email.getText().toString());
                    payload.addProperty("source", sourcetype);
                    payload.addProperty("productInterest", binding.etProductInterest.getText().toString());

                    // Set assignedTo based on AssignCode or fallback value
                    if (!AssignCode.isEmpty()) {
                        payload.addProperty("assignedTo", AssignCode);
                    } else {
                        payload.addProperty("assignedTo", Prefs.getString(Globals.SALES_EMPLOYEE_CODE, ""));
                    }

                    // Check and set numOfEmployee field
                    if (binding.etNumOfEmployee.getText().toString().trim().isEmpty()) {
                        payload.addProperty("numOfEmployee", "0");
                    } else {
                        payload.addProperty("numOfEmployee", binding.etNumOfEmployee.getText().toString());
                    }

                    payload.addProperty("turnover", binding.etTurnOver.getText().toString());
                    payload.addProperty("designation", binding.designation.getText().toString());
                    payload.addProperty("employeeId", String.valueOf(leadValues.get(0).getEmployeeId().getId()));
                    payload.addProperty("message", binding.comment.getText().toString());
                    payload.addProperty("date", Globals.getTodaysDatervrsfrmt());
                    payload.addProperty("timestamp", Globals.getTimestamp());
                    payload.addProperty("status", status);
                    payload.addProperty("leadType", leadtype);
                    payload.addProperty("Attach", "");
                    payload.addProperty("Caption", "");
                    payload.addProperty("location", binding.location.getText().toString());


                    List<DynamicFieldResponseData> dynamicFieldsData = dynamicTypeAdapter.getUpdatedFields();

                    for (DynamicFieldResponseData field : dynamicFieldsData) {
                        // Add each dynamic field directly to the JSON object
                        payload.addProperty(field.getField_name(), field.getField_value());
                    }

                    // Check for internet connection and make API call

                        binding.update.setEnabled(false);
                        binding.loader.setVisibility(View.VISIBLE);
                        callUpdateApi(payload);

                }
            }
        });




        binding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = parent.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                status = parent.getSelectedItem().toString();
            }
        });

        binding.leadTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                leadtype = leadTypeData.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                leadtype = leadTypeData.get(0).getName();
            }
        });


    }

    private boolean validation(EditText personName, EditText companyName, EditText contact_no, String sourceTypeInner, String statusinner,EditText email) {

        if (personName.getText().toString().isEmpty()) {
            personName.requestFocus();
            personName.setError("Enter Person Name");
            Toast.makeText(getActivity(), "Enter Person Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (companyName.getText().toString().isEmpty()) {
            companyName.requestFocus();
            companyName.setError("Enter Company Name");
            Toast.makeText(getActivity(), "Enter Company Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (contact_no.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (contact_no.getText().toString().isEmpty() || contact_no.length() != 10) {
            contact_no.requestFocus();
            contact_no.setError("Enter Valid Contact No");
            Toast.makeText(getActivity(), "Enter Contact No", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!email.getText().toString().isEmpty()) {
            if (isvalidateemail()) {
                email.requestFocus();
                email.setError("This email address is not valid");
                return false;
            }
        } else if (sourceTypeInner.isEmpty()) {
            Toast.makeText(getActivity(), "Select Source Type Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (statusinner.isEmpty()) {
            Toast.makeText(getActivity(), "Status is Required", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;

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

    // Your existing method for API call
    private void callUpdateApi(JsonObject lv) {
        Gson gson = new Gson();
        String jsonTut = gson.toJson(lv);
        Log.e("data", jsonTut);
        Call<GlobalResponse> call = RetrofitClient.INSTANCE.getApiService().updateLead(token,lv);
        call.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse> call, Response<GlobalResponse> response) {
                binding.loader.setVisibility(View.GONE);
                binding.update.setEnabled(true);

                if (response.body() != null && response.body().getStatus() == 200) {
                    if ("Update successful".equalsIgnoreCase(response.body().getMessage())) {
                        Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_LONG).show();

                        // Notify the activity about the update success
                        if (listener != null) {
                            listener.onLeadUpdated();
                        }

                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().onBackPressed();
                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {

                    // Enable the update button
                    binding.update.setEnabled(true);
                }


            }

            @Override
            public void onFailure(Call<GlobalResponse> call, Throwable t) {
                binding.loader.setVisibility(View.GONE);
                binding.update.setEnabled(true);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callApi(int id) {
        LeadValue lv = new LeadValue();
        lv.setId(id);
        JsonObject jsonObject =new JsonObject();
        jsonObject.addProperty("id",id);
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

            }
        });
    }

    // TODO: Updated by Tarun for dynamic functionalities
    public void callLeadOneApi(int id) {
        // Prepare dynamic parameters
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        // Initialize Retrofit

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


    // todo code for dynamic fields (Tarun Sharma)
    private void callDynamicFieldsApi() {

        // Make the API call
        Call<DynamicFieldsListModelClass> call = RetrofitClient.INSTANCE.getApiService().getDynamicFieldList(token,"Lead");

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

        callDynamicFieldApi(FinalDynamicFieldsKeyValueMap);

    }





    // Validation function based on mandatory fields
    private boolean validateFields() {
        boolean isValid = true;

        if (isCompanyNameMandatory && binding.companyname.getText().toString().trim().isEmpty()) {
            binding.companyname.requestFocus();
            binding.companyname.setError("Enter " + binding.companyname.getHint());
            Toast.makeText(requireContext(), "Enter " + binding.companyname.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isContactPersonMandatory && binding.personName.getText().toString().trim().isEmpty()) {
            binding.personName.requestFocus();
            binding.personName.setError("Enter " + binding.personName.getHint());
            Toast.makeText(requireContext(), "Enter " + binding.personName.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isPersonDesignationMandatory && binding.designation.getText().toString().trim().isEmpty()) {
            binding.designation.requestFocus();
            binding.designation.setError("Enter " + binding.designation.getHint());
            Toast.makeText(requireContext(), "Enter " + binding.designation.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isPhoneNumberMandatory) {
            String phone = binding.contactNo.getText().toString().trim();
            if (phone.isEmpty()) {
                binding.contactNo.requestFocus();
                binding.contactNo.setError("Phone no. is Required");
                Toast.makeText(requireContext(), "Enter " + binding.contactNo.getHint(), Toast.LENGTH_SHORT).show();
                isValid = false;
            } else if (phone.length() != 10) {
                binding.contactNo.requestFocus();
                binding.contactNo.setError("Enter Valid Contact No");
                Toast.makeText(requireContext(), "Enter Valid " + binding.contactNo.getHint(), Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        if (isEmailMandatory) {
            String email = binding.email.getText().toString().trim();
            if (email.isEmpty()) {
                binding.email.requestFocus();
                binding.email.setError("Enter " + binding.email.getHint());
                Toast.makeText(requireContext(), "Enter " + binding.email.getHint(), Toast.LENGTH_SHORT).show();
                isValid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.email.requestFocus();
                binding.email.setError("Invalid Email Address");
                Toast.makeText(requireContext(), "Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        if (isLocationMandatory && binding.location.getText().toString().trim().isEmpty()) {
            binding.location.requestFocus();
            binding.location.setError("Enter " + binding.location.getHint());
            Toast.makeText(requireContext(), "Enter " + binding.location.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isSourceSpinnerInputLayoutMandatory && sourcetype.trim().isEmpty()) {

            String message = binding.sourceText.getText().toString().replace("*", "").trim();
            Toast.makeText(requireContext(), "Enter " + message,Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isStatusMandatory && status.trim().isEmpty()) {

            String message = binding.statusText.getText().toString().replace("*", "").trim();
            Toast.makeText(requireContext(), "Enter status",Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (iSetNumOfEmployeeMandatory && binding.etNumOfEmployee.getText().toString().trim().isEmpty()) {
            binding.etNumOfEmployee.requestFocus();
            binding.etNumOfEmployee.setError("Enter " + binding.etNumOfEmployee.getHint());
            Toast.makeText(requireContext(), "Enter " + binding.etNumOfEmployee.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isTurnOverMandatory && binding.etTurnOver.getText().toString().trim().isEmpty()) {
            binding.etTurnOver.requestFocus();
            binding.etTurnOver.setError("Enter " + binding.etTurnOver.getHint());
            Toast.makeText(requireContext(), "Enter " + binding.etTurnOver.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isLeadTypeInputLayoutMandatory && leadtype.trim().isEmpty()) {
            String message = binding.leadPriorityText.getText().toString().replace("*", "").trim();
            Toast.makeText(requireContext(), "Enter " + message,Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isAssignedToInputLayoutMandatory && binding.acAssignTo.getText().toString().trim().isEmpty()) {
            binding.acAssignTo.requestFocus();
            binding.acAssignTo.setError("Enter " + binding.assignedToInputLayout.getHint());
            Toast.makeText(requireContext(), "Enter Assigned To", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isRemarkMandatory && binding.comment.getText().toString().trim().isEmpty()) {
            binding.comment.requestFocus();
            binding.comment.setError("Enter " + binding.comment.getHint());
            Toast.makeText(requireContext(), "Enter " + binding.comment.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // ✅ Product Interest Validation
        if (isProductInterestInputLayoutMandatory && binding.productInterestText.getText().toString().trim().isEmpty()) {
            binding.productInterestText.requestFocus();
            binding.productInterestText.setError("Select " + binding.productInterestInputLayout.getHint());
            Toast.makeText(requireContext(), "Select " + binding.productInterestInputLayout.getHint(), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }



    private void setData(LeadValue lv) {

        leadtype = lv.getLeadType();
        status = lv.getStatus();
        binding.statusSpinner.setSelection(getStatuspos(lv.getStatus()));
        binding.companyname.setText(lv.getCompanyName());
        binding.personName.setText(lv.getContactPerson());
        binding.etTurnOver.setText(lv.getTurnover());
       // binding.etNumOfEmployee.setText(lv.getNumOfEmployee());

        binding.contactNo.setText(lv.getPhoneNumber());
        binding.email.setText(lv.getEmail());
        binding.location.setText(lv.getLocation());

      //  binding.acProductInterest.setText(lv.getProductInterest());
        //todo bind product interest adapter with item click--
     //   ArrayAdapter<String> productInterestAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_textview, productInterestList_gl);
     //   binding.acProductInterest.setAdapter(productInterestAdapter);

      //  ProductInterestName = lv.getProductInterest();
        binding.designation.setText(lv.getDesignation());
        binding.comment.setText(lv.getMessage());

        /*if (ProductInterestName == "Other") {
            binding.productDetailLayout.setVisibility(View.VISIBLE);
            binding.tvProductDetail.setText(lv.getProductDetail());
        } else {
            binding.productDetailLayout.setVisibility(View.GONE);
        }*/

       // binding.projectAMount.setText(lv.getProjectAmount());
        binding.etProductInterest.setText(lv.getProductInterest());
        binding.location.setText(lv.getLocation());
        binding.acState.setText(lv.getState());
       // StateName = lv.getState();
      //  binding.selectCategorySpinner.setSelection(getCateogory(lv.getTurnover()));
      //  selectedCategory = lv.getTurnover();
        binding.sourceSpinner.setSelection(getSourcePos(lv.getSource()));
        sourcetype = lv.getSource();

        binding.acAssignTo.setText(lv.getAssignedTo().getFirstName());
        if (!lv.getAssignedTo().getSalesEmployeeCode().isEmpty()) {
            AssignCode = lv.getAssignedTo().getSalesEmployeeCode();
        }
        binding.loader.setVisibility(View.GONE);
        callleadTypeApi(lv.getLeadType());

  //      binding.etNumOfEmployee.setText(lv.getNumOfEmployee());

    }

    private int getStatuspos(String status) {
        int pos = -1;
        for (int i = 0; i < leadstatus.length; i++) {
            String data = leadstatus[i];
            if (data.equalsIgnoreCase(status)) {
                pos = i;
            }
        }
        return pos;
    }

    private int getSourcePos(String status) {
        int pos = -1;
        for (int i = 0; i < sourceData.size(); i++) {
            String data = sourceData.get(i).getName();
            if (data.equalsIgnoreCase(status)) {
                pos = i;
            }
        }
        return pos;
    }

    private int getCateogory(String status) {
        String[] cateogoryList = getResources().getStringArray(R.array.category);

        int pos = -1;
        for (int i = 0; i < cateogoryList.length; i++) {
            String data = cateogoryList[i];
            if (data.equalsIgnoreCase(status)) {
                pos = i;
            }
        }
        return pos;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_press:

                getActivity().onBackPressed();
                break;

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }
}
