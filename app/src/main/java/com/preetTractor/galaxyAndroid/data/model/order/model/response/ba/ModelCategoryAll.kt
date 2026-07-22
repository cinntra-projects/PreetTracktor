package com.preetTractor.galaxyAndroid.data.model.order.model.response.ba

/**
{
    "message": "Success",
    "status": 200,
    "data": [
        {
            "id": 4,
            "CategoryName": "CCTV",
            "CategoryImageURL": "",
            "CreateDate": null,
            "CreateTime": null,
            "UpdateDate": null,
            "UpdateTime": null
        },
        {
            "id": 3,
            "CategoryName": "FANS",
            "CategoryImageURL": "",
            "CreateDate": null,
            "CreateTime": null,
            "UpdateDate": null,
            "UpdateTime": null
        },
        {
            "id": 6,
            "CategoryName": "Laptop items",
            "CategoryImageURL": "/static/image/Item-Category/Ledure%20image.jfif",
            "CreateDate": null,
            "CreateTime": null,
            "UpdateDate": null,
            "UpdateTime": null
        },
        {
            "id": 2,
            "CategoryName": "test inventory12",
            "CategoryImageURL": "/static/image/Item-Category/image%20(8).png",
            "CreateDate": null,
            "CreateTime": null,
            "UpdateDate": null,
            "UpdateTime": null
        },
        {
            "id": 1,
            "CategoryName": "Testing Inventory",
            "CategoryImageURL": "/static/image/Item-Category/image.png",
            "CreateDate": null,
            "CreateTime": null,
            "UpdateDate": null,
            "UpdateTime": null
        },
        {
            "id": 7,
            "CategoryName": "tv",
            "CategoryImageURL": "",
            "CreateDate": null,
            "CreateTime": null,
            "UpdateDate": null,
            "UpdateTime": null
        }
    ]
}
*/
data class ModelCategoryAll(
    var message: String = "",
    var status: Int = 0,
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var id: Int = 0,
        var CategoryName: String = "",
        var CategoryImageURL: String = "",
        var CreateDate: Any = Any(),
        var CreateTime: Any = Any(),
        var UpdateDate: Any = Any(),
        var UpdateTime: Any = Any()
    )
}