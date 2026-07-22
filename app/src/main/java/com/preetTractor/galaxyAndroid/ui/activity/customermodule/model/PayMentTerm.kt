package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "table_payment_term")
data class PayMentTerm(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("GroupNumber")
    @Expose
    var groupNumber: String? = null,

    @SerializedName("PaymentTermsGroupName")
    @Expose
    var paymentTermsGroupName: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 821834809536410029L
    }
}
