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
import com.preetTractor.galaxyAndroid.data.AllLeadResponse
import com.preetTractor.galaxyAndroid.data.LeadModel
import com.preetTractor.galaxyAndroid.databinding.ItemAddLeadCardBinding
import java.util.*
import kotlin.collections.ArrayList

class LeadAdditionAdapter(
    private val leadList: ArrayList<LeadModel.LeadDataList>,
    private val modeOfTravelList: List<String>,
    private val leadItemList: List<AllLeadResponse.Data>, // List for dropdown
    private val context: Context
) : RecyclerView.Adapter<LeadAdditionAdapter.CustomerViewHolder>() {

    init {
        if (leadList.isEmpty()) {
            leadList.add(
                LeadModel.LeadDataList(
                    selectedLeadId = "",
                    leadName = "",
                    priority = "",
                    transport_mode = "",
                    timing = "",
                    remark = "",
                )
            ) // Initial card
        }
    }

    inner class CustomerViewHolder(val binding: ItemAddLeadCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            ItemAddLeadCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return leadList.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val item = leadList[position]

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

        setUpLeadList(item, holder)
        setUpPriorityList(item, holder)
        setUpModeOfTravelTextView(item,holder.binding.acModeOfTravel, modelist = modeOfTravelList)
        // Store user input into the model class
        saveDataInModelClass(item, holder)

        // 🔹 Set Click Listener for the Remove Icon
        holder.binding.ibCross.setOnClickListener {
            removeCard(holder.absoluteAdapterPosition, holder)
        }
    }

    // ✅ Function to Remove a Card
    private fun removeCard(position: Int, holder: LeadAdditionAdapter.CustomerViewHolder) {

            if (position < leadList.size) {
                leadList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)

                holder.binding.acLeadName.text.clear()
                holder.binding.edtTiming.text.clear()
                holder.binding.acPriorityList.text.clear()
                holder.binding.editTextRemark.text.clear()



            }
    }

    private fun setUpModeOfTravelTextView(
        item: LeadModel.LeadDataList,
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


    private fun saveDataInModelClass(
        item: LeadModel.LeadDataList,
        holder: CustomerViewHolder
    ) {

        holder.binding.editTextRemark.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item.remark = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun setUpPriorityList(
        item: LeadModel.LeadDataList,
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

    private fun setUpLeadList(
        item: LeadModel.LeadDataList,
        holder: CustomerViewHolder
    ) {
        val finalList = leadItemList.map { it.companyName }
        val adapter = ArrayAdapter(context, R.layout.drop_down_textview, finalList)

        holder.binding.acLeadName.setAdapter(adapter)
        holder.binding.acLeadName.setOnClickListener { holder.binding.acLeadName.showDropDown() }
        holder.binding.acLeadName.setOnItemClickListener { _, _, position, _ ->
            item.leadName = adapter.getItem(position).toString()
            item.selectedLeadId = leadItemList[position].id.toString()


        }
    }





//     ✅ Function to add a new card dynamically
    fun addNewCard() {
        leadList.add(LeadModel.LeadDataList(
            selectedLeadId = "",
            leadName = "",
            priority = "",
            timing = "",
            remark = "",

        )) // Add a new empty data object
        notifyItemInserted(leadList.size - 1)
    }

    // ✅ Validate all required fields before saving
    fun isAllNamesFilled(recyclerView: RecyclerView, context: Context): Boolean {
        var allFilled = true

        for (i in 0 until itemCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? CustomerViewHolder
            viewHolder?.let { holder ->
                val name = holder.binding.acLeadName.text.toString().trim()
                val time = holder.binding.edtTiming.text.toString().trim()
                val priority = holder.binding.acPriorityList.text.toString().trim()
                val remark = holder.binding.editTextRemark.text.toString().trim()

                // Validate each field separately
                if (name.isEmpty()) {
//                    holder.binding.acLeadName.error = "Lead is required"
                    Toast.makeText(context, "Please Select Lead ", Toast.LENGTH_SHORT).show()
                    allFilled = false
                }

                else if (time.isEmpty()) {
//                    holder.binding.edtTiming.error = "Time is required"
                    Toast.makeText(context, "Please Select Time", Toast.LENGTH_SHORT).show()
                    allFilled = false
                }

                else if (priority.isEmpty()) {
//                    holder.binding.acPriorityList.error = "Priority is required"
                    Toast.makeText(context, "Please Select Priority", Toast.LENGTH_SHORT).show()
                    allFilled = false
                }

                else if (remark.isEmpty()) {
//                    holder.binding.editTextRemark.error = "Remark is required"
                    Toast.makeText(context, "Please Enter Remark", Toast.LENGTH_SHORT).show()
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
    fun getleadList(): ArrayList<LeadModel.LeadDataList> {
        return leadList
    }

}
