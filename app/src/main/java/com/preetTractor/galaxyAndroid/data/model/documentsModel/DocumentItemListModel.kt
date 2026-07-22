package com.preetTractor.galaxyAndroid.moreUi.model.documentsModel

import android.os.Parcel
import android.os.Parcelable

data class DocumentItemListModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val errors: String,
    val status: Int
) {



    data class Data(
        val id: Int,
        val image: String,
        val category: String,
        val create_date: String,
        val create_time: String,
        val description: String,
        val document_id: String,
        val default_img_url: String,
        val `file`: String,
        val file_type: String,
        val title: String,
        val tags: String,
        val type: String,
        val size: String,
        val format: String,
        val thumbnail: String
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: ""
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(image)
            parcel.writeString(category)
            parcel.writeString(create_date)
            parcel.writeString(create_time)
            parcel.writeString(description)
            parcel.writeString(document_id)
            parcel.writeString(default_img_url)
            parcel.writeString(`file`)
            parcel.writeString(file_type)
            parcel.writeString(title)
            parcel.writeString(tags)
            parcel.writeString(type)
            parcel.writeString(size)
            parcel.writeString(format)
            parcel.writeString(thumbnail)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Data> {
            override fun createFromParcel(parcel: Parcel): Data {
                return Data(parcel)
            }

            override fun newArray(size: Int): Array<Data?> {
                return arrayOfNulls(size)
            }
        }
    }


}