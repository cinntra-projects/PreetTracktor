package com.preetTractor.galaxyAndroid.data.model.customer

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class JournalEntryLineBodyData(
    @SerializedName("Debit")
    var debit: String? = null,

    @SerializedName("Credit")
    var credit: String? = null,

    @SerializedName("Balance")
    var balance: Double = 0.0,

    @SerializedName("DueDate")
    var dueDate: String? = null,

    @SerializedName("Reference1")
    var reference1: String? = null,

    @SerializedName("Original")
    var original: String? = null,

    @SerializedName("OriginalJournal")
    var originalJournal: String? = null,

    @SerializedName("DocId")
    var docId: String? = null,

    @SerializedName("id")
    var id: String? = null,

    @SerializedName("EntryType")
    var entryType: String? = null,

    @SerializedName("AccountName")
    var accountName: String? = null
) : Serializable

