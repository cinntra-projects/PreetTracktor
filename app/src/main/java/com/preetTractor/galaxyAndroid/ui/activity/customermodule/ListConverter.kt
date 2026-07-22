package com.preetTractor.galaxyAndroid.ui.activity.customermodule


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.PayMentTerm

class ListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<PayMentTerm> {
        return gson.fromJson(value, object : TypeToken<List<PayMentTerm>>() {}.type)
    }

    @TypeConverter
    fun fromList(list: List<PayMentTerm>): String {
        return gson.toJson(list)
    }
}
