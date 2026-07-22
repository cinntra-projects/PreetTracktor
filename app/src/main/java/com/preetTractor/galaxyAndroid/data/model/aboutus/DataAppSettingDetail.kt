package com.preetTractor.galaxyAndroid.moreUi.model.aboutus

data class DataAppSettingDetail(
    val about_us: List<AboutU>,
    val contact_us: List<ContactU>,
    val sales_email: String,
    val sales_mobile: String,
    val term_condition: List<TermCondition>
)