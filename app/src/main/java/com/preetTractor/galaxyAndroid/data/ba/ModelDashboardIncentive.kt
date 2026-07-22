package com.preetTractor.galaxyAndroid.data.ba

/**
{
    "message": "Success",
    "status": 200,
    "data": [
        {
            "EmployeeName": "Akshat",
            "EmployeeCode": "14",
            "IncentiveType": "Target Achieved",
            "ServiceType": "Monthly",
            "PlannedSalesTarget": "20",
            "AchievedSalesTarget": 1109.8,
            "IncentiveSlabPercentage": 1.0,
            "TotalIncentiveCalculations": 11.1
        }
    ]
}
*/
data class ModelDashboardIncentive(
    var message: String = "",
    var status: Int = 0,
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var EmployeeName: String = "",
        var EmployeeCode: String = "",
        var IncentiveType: String = "",
        var ServiceType: String = "",
        var PlannedSalesTarget: String = "",
        var AchievedSalesTarget: Double = 0.0,
        var IncentiveSlabPercentage: Double = 0.0,
        var TotalIncentiveCalculations: Double = 0.0
    )
}