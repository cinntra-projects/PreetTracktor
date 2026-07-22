package com.preetTractor.galaxyAndroid.ui.activity.customermodule



import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.SalesEmployeeItemKt

class SaleEmployeeItemConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<SalesEmployeeItemKt> {
        return gson.fromJson(value, object : TypeToken<List<SalesEmployeeItemKt>>() {}.type)
    }

    @TypeConverter
    fun fromList(list: List<SalesEmployeeItemKt>): String {
        return gson.toJson(list)
    }
}
