package com.preetTractor.galaxyAndroid.ui.activity.customermodule.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.BPListResponse

class CustomersAdapterDetals(private val dataList: List<BPListResponse>) :
    RecyclerView.Adapter<CustomersAdapterDetals.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardCode: TextView = view.findViewById(R.id.cardNumber)
        val cardName: TextView = view.findViewById(R.id.customerName)
        val email: TextView = view.findViewById(R.id.tvEmail)
        val phone: TextView = view.findViewById(R.id.tvPhoneno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.customers_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.cardCode.text = "Code: ${data.cardCode}"
        holder.cardName.text = "${data.cardName}"
        holder.email.text = "${data.emailAddress}"
        holder.phone.text = "${data.phone1}"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CustomerDetailActivity::class.java)
            /*intent.putExtra("CustomerCardCode",data.cardCode.toString())
            intent.putExtra("flagCustomer","CustomerModule")*/
            intent.putExtra(Constant.CustomerCardCode, data.cardCode.toString())
            intent.putExtra(Constant.flagCustomerModule, "CustomerModule")

            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = dataList.size
}