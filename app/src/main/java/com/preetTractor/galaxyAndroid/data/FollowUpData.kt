package com.preetTractor.galaxyAndroid.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FollowUpData(
     var SourceID: String? = null,
      var Comment: String? = null,
      var Emp: Int? = null,
      var From: String? = null,
      var To: String? = null,
      var Time: String? = null,
      var SourceType: String? = null,
      var CreateDate: String? = null,
      var CreateTime: String? = null,
      var Emp_Name: String? = null,
      var Type: String? = null,
      var Subject: String? = null,
      var Mode: String? = null,
      var leadType: String? = null
)
