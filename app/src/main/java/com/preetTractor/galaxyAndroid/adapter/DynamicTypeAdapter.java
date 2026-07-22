package com.preetTractor.galaxyAndroid.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.preetTractor.galaxyAndroid.R;
import com.preetTractor.galaxyAndroid.data.DynamicField.DynamicFieldResponseData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DynamicTypeAdapter extends RecyclerView.Adapter<DynamicTypeAdapter.ViewHolder> {

    private final Context context;

    String temp;
    private final List<DynamicFieldResponseData> itemsList;
    private final List<Map.Entry<String, String>> FinalDynamicFieldsKeyValueMap;

    private final HashMap<String, String> textValues = new HashMap<>();
    private final HashMap<String, String> numberValues = new HashMap<>();
    private final HashMap<String, String> dateValues = new HashMap<>();
    private final HashMap<String, String> dropDownValues = new HashMap<>();

    public DynamicTypeAdapter(String temp, List<Map.Entry<String, String>> FinalDynamicFieldsKeyValueMap, List<DynamicFieldResponseData> itemsList, Context context) {
        this.context = context;
        this.itemsList = itemsList;
        this.FinalDynamicFieldsKeyValueMap = FinalDynamicFieldsKeyValueMap;
        this.temp = temp;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDynamicTextField, tvDynamicNumberField, tvDynamicDateField, tvDynamicDropDownField, edDynamicDateValue;
        LinearLayout dynamicTextLayout, dynamicNumberLayout, dynamicDateLayout, dynamicListLayout;
        EditText edDynamicTextValue, edDynamicNumberValue;
        AutoCompleteTextView acDynamicDropDownValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDynamicTextField = itemView.findViewById(R.id.tvDynamicTextField);
            tvDynamicNumberField = itemView.findViewById(R.id.tvDynamicNumberField);
            tvDynamicDateField = itemView.findViewById(R.id.tvDynamicDateField);
            tvDynamicDropDownField = itemView.findViewById(R.id.tvDynamicDropDownField);
            edDynamicDateValue = itemView.findViewById(R.id.edDynamicDateValue);

            dynamicTextLayout = itemView.findViewById(R.id.dynamicTextLayout);
            dynamicNumberLayout = itemView.findViewById(R.id.dynamicNumberLayout);
            dynamicDateLayout = itemView.findViewById(R.id.dynamicDateLayout);
            dynamicListLayout = itemView.findViewById(R.id.dynamicListLayout);

            edDynamicTextValue = itemView.findViewById(R.id.edDynamicTextValue);
            edDynamicNumberValue = itemView.findViewById(R.id.edDynamicNumberValue);
            acDynamicDropDownValue = itemView.findViewById(R.id.acDynamicDropDownValue);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.dynamic_fields_layout, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DynamicFieldResponseData obj = itemsList.get(position);
        String type = obj.getData_type();
        String fieldName = obj.getField_name();
        String fieldName1 = obj.getField_value();
        String value = obj.getField_value();
        Boolean isMandatory = obj.getIs_mandatory();

        // Hide all layouts by default
        holder.dynamicTextLayout.setVisibility(View.GONE);
        holder.dynamicNumberLayout.setVisibility(View.GONE);
        holder.dynamicDateLayout.setVisibility(View.GONE);
        holder.dynamicListLayout.setVisibility(View.GONE);


        if(temp.equals("Update")){

            // Check if field_value matches any key in FinalDynamicFieldsKeyValueMap and set the corresponding value
            if (FinalDynamicFieldsKeyValueMap != null) {
                for (Map.Entry<String, String> entry : FinalDynamicFieldsKeyValueMap) {
                    if (fieldName1.equals(entry.getKey())) {
                        value = entry.getValue(); // Update the field value from the map
                        Log.d("UpdatedValue", "Updated value for " + fieldName1 + ": " + value);
                        break; // Once we find the match, we can break the loop
                    }
                }
            }

            // Check field type and set corresponding field's value in the appropriate layout
            switch (type) {
                case "text":
                    holder.dynamicTextLayout.setVisibility(View.VISIBLE);

                    setMandatoryField(holder.tvDynamicTextField, fieldName1, isMandatory != null ? isMandatory : false);

                    // Set the value in the EditText

                    String storedValue = textValues.get(fieldName); // Get the stored value

                    if (storedValue != null && storedValue.equals(value)) {
//                        holder.edDynamicTextValue.setHint(value);
                        holder.edDynamicTextValue.setText(storedValue);
                    } else {
                        holder.edDynamicTextValue.setHint(value);
                        holder.edDynamicTextValue.setText(value);
                    }


                    holder.edDynamicTextValue.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            textValues.put(fieldName, s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
                    break;

                case "number":
                    holder.dynamicNumberLayout.setVisibility(View.VISIBLE);

                    setMandatoryField(holder.tvDynamicNumberField, fieldName1, isMandatory != null ? isMandatory : false);

                    // Set the value in the EditText
//                    holder.edDynamicNumberValue.setHint(value);

//                    holder.edDynamicNumberValue.setText(numberValues.get(fieldName));
                    holder.edDynamicNumberValue.setText(value);
                    holder.edDynamicNumberValue.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            numberValues.put(fieldName, s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
                    break;

                case "date":
                    holder.dynamicDateLayout.setVisibility(View.VISIBLE);
                    setMandatoryField(holder.tvDynamicDateField, fieldName1, isMandatory != null ? isMandatory : false);


//                    holder.edDynamicDateValue.setHint(value);
                    holder.edDynamicDateValue.setText(value);
                    holder.dynamicDateLayout.setOnClickListener(v -> {
                        Calendar calendar = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                (view, year, month, day) -> {
                                    String selectedDate = day + "-" + (month + 1) + "-" + year;
                                    holder.edDynamicDateValue.setText(selectedDate);
                                    dateValues.put(fieldName, selectedDate);
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    });
                    break;

                case "Dropdown":
                    holder.dynamicListLayout.setVisibility(View.VISIBLE);

                    setMandatoryField(holder.tvDynamicDropDownField, fieldName1, isMandatory != null ? isMandatory : false);


                    // Set the dropdown values
                    List<String> dataOptionList = parseDataOption(obj.getData_option());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.drop_down_textview, dataOptionList);
                    holder.acDynamicDropDownValue.setAdapter(adapter);
//                    holder.acDynamicDropDownValue.setHint(value);
                    holder.acDynamicDropDownValue.setText(value);

                    holder.acDynamicDropDownValue.setOnItemClickListener((parent, view, pos, id) ->
                            dropDownValues.put(fieldName, parent.getItemAtPosition(pos).toString()));
                    break;
            }
        }
        else{

            // Check field type and set corresponding field's value in the appropriate layout
            switch (type) {
                case "text":
                    holder.dynamicTextLayout.setVisibility(View.VISIBLE);

                    boolean mandatory = (isMandatory != null) ? isMandatory : false;
                    setMandatoryField(holder.tvDynamicTextField, value, mandatory);


                    holder.edDynamicTextValue.setHint(value);
                    holder.edDynamicTextValue.setText(textValues.get(fieldName));

                    holder.edDynamicTextValue.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            textValues.put(fieldName, s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
                    break;

                case "number":
                    holder.dynamicNumberLayout.setVisibility(View.VISIBLE);

                    boolean mandatory1 = (isMandatory != null) ? isMandatory : false;
                    setMandatoryField(holder.tvDynamicNumberField, value, mandatory1);


                    // Set the value in the EditText
                    holder.edDynamicNumberValue.setHint(value);

                    holder.edDynamicNumberValue.setText(numberValues.get(fieldName));
                    holder.edDynamicNumberValue.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            numberValues.put(fieldName, s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
                    break;

                case "date":
                    holder.dynamicDateLayout.setVisibility(View.VISIBLE);

                    boolean mandatory2 = (isMandatory != null) ? isMandatory : false;
                    setMandatoryField(holder.tvDynamicDateField, value, mandatory2);


                    holder.edDynamicDateValue.setHint("Select " + value);
                    holder.dynamicDateLayout.setOnClickListener(v -> {
                        Calendar calendar = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                (view, year, month, day) -> {
                                    String selectedDate = day + "-" + (month + 1) + "-" + year;
                                    holder.edDynamicDateValue.setText(selectedDate);
                                    dateValues.put(fieldName, selectedDate);
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    });
                    break;

                case "Dropdown":
                    holder.dynamicListLayout.setVisibility(View.VISIBLE);

                    boolean mandatory3 = (isMandatory != null) ? isMandatory : false;
                    setMandatoryField(holder.tvDynamicDropDownField, value, mandatory3);


                    // Set the dropdown values
                    List<String> dataOptionList = parseDataOption(obj.getData_option());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.drop_down_textview, dataOptionList);
                    holder.acDynamicDropDownValue.setAdapter(adapter);
                    holder.acDynamicDropDownValue.setHint(value);

                    holder.acDynamicDropDownValue.setOnItemClickListener((parent, view, pos, id) ->
                            dropDownValues.put(fieldName, parent.getItemAtPosition(pos).toString()));
                    break;
            }
        }

    }



    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public String convertToNameFormat(String input) {
        // Split the input by underscores
        String[] words = input.split("_");

        StringBuilder articleName = new StringBuilder();

        for (String word : words) {
            // Capitalize the first letter and make the rest lowercase
            articleName.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }

        // Trim the trailing space
        return articleName.toString().trim();
    }

    public String validateFields() {
        for (DynamicFieldResponseData field : itemsList) {
            if (Boolean.TRUE.equals(field.getIs_mandatory())) {
                String value = "";

                switch (field.getData_type()) {
                    case "text":
                        value = textValues.get(field.getField_name());
                        break;
                    case "number":
                        value = numberValues.get(field.getField_name());
                        break;
                    case "date":
                        value = dateValues.get(field.getField_name());
                        break;
                    case "Dropdown":
                        value = dropDownValues.get(field.getField_name());
                        break;
                }

                if (value == null || value.trim().isEmpty()) {
                    return "Enter " + formatFieldName(field.getField_name());
                }
            }
        }
        return ""; // No validation error
    }

    public String formatFieldName(String fieldName) {
        // Replace underscores with spaces and capitalize each word
        String[] words = fieldName.split("_");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            if (formattedName.length() > 0) {
                formattedName.append(" ");
            }
            formattedName.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }

        return formattedName.toString();
    }


    private void setMandatoryField(TextView textView, String value, boolean isMandatory) {
        if (isMandatory) {
            SpannableString spannable = new SpannableString(value + " *");
            spannable.setSpan(new ForegroundColorSpan(Color.RED), value.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannable);
        } else {
            textView.setText(value);
        }
    }

    public List<String> parseDataOption(String dataOptionString) {
        return new Gson().fromJson(dataOptionString, new TypeToken<List<String>>() {}.getType());
    }

    public List<DynamicFieldResponseData> getUpdatedFields() {
        List<DynamicFieldResponseData> dynamicFields = new ArrayList<>();
        for (DynamicFieldResponseData field : itemsList) {
            String value = null;

            switch (field.getData_type()) {
                case "text":
                    value = textValues.get(field.getField_name());
                    break;
                case "number":
                    value = numberValues.get(field.getField_name());
                    break;
                case "date":
                    value = dateValues.get(field.getField_name());
                    break;
                case "Dropdown":
                    value = dropDownValues.get(field.getField_name());
                    break;
            }

            dynamicFields.add(new DynamicFieldResponseData(field.getField_name(), value, field.getData_type(), field.getData_option()));
        }
        return dynamicFields;
    }
}





