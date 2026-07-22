package com.preetTractor.galaxyAndroid.helper

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar


class DatePickerDialogFragment(callback: OnDateSetListener?) : DialogFragment() {
    private var mDateSetListener: OnDateSetListener? = callback

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal = Calendar.getInstance()
        return DatePickerDialog(
            requireActivity(),
            mDateSetListener, cal[Calendar.YEAR],
            cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
        )
    }
}