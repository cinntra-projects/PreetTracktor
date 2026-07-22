package com.preetTractor.galaxyAndroid.helper

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.preetTractor.galaxyAndroid.data.expense.newexpense.DataExpenseNewList


class ExpenseDiffCallback(oldEmployeeList: List<DataExpenseNewList>, newEmployeeList: List<DataExpenseNewList>) :
    DiffUtil.Callback() {
    private val mOldEmployeeList: List<DataExpenseNewList> = oldEmployeeList
    private val mNewEmployeeList: List<DataExpenseNewList> = newEmployeeList

    override fun getOldListSize(): Int {
        return mOldEmployeeList.size
    }

    override fun getNewListSize(): Int {
        return mNewEmployeeList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldEmployeeList[oldItemPosition].id === mNewEmployeeList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee: DataExpenseNewList = mOldEmployeeList[oldItemPosition]
        val newEmployee: DataExpenseNewList = mNewEmployeeList[newItemPosition]

        return oldEmployee.approvalStatus == newEmployee.approvalStatus
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}