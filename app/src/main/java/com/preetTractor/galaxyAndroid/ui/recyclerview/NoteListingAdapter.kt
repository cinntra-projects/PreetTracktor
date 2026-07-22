package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.model.notes.DataAllNotes
import com.preetTractor.galaxyAndroid.databinding.ItemNotesCustomerBinding
import com.preetTractor.galaxyAndroid.helper.Globals


class NoteListingAdapter(
    itemList: List<DataAllNotes>,
    context: Context
) :
    RecyclerView.Adapter<NoteListingAdapter.ItemViewHolder>() {
    private val itemList: List<DataAllNotes>
    private val context: Context

    init {
        this.itemList = itemList
        this.context = context
    }

    private var onItemClickListener: ((DataAllNotes) -> Unit)? = null
    fun setOnItemClickListener(listener: (DataAllNotes) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemKebabClickListener: ((DataAllNotes) -> Unit)? = null
    fun setOnItemKebabClickListener(listener: (DataAllNotes) -> Unit) {
        onItemKebabClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemNotesCustomerBinding =
            ItemNotesCustomerBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: DataAllNotes = itemList[position]
        holder.binding.apply {
            tvRemarkHeading.text = item.Title
            tvRemark.text = item.Remark
            tvDateNotes.text = "${
                Globals.dateStringConvertToDesiredFormat(
                    item.Create_Date,
                    "yyyy-MM-dd", "dd/MM/yyyy"
                )
            } - ${item.Create_Time}"

            // Set up the inner RecyclerView
            val innerAdapter = AttachmentNotesAdapter(item.Attach)
            holder.binding.rvInnerListAttachment.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = innerAdapter

            }
            innerAdapter.notifyDataSetChanged()

        }



        holder.binding.ivKebab.setOnClickListener { click ->
            onItemKebabClickListener?.let { click ->
                click(item)
            }
        }

        holder.itemView.setOnClickListener {

            onItemClickListener?.let { click ->
                click(item)

            }
        }


    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(val binding: ItemNotesCustomerBinding) :
        RecyclerView.ViewHolder(binding.root)
}