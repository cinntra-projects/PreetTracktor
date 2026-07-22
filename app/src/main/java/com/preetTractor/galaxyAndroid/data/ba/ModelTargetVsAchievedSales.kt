package com.preetTractor.galaxyAndroid.data.ba

/**
{
    "message": "success",
    "status": 200,
    "data": [
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Apr-2024",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "May-2024",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Jun-2024",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Jul-2024",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Aug-2024",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Sep-2024",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Oct-2024",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Nov-2024",
            "MonthlyAchievedSales": 1109.8,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Dec-2024",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Jan-2025",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Feb-2025",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        },
        {
            "MonthlyTargetSales": 1.6666666666666667,
            "Month": "Mar-2025",
            "MonthlyAchievedSales": 0,
            "FinancialYear": "2024-2025"
        }
    ]
}
*/
data class ModelTargetVsAchievedSales(
    var message: String = "",
    var status: Int = 0,
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var MonthlyTargetSales: Double = 0.0,
        var Month: String = "",
        var MonthlyAchievedSales: Double = 0.0,
        var FinancialYear: String = ""
    )
}