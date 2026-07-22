package com.preetTractor.galaxyAndroid.data.model.customer

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataCustomerLedger(
    @SerializedName("CardCode")
    var cardCode: String? = null,

    @SerializedName("CardName")
    var cardName: String? = null,

    @SerializedName("OpeningBalance")
    var openingBalance: String? = null,

    @SerializedName("ClosingBalance")
    var closingBalance: String? = null,

    @SerializedName("JournalEntryLines")
    var journalEntryLines: ArrayList<JournalEntryLineBodyData>? = null
) : Serializable
