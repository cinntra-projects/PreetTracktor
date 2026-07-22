package com.preetTractor.galaxyAndroid.data.model.order.model.response.ba

/**
{
    "message": "Success",
    "status": 200,
    "data": [
        {
            "id": 15,
            "CardCode": "BACODE",
            "CardName": "Beauty Advisor Customer"
        }
    ]
}
*/
data class ModelBpListStatic(
    var message: String = "",
    var status: Int = 0,
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var id: Int = 0,
        var CardCode: String = "",
        var CardName: String = ""
    )
}