package com.preetTractor.galaxyAndroid.helper

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.preetTractor.galaxyAndroid.data.LeaveStatusData


class LeaveDiffCallback(oldEmployeeList: List<LeaveStatusData>, newEmployeeList: List<LeaveStatusData>) :
    DiffUtil.Callback() {
    private val mOldEmployeeList: List<LeaveStatusData> = oldEmployeeList
    private val mNewEmployeeList: List<LeaveStatusData> = newEmployeeList

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
        val oldEmployee: LeaveStatusData = mOldEmployeeList[oldItemPosition]
        val newEmployee: LeaveStatusData = mNewEmployeeList[newItemPosition]

        return oldEmployee.Approval_Status.equals(newEmployee.Approval_Status)
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}