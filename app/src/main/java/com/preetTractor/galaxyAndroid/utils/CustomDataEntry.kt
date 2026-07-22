package com.preetTractor.galaxyAndroid.utils

import com.anychart.chart.common.dataentry.ValueDataEntry


class CustomDataEntry(x: String, value: Int, color: Int) : ValueDataEntry(x, value) {
    init {
        val hexColor = String.format("#%06X", 0xFFFFFF and color)
        setValue("fill", hexColor)
    }

    fun setSelected(selected: Boolean) {
        setValue("selected", selected)
    }
}

