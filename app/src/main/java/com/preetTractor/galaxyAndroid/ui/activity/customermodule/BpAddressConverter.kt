package com.preetTractor.galaxyAndroid.ui.activity.customermodule


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.BPAddress

class BpAddressConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<BPAddress>? {
        return value?.let {
            gson.fromJson(it, object : TypeToken<List<BPAddress>>() {}.type)
        }
    }

    @TypeConverter
    fun fromList(list: List<BPAddress>?): String {
        return gson.toJson(list)
    }
}
