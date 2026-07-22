package com.preetTractor.galaxyAndroid.data.model.order.model.response.ba

/**
{
    "message": "Success",
    "status": 200,
    "data": [
        {
            "id": 1,
            "CardCode": "C1",
            "CardName": "Testing BP1"
        },
        {
            "id": 2,
            "CardCode": "C2",
            "CardName": "Testing BP2"
        }
    ]
}
*/
data class ModelBusinessPartnerAll(
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