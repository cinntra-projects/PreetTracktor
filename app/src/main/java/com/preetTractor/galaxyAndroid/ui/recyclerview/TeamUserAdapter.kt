package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.data.team.DataTeamList
import com.preetTractor.galaxyAndroid.databinding.ItemTeamUserBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.*
import com.preetTractor.galaxyAndroid.ui.activity.test.DataHeirarchYList
import com.preetTractor.galaxyAndroid.ui.activity.test.EmployeeAdapter
import com.preetTractor.galaxyAndroid.ui.activity.test.ResponseHeirarchYList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamUserAdapter(
    private val itemList: List<DataTeamList>,
    private val context: Context,
    var fromWhere: String
) :
    RecyclerView.Adapter<TeamUserAdapter.ItemViewHolder>() {

    private var filteredItemList: List<DataTeamList> = itemList.toMutableList()
    private var onItemClickListener: ((DataTeamList, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (DataTeamList, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onItemForwardClickListener: ((DataTeamList, Int) -> Unit)? = null

    fun setOnItemForwardClickListener(listener: (DataTeamList, Int) -> Unit) {
        onItemForwardClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemTeamUserBinding = ItemTeamUserBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: DataTeamList = filteredItemList[position]

        if (item.EmployeeID.isNotEmpty()) {
            holder.binding.tvUser.text = "${item.firstName} ${item.lastName} -(${item.EmployeeID})"
        } else {
            holder.binding.tvUser.text = "${item.firstName} ${item.lastName}"
        }
        //  holder.binding.tvUser.text = "${item.SalesEmployeeName}- (${item.EmployeeID})"

        holder.binding.tvUser.setOnClickListener {
            /* onItemClickListener?.let { click ->
                 click(item, position)
             }*/
            /*  if (holder.binding.recyclerView.visibility==View.VISIBLE){
                 holder.binding.recyclerView.visibility=View.GONE
              }else{
                  holder.binding.recyclerView.visibility=View.VISIBLE
                  Toast.makeText(holder.itemView.context, "LOADING....", Toast.LENGTH_SHORT).show()
              }*/
            item.isExpanded = !item.isExpanded
            if (item.isExpanded) {
                holder.binding.recyclerView.apply {
                    visibility = View.VISIBLE

                }
            } else {
                holder.binding.recyclerView.visibility = View.GONE
            }


            //  Toast.makeText(holder.itemView.context, "Loading....", Toast.LENGTH_SHORT).show()

            Snackbar.make(
                holder.binding.recyclerView,
                "Loading please wait....",
                Snackbar.LENGTH_SHORT
            ).show()
            getListing(item.SalesEmployeeCode, holder, position)
        }


        holder.binding.ivForwradHeadeBigr.setOnClickListener {
            onItemForwardClickListener?.let { click ->
                click(item, position)
            }

        }
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    inner class ItemViewHolder(val binding: ItemTeamUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Function to filter the list based on search query
    fun filterList(query: String) {
        filteredItemList = if (query.isEmpty()) {
            itemList
        } else {
            itemList.filter {
                it.SalesEmployeeName.contains(query, ignoreCase = true)
            }
        }

        notifyDataSetChanged()
    }


    private fun getListing(dateStr: String, holder: ItemViewHolder, position: Int) {
        val hde = JsonObject().apply {
            addProperty("SalesEmployeeCode", dateStr)

        }

        val call = RetrofitClient.apiService.callHerirachyListing(hde)
        call.enqueue(object : Callback<ResponseHeirarchYList> {
            override fun onResponse(
                call: Call<ResponseHeirarchYList>,
                response: Response<ResponseHeirarchYList>
            ) {
                response.body()?.let {
                    if (it.status == 200) {
                        if (response.body()!!.data.isNotEmpty()) {
                            val employees = response.body()!!.data
                            setupRecyclerView(employees, holder, position)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseHeirarchYList>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")

            }
        })
    }


    companion object {
        private const val TAG = "TestHerarchyActivity"
    }


    private fun setupRecyclerView(
        employees: List<DataHeirarchYList>,
        holder: ItemViewHolder,
        position: Int
    ) {

        if (employees.isNotEmpty()) {
            val adapter = EmployeeAdapter(
                employees = employees.get(0).reportees,
                onItemClickListener = { dataHeirarchYList, level ->
                    // Handle the click on an employee item
                    /* Toast.makeText(
                         holder.itemView.context,
                         "Clicked on ${employee.SalesEmployeeName} at level $level",
                         Toast.LENGTH_SHORT
                     ).show()*/

                    /*  if (fromWhere == Constant.WHERE_INTENT_VALUE_STATUS) {
                          holder.itemView.context.startActivity(
                              Intent(
                                  holder.itemView.context,
                                  AttendanceBackGroundListActivity::class.java
                              ).apply {
                                  putExtra("sales", dataHeirarchYList.SalesEmployeeCode)
                                  putExtra("name", dataHeirarchYList.SalesEmployeeName)
                              })

                      }


                      if (fromWhere.equals(Constant.WHERE_INTENT_VALUE_LEAVE)) {
                          holder.itemView.context.startActivity(
                              Intent(holder.itemView.context, LeaveActivity::class.java).apply {
                                  putExtra(
                                      Constant.WHERE_INTENT_VALUE_SALES,
                                      dataHeirarchYList.SalesEmployeeCode
                                  )
                                  putExtra(
                                      Constant.WHERE_INTENT_VALUE_SALES_NAME,
                                      dataHeirarchYList.SalesEmployeeName
                                  )
                              })

                      }
                      if (fromWhere.equals(Constant.WHERE_INTENT_VALUE_EXPENSE)) {
                          holder.itemView.context.startActivity(
                              Intent(
                                  holder.itemView.context,
                                  ExpenseRequestActivity::class.java
                              ).apply {
                                  putExtra(
                                      Constant.WHERE_INTENT_VALUE_SALES,
                                      dataHeirarchYList.SalesEmployeeCode
                                  )
                                  putExtra(
                                      Constant.WHERE_INTENT_VALUE_SALES_NAME,
                                      dataHeirarchYList.SalesEmployeeName
                                  )
                              })


                      }

                      if (fromWhere.equals(Constant.WHERE_INTENT_VALUE_ATTENDANCE)) {
                          holder.itemView.context.startActivity(
                              Intent(holder.itemView.context, AttendanceActivity::class.java).apply {
                                  putExtra(
                                      Constant.WHERE_INTENT_VALUE_SALES,
                                      dataHeirarchYList.SalesEmployeeCode
                                  )
                                  putExtra(
                                      Constant.WHERE_INTENT_VALUE_SALES_NAME,
                                      dataHeirarchYList.SalesEmployeeName
                                  )
                              })

                          PrefsByShubh.putString(
                              Constant.SALESEMPLOYEECODE,
                              dataHeirarchYList.SalesEmployeeCode
                          )
                          PrefsByShubh.putString(
                              Constant.SALESEMPLOYEENAME,
                              dataHeirarchYList.SalesEmployeeName
                          )
                          PrefsByShubh.putString(Constant.FLAG, "_FROM_ATTENDANCE")

                      }*/

                },
                onItemInnerListForwardClickListener = { dataHeirarchYList, level ->

                    if (fromWhere == Constant.WHERE_INTENT_VALUE_BEAT_PLAN) {
                        holder.itemView.context.startActivity(
                            Intent(
                                holder.itemView.context,
                                BeatPlanActivity::class.java
                            ).apply {
//                                putExtra("sales", dataHeirarchYList.SalesEmployeeCode)
                                putExtra("sales", dataHeirarchYList.id.toString())
                                putExtra("name", dataHeirarchYList.SalesEmployeeName)

                            })

                        Log.e("BEAT PLAN", "EXECUTION......")


                    }

                    if (fromWhere == Constant.WHERE_INTENT_VALUE_STATUS) {
                        holder.itemView.context.startActivity(
                            Intent(
                                holder.itemView.context,
                                AttendanceBackGroundListActivity::class.java
                            ).apply {
                                putExtra("sales", dataHeirarchYList.SalesEmployeeCode)
                                putExtra("name", dataHeirarchYList.SalesEmployeeName)
                                putExtra("teamStatusId", dataHeirarchYList.id.toString())
                            })

                    }


                    if (fromWhere == Constant.WHERE_INTENT_VALUE_LEAVE) {
                        holder.itemView.context.startActivity(
                            Intent(holder.itemView.context, LeaveActivity::class.java).apply {
                                putExtra(
                                    Constant.WHERE_INTENT_VALUE_SALES,
                                    dataHeirarchYList.SalesEmployeeCode
                                )
                                putExtra(
                                    Constant.WHERE_INTENT_VALUE_SALES_NAME,
                                    dataHeirarchYList.SalesEmployeeName
                                )
                                putExtra("itemId", dataHeirarchYList.id.toString())
                            })
                    }


                    if (fromWhere.equals(Constant.WHERE_INTENT_VALUE_EXPENSE)) {
                        holder.itemView.context.startActivity(
                            Intent(
                                holder.itemView.context,
                                ExpenseRequestActivity::class.java
                            ).apply {
                                putExtra(
                                    Constant.WHERE_INTENT_VALUE_SALES,
                                    dataHeirarchYList.SalesEmployeeCode
                                )
                                putExtra(
                                    Constant.WHERE_INTENT_VALUE_SALES_NAME,
                                    dataHeirarchYList.SalesEmployeeName
                                )
                                putExtra("expenseItemId", dataHeirarchYList.id.toString())

                            })


                    }

                    if (fromWhere.equals(Constant.WHERE_INTENT_VALUE_ATTENDANCE)) {
                        holder.itemView.context.startActivity(
                            Intent(holder.itemView.context, AttendanceActivity::class.java).apply {
                                putExtra(
                                    Constant.WHERE_INTENT_VALUE_SALES,
                                    dataHeirarchYList.SalesEmployeeCode
                                )
                                putExtra(
                                    Constant.WHERE_INTENT_VALUE_SALES_NAME,
                                    dataHeirarchYList.SalesEmployeeName
                                )

                                putExtra("attendanceItemId", dataHeirarchYList.id.toString())
                            })

                        PrefsByShubh.putString(
                            Constant.SALESEMPLOYEECODE,
                            dataHeirarchYList.SalesEmployeeCode
                        )
                        PrefsByShubh.putString(
                            Constant.SALESEMPLOYEENAME,
                            dataHeirarchYList.SalesEmployeeName
                        )
                        PrefsByShubh.putString(Constant.FLAG, "_FROM_ATTENDANCE")

                    }
                }
            )




            holder.binding.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.binding.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

            adapter.setOnForwardItemClickListener { dataHeirarchYList, i ->

                if (fromWhere.equals(Constant.WHERE_INTENT_VALUE_BEAT_PLAN)) {
                    holder.itemView.context.startActivity(
                        Intent(
                            holder.itemView.context,
                            BeatPlanActivity::class.java
                        ).apply {
//                            putExtra("sales", dataHeirarchYList.SalesEmployeeCode)
                            putExtra("sales", dataHeirarchYList.id.toString())
                            putExtra("name", dataHeirarchYList.SalesEmployeeName)
                        })

                    Log.e("BEAT PLAN", "EXECUTION......")


                }


                if (fromWhere == Constant.WHERE_INTENT_VALUE_STATUS) {
                    holder.itemView.context.startActivity(
                        Intent(
                            holder.itemView.context,
                            AttendanceBackGroundListActivity::class.java
                        ).apply {
                            putExtra("sales", dataHeirarchYList.SalesEmployeeCode)
                            putExtra("name", dataHeirarchYList.SalesEmployeeName)
                            putExtra("teamStatusId", dataHeirarchYList.id.toString())
                        })

                }


                if (fromWhere.equals(Constant.WHERE_INTENT_VALUE_LEAVE)) {
                    holder.itemView.context.startActivity(
                        Intent(holder.itemView.context, LeaveActivity::class.java).apply {
                            putExtra(
                                Constant.WHERE_INTENT_VALUE_SALES,
                                dataHeirarchYList.SalesEmployeeCode
                            )
                            putExtra(
                                Constant.WHERE_INTENT_VALUE_SALES_NAME,
                                dataHeirarchYList.SalesEmployeeName
                            )
                            putExtra("itemId", dataHeirarchYList.id.toString())
                        })

                }
                if (fromWhere.equals(Constant.WHERE_INTENT_VALUE_EXPENSE)) {
                    holder.itemView.context.startActivity(
                        Intent(holder.itemView.context, ExpenseRequestActivity::class.java).apply {
                            putExtra(
                                Constant.WHERE_INTENT_VALUE_SALES,
                                dataHeirarchYList.SalesEmployeeCode
                            )
                            putExtra(
                                Constant.WHERE_INTENT_VALUE_SALES_NAME,
                                dataHeirarchYList.SalesEmployeeName
                            )
                            putExtra("expenseItemId", dataHeirarchYList.id.toString())
                        })


                }

                if (fromWhere.equals(Constant.WHERE_INTENT_VALUE_ATTENDANCE)) {
                    holder.itemView.context.startActivity(
                        Intent(holder.itemView.context, AttendanceActivity::class.java).apply {
                            putExtra(
                                Constant.WHERE_INTENT_VALUE_SALES,
                                dataHeirarchYList.SalesEmployeeCode
                            )
                            putExtra(
                                Constant.WHERE_INTENT_VALUE_SALES_NAME,
                                dataHeirarchYList.SalesEmployeeName
                            )
                            putExtra("attendanceItemId", dataHeirarchYList.id.toString())
                        })

                    PrefsByShubh.putString(
                        Constant.SALESEMPLOYEECODE,
                        dataHeirarchYList.SalesEmployeeCode
                    )
                    PrefsByShubh.putString(
                        Constant.SALESEMPLOYEENAME,
                        dataHeirarchYList.SalesEmployeeName
                    )
                    PrefsByShubh.putString(Constant.FLAG, "_FROM_ATTENDANCE")

                }
            }
        } else {
            Toast.makeText(holder.itemView.context, "no Data Found", Toast.LENGTH_SHORT).show()
        }


    }


}
