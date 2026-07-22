package com.preetTractor.galaxyAndroid.ui.activity.customermodule


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.UTypeData

class UTypeDataConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<UTypeData> {
        return gson.fromJson(value, object : TypeToken<List<UTypeData>>() {}.type)
    }

    @TypeConverter
    fun fromList(list: List<UTypeData>): String {
        return gson.toJson(list)
    }
}
