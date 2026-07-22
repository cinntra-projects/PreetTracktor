package com.preetTractor.galaxyAndroid.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelPreviousOrder(
      val data: List<Data>

):Parcelable {
      @Parcelize
      data class Data(
            val name: String,
            val mobile: String,
            val email: String
      ):Parcelable
}
