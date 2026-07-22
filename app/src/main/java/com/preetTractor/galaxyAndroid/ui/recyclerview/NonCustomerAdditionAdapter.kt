package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.LeadSourceAllResponseModel
import com.preetTractor.galaxyAndroid.data.NonCustomerFormModel
import com.preetTractor.galaxyAndroid.databinding.NonCustomerItemfileBinding
import java.util.*
import kotlin.collections.ArrayList

class NonCustomerAdditionAdapter(
    private val nonCustomerList: ArrayList<NonCustomerFormModel.NonCustomerData>,
    private val modeOfTravelList: List<String>,
    private val sourceList: List<LeadSourceAllResponseModel.Data>, // List for dropdown
    private val context: Context
) : RecyclerView.Adapter<NonCustomerAdditionAdapter.CustomerViewHolder>() {

    init {
        if (nonCustomerList.isEmpty()) {
            nonCustomerList.add(
                NonCustomerFormModel.NonCustomerData(
                    prospectName = "",
                    prospectNumber = "",
                    priority = "",
                    source = "",
                    selectedSourceId = "",
                    industry = "",
                    timing = "",
                    remark = "",
                    zone = "",
                    createLeadCheck = false
                )
            ) // Initial card
        }
    }

    inner class CustomerViewHolder(val binding: NonCustomerItemfileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            NonCustomerItemfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return nonCustomerList.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val item = nonCustomerList[position]

        // Set previously selected time if available
        holder.binding.edtTiming.setText(item.timing)

        // Open TimePickerDialog on click
        holder.binding.edtTiming.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    // Format the time in hh:mm AM/PM
                    val formattedTime = String.format(
                        Locale.getDefault(), "%02d:%02d %s",
                        if (selectedHour % 12 == 0) 12 else selectedHour % 12,
                        selectedMinute,
                        if (selectedHour >= 12) "PM" else "AM"
                    )

                    // Set time in EditText and update item model
                    holder.binding.edtTiming.setText(formattedTime)
                    item.timing = formattedTime
                },
                hour, minute, false
            )

            timePickerDialog.show()
        }

        Log.d("TimePicker", "onBindViewHolder: ${item.timing}")

        setUpSourceList(item, holder)
        setUpZoneList(item, holder)
