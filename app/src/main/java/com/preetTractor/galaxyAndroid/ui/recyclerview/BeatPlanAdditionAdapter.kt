package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.BdrcData
import com.preetTractor.galaxyAndroid.data.BeatPlanCustomerDropDownModel
import com.preetTractor.galaxyAndroid.databinding.ItemBeatPlanBinding
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoTimePicker

class BeatPlanAdditionAdapter(
    private var customerList: MutableList<BeatPlanCustomerDropDownModel.Data>,
    private val modeOfTravelList: List<String>,
    private val allCustomers: ArrayList<BeatPlanCustomerDropDownModel.Data>,
    private val onanyItemClicked: (position: Int) -> Unit,
    private val onCustomerClick: (cardCode: String, position: Int) -> Unit
) : RecyclerView.Adapter<BeatPlanAdditionAdapter.CustomerViewHolder>() {


    inner class CustomerViewHolder(val binding: ItemBeatPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {

            }
        }


        fun bind(item: BeatPlanCustomerDropDownModel.Data) {
            // Set up AutoCompleteTextView with a custom adapter


            if(item.bdrcList.isNotEmpty()){
                binding.billingCounter.text = item.bdrcList[0].BillingTarget.toString()
                binding.deliveryCounter.text = item.bdrcList[0].DeliveryTarget.toString()
                binding.retailCounter.text = item.bdrcList[0].RetailTarget.toString()
                binding.collectionCounter.text = item.bdrcList[0].CollectionTarget.toString()
            }

            setupAutoCompleteTextView(binding.autoCompleteCustomer, allCustomers)
            setUpModeOfTravelTextView(binding.acModeOfTravel,modeOfTravelList)
            // Set initial values
            binding.autoCompleteCustomer.setText(item.CardName, false)

            // Populate Spinners
            // setupSpinner(binding.spinnerTiming, R.array.timing_array)
            setupSpinner(binding.spinnerPriority, R.array.priority_array)

            binding.spinnerTiming.setSelection(getIndex(binding.spinnerTiming, item.timing))
            binding.spinnerPriority.setSelection(getIndex(binding.spinnerPriority, item.priority))
            binding.editTextRemark.setText(item.remark)

            // Listeners to update the data class
            binding.autoCompleteCustomer.addTextChangedListener {
                customerList[absoluteAdapterPosition].CardName = it.toString()
            }

            binding.edtTiming.transformIntoTimePicker(itemView.context, "hh:mm a")

            binding.edtTiming.addTextChangedListener {
                customerList[absoluteAdapterPosition].CheckinTime = it.toString()
            }

            binding.spinnerTiming.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        parent?.getItemAtPosition(position).toString()
                        // customerList[adapterPosition].timing = selectedTiming
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            binding.spinnerPriority.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedPriority = parent?.getItemAtPosition(position).toString()
                        customerList[absoluteAdapterPosition].priority = selectedPriority
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            binding.editTextRemark.addTextChangedListener {
                customerList[absoluteAdapterPosition].remark = it.toString()
            }

            binding.ibCross.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onanyItemClicked(position)
                }
            }
        }

        private fun setUpModeOfTravelTextView(
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
                customerList[absoluteAdapterPosition].transport_mode = adapter.getItem(position).toString()
            }

        }

        private fun setupSpinner(spinner: Spinner, arrayResId: Int) {
            ArrayAdapter.createFromResource(
                spinner.context, arrayResId, R.layout.drop_down_textview
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.drop_down_textview)
                spinner.adapter = adapter
            }
        }

        private fun setupAutoCompleteTextView(
            autoCompleteTextView: AutoCompleteTextView,
            customers: ArrayList<BeatPlanCustomerDropDownModel.Data>
        ) {
            val adapter = ArrayAdapter(
                autoCompleteTextView.context,
                android.R.layout.simple_dropdown_item_1line,
                customers.map { it.CardName })

            autoCompleteTextView.setAdapter(adapter)
            autoCompleteTextView.setOnClickListener {
                autoCompleteTextView.showDropDown()
            }
            autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->

                val selectedName = parent.getItemAtPosition(position) as String

                val selectedCustomer = customers.find {
                    it.CardName == selectedName
                }

                selectedCustomer?.let {

                    customerList[absoluteAdapterPosition].apply {
                        CardName = it.CardName
                        CardCode = it.CardCode
                        CheckinTime = it.CheckinTime
                        CheckoutTime = it.CheckoutTime
                        id = it.id
                    }

                    binding.edtTiming.setText(it.CheckinTime)
                }

                onCustomerClick(customerList[absoluteAdapterPosition].CardCode,absoluteAdapterPosition)
            }
        }

        private fun getIndex(spinner: Spinner, value: String): Int {
            for (i in 0 until spinner.count) {
                if (spinner.getItemAtPosition(i).toString() == value) {
                    return i
                }
            }
            return 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            ItemBeatPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(customerList[position])


    }

    override fun getItemCount(): Int = customerList.size

    fun addItem(item: BeatPlanCustomerDropDownModel.Data) {
        customerList.add(item)
        notifyItemInserted(customerList.size - 1)
    }

    fun updateItem(position: Int, item: BeatPlanCustomerDropDownModel.Data) {
        customerList[position] = item
        notifyItemChanged(position)
    }

    fun updateBDRCData(position: Int, data: List<BdrcData>){
        customerList[position].bdrcList.clear()
        customerList[position].bdrcList.addAll(data)
        notifyItemChanged(position)
    }

    /*  fun removeItem(position: Int) {
          customerList.removeAt(position)
          notifyItemRemoved(pospition)
      }*/

    // Function to remove an item
    fun removeItem(position: Int) {
        if (position >= 0 && position < customerList.size) {
            customerList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, customerList.size) // Update the remaining items
        }
    }


    fun getAttachList(): List<BeatPlanCustomerDropDownModel.Data> {
        return customerList.toList()
    }

}

