package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.expense.addExpense.ConveyanceModel
import com.preetTractor.galaxyAndroid.databinding.ItemConveyanceBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker


class AddConveyanceAdapter(
    private val itemList: ArrayList<ConveyanceModel>,
    private val context: Context,
    private val showEditButton: Boolean = true,
    private val showToLocation: Boolean = false
) :
    RecyclerView.Adapter<AddConveyanceAdapter.ItemViewHolder>() {

    private var onEditBtnClickListener: ((String, Int) -> Unit)? = null
    private var onDateBtnClickListener: ((String, Int) -> Unit)? = null
    private var onModeBtnClickListener: ((String, Int) -> Unit)? = null
    private var onLocationBtnClickListener: ((String, Int) -> Unit)? = null
    private var onToLocationBtnClickListener: ((String, Int) -> Unit)? = null
    private var onRemarkBtnClickListener: ((String, Int) -> Unit)? = null
    private var onCameraBtnClickListener: ((String, Int) -> Unit)? = null
    private var onDeleteBtnClickListener: ((String, Int) -> Unit)? = null
    private var onAmountBtnClickListener: ((Int, Int) -> Unit)? = null

    fun setonEditBtnClickListener(listener: (String, Int) -> Unit) {
        onEditBtnClickListener = listener
    }

    fun setOnDateClickListener(listener: (String, Int) -> Unit) {
        onDateBtnClickListener = listener
    }

    fun setOnAmountClickListener(listener: (Int, Int) -> Unit) {
        onAmountBtnClickListener = listener
    }

    fun setOnModeBtnClickListener(listener: (String, Int) -> Unit) {
        onModeBtnClickListener = listener
    }

    fun setOnLocationBtnClickListener(listener: (String, Int) -> Unit) {
        onLocationBtnClickListener = listener
    }

    fun setOnToLocationBtnClickListener(listener: (String, Int) -> Unit) {
        onToLocationBtnClickListener = listener
    }

    fun setOnRemarkBtnClickListener(listener: (String, Int) -> Unit) {
        onRemarkBtnClickListener = listener
    }

    fun setOnCameraBtnClickListener(listener: (String, Int) -> Unit) {
        onCameraBtnClickListener = listener
    }

    fun setOnDeleteBtnClickListener(listener: (String, Int) -> Unit) {
        onDeleteBtnClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemConveyanceBinding =
            ItemConveyanceBinding.inflate(inflater, parent, false)

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
            onRemarkBtnClickListener,
            onDateBtnClickListener,
            onModeBtnClickListener,
            onEditBtnClickListener,
            onLocationBtnClickListener,
            onToLocationBtnClickListener,
            onDeleteBtnClickListener, item
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
        val binding: ItemConveyanceBinding,

        ) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var etLocation: EditText
        lateinit var etToLocation: EditText
        lateinit var etAmount: EditText
        lateinit var etRemark: EditText
        lateinit var etDate: EditText
        lateinit var ivDelete: ImageView
        lateinit var tvCamera: ImageView
        lateinit var spinnerMode: Spinner
        fun onBind(
            positionHeader: Int, onCameraBtnClickListener: ((String, Int) -> Unit)?,
            onAmountBtnClickListener: ((Int, Int) -> Unit)?,
            onRemarkBtnClickListener: ((String, Int) -> Unit)?,
            onDateBtnClickListener: ((String, Int) -> Unit)?,
            onModeBtnClickListener: ((String, Int) -> Unit)?,
            onEditBtnClickListener: ((String, Int) -> Unit)?,
            onLocationBtnClickListener: ((String, Int) -> Unit)?,
            onToLocationBtnClickListener: ((String, Int) -> Unit)?,
            onDeleteBtnClickListener: ((String, Int) -> Unit)?,
            item: ConveyanceModel
        ) {
            etLocation = binding.etLocation
            etToLocation = binding.etToLocation
            etAmount = binding.etAmount
            etRemark = binding.etRemark
            etDate = binding.tvDate
            ivDelete = binding.ivDelete
            tvCamera = binding.tvCamera
            spinnerMode = binding.spinnerMode
            etDate.setText(Globals.getTodaysDate())
            etDate.transformIntoDatePicker(etDate.context, "dd/MM/yyyy", null)
            ivDelete.visibility = if (position == 0) View.GONE else View.VISIBLE
            etToLocation.visibility = if (showToLocation) View.VISIBLE else View.GONE



            setupSpinner(binding.spinnerMode, R.array.mode_spinner)

            tvCamera.setOnClickListener {
                onCameraBtnClickListener?.invoke("camera", position)
            }
            ivDelete.setOnClickListener {
                onDeleteBtnClickListener?.invoke("delete", position)
            }

            etLocation.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onLocationBtnClickListener?.invoke(str.toString(), position)
                }
            })
            etDate.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onDateBtnClickListener?.invoke(str.toString(), position)
                    Log.e("ADAPTER", "onTextChanged: $str")
                }
            })
            etRemark.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onRemarkBtnClickListener?.invoke(str.toString(), position)
                }
            })
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
            etToLocation.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onToLocationBtnClickListener?.invoke(str.toString(), position)
                }
            })
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
            etAmount.setText(item.amount.toString())
            etLocation.setText(item.location)
            etRemark.setText(item.remark)
            etDate.setText(item.date)


           binding.spinnerMode.setSelection(getIndex(binding.spinnerMode, item.mode))

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
