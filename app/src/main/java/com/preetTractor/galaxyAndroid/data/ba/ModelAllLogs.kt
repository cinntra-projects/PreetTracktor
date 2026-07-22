package com.preetTractor.galaxyAndroid.data.ba

/**
{
    "message": "Success",
    "status": 200,
    "data": [
        {
            "CardCode": "C1",
            "CardName": "Testing BP1",
            "Total": 1109.8
        },
        {
            "CardCode": "C2",
            "CardName": "Testing BP2",
            "Total": 1026.2
        }
    ]
}
*/
data class ModelAllLogs(
    val message: String? = "",
    val status: Int? = 0,
    val `data`: List<Data?>? = listOf()
) {
    data class Data(
        val CardCode: String? = "",
        val CardName: String? = "",
        val Total: Double? = 0.0
    )
}