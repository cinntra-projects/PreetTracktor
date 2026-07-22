package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.expense.addExpense.ConveyanceModel
import com.preetTractor.galaxyAndroid.databinding.ItemLodgingBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker


class AddLodgingAdapter(
    private val itemList: ArrayList<ConveyanceModel>,
    private val context: Context,
    private val showEditButton: Boolean = true
) :
    RecyclerView.Adapter<AddLodgingAdapter.ItemViewHolder>() {

    private var onEditBtnClickListener: ((String, Int) -> Unit)? = null

    private var onFromDateBtnClickListener: ((String, Int) -> Unit)? = null
    private var onLocationBtnClickListener: ((String, Int) -> Unit)? = null
    private var onToDateClickListener: ((String, Int) -> Unit)? = null
    private var onNumberOfPeopleClickListener: ((String, Int) -> Unit)? = null
    private var onHotelNameClickListener: ((String, Int) -> Unit)? = null
    private var onCameraBtnClickListener: ((String, Int) -> Unit)? = null
    private var onDeleteBtnClickListener: ((String, Int) -> Unit)? = null
    private var onAmountBtnClickListener: ((Int, Int) -> Unit)? = null

    fun setonEditBtnClickListener(listener: (String, Int) -> Unit) {
        onEditBtnClickListener = listener
    }

    fun setOnToDateClickListener(listener: (String, Int) -> Unit) {
        onToDateClickListener = listener
    }

    fun setOnAmountClickListener(listener: (Int, Int) -> Unit) {
        onAmountBtnClickListener = listener
    }

    fun setOnFromDateBtnClickListener(listener: (String, Int) -> Unit) {
        onFromDateBtnClickListener = listener
    }

    fun setOnLocationBtnClickListener(listener: (String, Int) -> Unit) {
        onLocationBtnClickListener = listener
    }

    fun setOnToLocationBtnClickListener(listener: (String, Int) -> Unit) {
        onLocationBtnClickListener = listener
    }

    fun setOnHotelNameClickListener(listener: (String, Int) -> Unit) {
        onHotelNameClickListener = listener
    }


    fun setOnNumberOfPeopleClickListener(listener: (String, Int) -> Unit) {
        onNumberOfPeopleClickListener = listener
    }

    fun setOnCameraBtnClickListener(listener: (String, Int) -> Unit) {
        onCameraBtnClickListener = listener
    }

    fun setOnDeleteBtnClickListener(listener: (String, Int) -> Unit) {
        onDeleteBtnClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemLodgingBinding =
            ItemLodgingBinding.inflate(inflater, parent, false)

        return ItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item: ConveyanceModel = itemList[position]
        holder.onBind(
            position,
            onCameraBtnClickListener,
            onAmountBtnClickListener,
            onFromDateBtnClickListener,
            onToDateClickListener,
            onHotelNameClickListener,
            onNumberOfPeopleClickListener,
            onEditBtnClickListener,
            onLocationBtnClickListener,
            onDeleteBtnClickListener,
            item
        )
        /*holder.binding.ivDelete.visibility = if (position == 1) View.GONE else View.VISIBLE

        holder.binding.tvCamera.setOnClickListener {
            onCameraBtnClickListener?.invoke("camera", position)
        }
        holder.binding.ivDelete.setOnClickListener {
            onDeleteBtnClickListener?.invoke("delete", position)
        }
        holder.binding.tvDate.setOnClickListener {
            onDateBtnClickListener?.invoke("date", position)
        }
        holder.binding.etLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onLocationBtnClickListener?.invoke(str.toString(), position)
            }
        })
        holder.binding.etRemark.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onRemarkBtnClickListener?.invoke(str.toString(), position)
            }
        })
        holder.binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onAmountBtnClickListener?.invoke(str.toString(), position)
            }
        })
        holder.binding.etToLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onToLocationBtnClickListener?.invoke(str.toString(), position)
            }
        })
        holder.binding.spinnerMode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Get the selected item text from the Spinner
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    onModeBtnClickListener?.invoke(selectedItem, position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle case where no item is selected (optional)
                }
            }*/

    }


    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ItemViewHolder(
        val binding: ItemLodgingBinding,

        ) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var etLodgeLocation: EditText
        lateinit var etNoOfPeople: EditText
        lateinit var etAmount: EditText
        lateinit var etHotelName: EditText
        lateinit var tvFromDate: TextView
        lateinit var etToDate: TextView
        lateinit var ivDelete: ImageView
        lateinit var tvCamera: ImageView
        lateinit var spinnerMode: Spinner
        fun onBind(
            positionHeader: Int, onCameraBtnClickListener: ((String, Int) -> Unit)?,
            onAmountBtnClickListener: ((Int, Int) -> Unit)?,
            onFromDateClickListener: ((String, Int) -> Unit)?,
            onToDateBtnClickListener: ((String, Int) -> Unit)?,
            onHotelNameClickListener: ((String, Int) -> Unit)?,
            onNumberOfPeopleClickListener: ((String, Int) -> Unit)?,

            onEditBtnClickListener: ((String, Int) -> Unit)?,
            onLocationBtnClickListener: ((String, Int) -> Unit)?,

            onDeleteBtnClickListener: ((String, Int) -> Unit)?,
            item: ConveyanceModel
        ) {
            etLodgeLocation = binding.etLodgeLocation
            etNoOfPeople = binding.etNoOfPeople
            etAmount = binding.etLodgeAmount
            etHotelName = binding.etHotelName
            tvFromDate = binding.tvFromDate
            etToDate = binding.tvToDate
            ivDelete = binding.ivDelete
            tvCamera = binding.tvCamera

            tvFromDate.setText(Globals.getTodaysDate())
            etToDate.setText(Globals.getTodaysDate())
            tvFromDate.transformIntoDatePicker(tvFromDate.context, "dd/MM/yyyy", null)
            etToDate.transformIntoDatePicker(etToDate.context, "dd/MM/yyyy", null)
            ivDelete.visibility = if (position == 0) View.GONE else View.VISIBLE



            tvCamera.setOnClickListener {
                onCameraBtnClickListener?.invoke("camera", position)
            }
            ivDelete.setOnClickListener {
                onDeleteBtnClickListener?.invoke("delete", position)
            }


            etAmount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (str!!.isNotEmpty()) {
                        onAmountBtnClickListener?.invoke(str.toString().toInt(), position)
                    } else {
                        onAmountBtnClickListener?.invoke("0".toInt(), position)
                    }


                }
            })


            etLodgeLocation.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    onLocationBtnClickListener?.invoke(str.toString(), position)


                }
            })



            tvFromDate.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    onFromDateClickListener?.invoke(str.toString(), position)


                }
            })


            etToDate.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    onToDateBtnClickListener?.invoke(str.toString(), position)


                }
            })


            etHotelName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    onHotelNameClickListener?.invoke(str.toString(), position)


                }
            })


            etNoOfPeople.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    onNumberOfPeopleClickListener?.invoke(str.toString(), position)


                }
            })


            etAmount.setText(item.amount.toString())
            etLodgeLocation.setText(item.location.toString())
            etHotelName.setText(item.remark.toString())

            etNoOfPeople.setText(item.noOFPerson.toString())
            tvFromDate.setText(item.date.toString())
            etToDate.setText(item.toDate.toString())


        }

    }

    private fun setupSpinner(spinner: Spinner, arrayResId: Int) {
        ArrayAdapter.createFromResource(
            spinner.context,
            arrayResId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
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