//        setUpIndustryList(item, holder)
        setUpPriorityList(item, holder)
        setUpModeOfTravelTextView(item,holder.binding.acModeOfTravel,modeOfTravelList)
        // Store user input into the model class
        saveDataInModelClass(item, holder)

        // 🔹 Set Click Listener for the Remove Icon
        holder.binding.ivRemoveCard.setOnClickListener {
            removeCard(holder.absoluteAdapterPosition, holder)
        }
    }


    private fun setUpModeOfTravelTextView(
        item: NonCustomerFormModel.NonCustomerData,
        autoCompleteTextView: AutoCompleteTextView,
        modelist: List<String>
    ) {
        val adapter = ArrayAdapter(
            autoCompleteTextView.context,
            android.R.layout.simple_dropdown_item_1line,
            modelist)

        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnClickListener {
            autoCompleteTextView.showDropDown()
        }
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            item.transport_mode = adapter.getItem(position).toString()
        }

    }

    // ✅ Function to Remove a Card
    private fun removeCard(position: Int, holder: NonCustomerAdditionAdapter.CustomerViewHolder) {

            if (position < nonCustomerList.size) {
                nonCustomerList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)

                holder.binding.acProspectName.text.clear()
                holder.binding.atProspectNumber.text.clear()
                holder.binding.searchableAutoCompleteSource.text.clear()
                holder.binding.searchableAutoCompleteIndustry.text.clear()
                holder.binding.searchableAutoCompleteZone.text.clear()
                holder.binding.edtTiming.text.clear()
                holder.binding.acPriorityList.text.clear()
                holder.binding.editTextRemark.text.clear()

                holder.binding.createLeadCheckBox.isChecked = false

            }
    }




    private fun saveDataInModelClass(
        item: NonCustomerFormModel.NonCustomerData,
        holder: CustomerViewHolder
    ) {
        holder.binding.acProspectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item.prospectName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        holder.binding.searchableAutoCompleteIndustry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item.industry = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        holder.binding.atProspectNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item.prospectNumber = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        holder.binding.editTextRemark.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item.remark = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        holder.binding.createLeadCheckBox.setOnCheckedChangeListener { _, isChecked ->
            item.createLeadCheck = isChecked
        }
    }

    private fun setUpPriorityList(
        item: NonCustomerFormModel.NonCustomerData,
        holder: CustomerViewHolder
    ) {
        holder.binding.acPriorityList.hint = "Select"

        val priorityArray = context.resources.getStringArray(R.array.priority_array).toList()
        val adapter = ArrayAdapter(context, R.layout.drop_down_textview, priorityArray)

        holder.binding.acPriorityList.setAdapter(adapter)
        holder.binding.acPriorityList.setOnClickListener { holder.binding.acPriorityList.showDropDown() }
        holder.binding.acPriorityList.setOnItemClickListener { _, _, position, _ ->
            item.priority = adapter.getItem(position).toString()
        }
    }

 /*   private fun setUpIndustryList(
        item: NonCustomerFormModel.NonCustomerData,
        holder: CustomerViewHolder
    ) {
        holder.binding.searchableAutoCompleteIndustry.hint = "Select"

        val industryList = context.resources.getStringArray(R.array.industry_item_list).toList()
        val adapter = ArrayAdapter(context, R.layout.drop_down_textview, industryList)

        holder.binding.searchableAutoCompleteIndustry.setAdapter(adapter)
        holder.binding.searchableAutoCompleteIndustry.setOnClickListener { holder.binding.searchableAutoCompleteIndustry.showDropDown() }
        holder.binding.searchableAutoCompleteIndustry.setOnItemClickListener { _, _, position, _ ->
            item.industry = adapter.getItem(position).toString()
        }
    }*/

    private fun setUpZoneList(
        item: NonCustomerFormModel.NonCustomerData,
        holder: CustomerViewHolder
    ) {
        holder.binding.searchableAutoCompleteZone.hint = "Select Zone"

        val zoneList = context.resources.getStringArray(R.array.zone_list).toList()
        val adapter = ArrayAdapter(context, R.layout.drop_down_textview, zoneList)

        holder.binding.searchableAutoCompleteZone.setAdapter(adapter)
        holder.binding.searchableAutoCompleteZone.setOnClickListener { holder.binding.searchableAutoCompleteZone.showDropDown() }
        holder.binding.searchableAutoCompleteZone.setOnItemClickListener { _, _, position, _ ->
            item.zone = adapter.getItem(position).toString()
        }
    }

    private fun setUpSourceList(
        item: NonCustomerFormModel.NonCustomerData,
        holder: CustomerViewHolder
    ) {
        holder.binding.searchableAutoCompleteSource.setHint("Source")

        val finalList = sourceList.map { it.Name }

        val adapter = ArrayAdapter(context, R.layout.drop_down_textview, finalList)

        holder.binding.searchableAutoCompleteSource.setAdapter(adapter)
        holder.binding.searchableAutoCompleteSource.setOnClickListener { holder.binding.searchableAutoCompleteSource.showDropDown() }
        holder.binding.searchableAutoCompleteSource.setOnItemClickListener { _, _, position, _ ->
            item.source = adapter.getItem(position).toString()
            item.selectedSourceId = sourceList[position].id.toString()
        }
    }

    // ✅ Function to add a new card dynamically
    fun addNewCard() {
        nonCustomerList.add(NonCustomerFormModel.NonCustomerData(
            prospectName = "",
            prospectNumber = "",
            priority = "",
            source = "",
            selectedSourceId = "",
            industry = "",
            timing = "",
            remark = "",
            zone = "",
            createLeadCheck = false

        )) // Add a new empty data object
        notifyItemInserted(nonCustomerList.size - 1)
    }

    // ✅ Validate all name fields before saving
    fun isAllNamesFilled(recyclerView: RecyclerView, context: Context): Boolean {
        var allFilled = true

        for (i in 0 until itemCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? CustomerViewHolder
            viewHolder?.let { holder ->
                val name = holder.binding.acProspectName.text.toString().trim()
                val timing = holder.binding.edtTiming.text.toString().trim()
                val priority = holder.binding.acPriorityList.text.toString().trim()
                val remarks = holder.binding.editTextRemark.text.toString().trim()
                if (name.isEmpty()) {
                    holder.binding.acProspectName.error = "Name is required"
                    Toast.makeText(context, "Enter Prospect Name", Toast.LENGTH_SHORT).show()
                    allFilled = false
                } else {
                    holder.binding.acProspectName.error = null
                }
                if (timing.isEmpty()) {
                    Toast.makeText(context, "Enter Timing", Toast.LENGTH_SHORT).show()
                    allFilled = false
                }
                if (priority.isEmpty()) {
                    holder.binding.acPriorityList.error = "Priority is required"
                    Toast.makeText(context, "Select Priority", Toast.LENGTH_SHORT).show()
                    allFilled = false
                } else {
                    holder.binding.acPriorityList.error = null
                }
                if (remarks.isEmpty()) {
                    Toast.makeText(context, "Enter Remarks", Toast.LENGTH_SHORT).show()
                    allFilled = false
                }
            }
        }

        /*if (!allFilled) {
            Toast.makeText(context, "Please fill all mandatory fields", Toast.LENGTH_SHORT).show()
        }*/

        return allFilled
    }


    // ✅ Function to get all entered data
    fun getNonCustomerList(): ArrayList<NonCustomerFormModel.NonCustomerData> {
        return nonCustomerList
    }
}
