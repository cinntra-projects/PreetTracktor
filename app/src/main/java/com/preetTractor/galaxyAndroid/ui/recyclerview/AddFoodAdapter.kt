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
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.expense.addExpense.ConveyanceModel
import com.preetTractor.galaxyAndroid.databinding.ItemFoodBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker


class AddFoodAdapter(
    private val itemList: ArrayList<ConveyanceModel>,
    private val context: Context,
    private val showEditButton: Boolean = true
) :
    RecyclerView.Adapter<AddFoodAdapter.ItemViewHolder>() {

    private var onEditBtnClickListener: ((String, Int) -> Unit)? = null

    private var onFromDateBtnClickListener: ((String, Int) -> Unit)? = null
    private var onLocationBtnClickListener: ((String, Int) -> Unit)? = null
    private var onToLocationBtnClickListener: ((String, Int) -> Unit)? = null
    private var onToDateClickListener: ((String, Int) -> Unit)? = null
    private var onNumberOfPeopleClickListener: ((String, Int) -> Unit)? = null
    private var onHotelNameClickListener: ((String, Int) -> Unit)? = null
    private var onCameraBtnClickListener: ((String, Int) -> Unit)? = null
    private var onDeleteBtnClickListener: ((String, Int) -> Unit)? = null
    private var onAmountBtnClickListener: ((Int, Int) -> Unit)? = null
    private var onModeBtnClickListener: ((String, Int) -> Unit)? = null

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
        onToLocationBtnClickListener = listener
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

    fun setOnModeBtnClickListener(listener: (String, Int) -> Unit) {
        onModeBtnClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemFoodBinding =
            ItemFoodBinding.inflate(inflater, parent, false)

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
            onModeBtnClickListener,
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
        val binding: ItemFoodBinding,

        ) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var etFoodLocation: EditText
        lateinit var etFoodAmount: EditText
        lateinit var etMeal: EditText
        lateinit var tvFoodDate: TextView
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
            onModeBtnClickListener: ((String, Int) -> Unit)?,
            item: ConveyanceModel
        ) {
            etFoodLocation = binding.etFoodLocation
            etFoodAmount = binding.etFoodAmount
            etMeal = binding.etMeal
            tvFoodDate = binding.tvFoodDate
            ivDelete = binding.ivDelete
            tvCamera = binding.tvFoodCamera
            spinnerMode = binding.spinnerFood

            setupSpinner(binding.spinnerFood, R.array.mode_food_spinner)
            spinnerMode.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        // Get the selected item text from the Spinner
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        onModeBtnClickListener?.invoke(selectedItem, positionHeader)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Handle case where no item is selected (optional)
                    }
                }


            tvFoodDate.text = Globals.getTodaysDate()

            tvFoodDate.transformIntoDatePicker(tvFoodDate.context, "dd/MM/yyyy", null)

            ivDelete.visibility = if (position == 0) View.GONE else View.VISIBLE



            tvCamera.setOnClickListener {
                onCameraBtnClickListener?.invoke("camera", position)
            }
            ivDelete.setOnClickListener {
                onDeleteBtnClickListener?.invoke("delete", position)
            }


            etFoodAmount.addTextChangedListener(object : TextWatcher {
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


            etFoodLocation.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    onLocationBtnClickListener?.invoke(str.toString(), position)


                }
            })



            tvFoodDate.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    onFromDateClickListener?.invoke(str.toString(), position)


                }
            })





            etFoodAmount.setText(item.amount.toString())
            etFoodLocation.setText(item.location.toString())
            etMeal.setText(item.remark.toString())

            tvFoodDate.setText(item.date.toString())



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
