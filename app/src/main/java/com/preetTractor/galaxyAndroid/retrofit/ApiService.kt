package com.preetTractor.galaxyAndroid.retrofit

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.data.AllLeadResponse
import com.preetTractor.galaxyAndroid.data.AllLeaveModel
import com.preetTractor.galaxyAndroid.data.AttachmentModel
import com.preetTractor.galaxyAndroid.data.BdrcModel
import com.preetTractor.galaxyAndroid.data.BeatPlanCustomerDropDownModel
import com.preetTractor.galaxyAndroid.data.ChatModel
import com.preetTractor.galaxyAndroid.data.ChatResponse
import com.preetTractor.galaxyAndroid.data.DashBoardCounterResponse
import com.preetTractor.galaxyAndroid.data.DynamicField.DynamicFieldResponse
import com.preetTractor.galaxyAndroid.data.DynamicFieldsListModelClass
import com.preetTractor.galaxyAndroid.data.FilterOverAll
import com.preetTractor.galaxyAndroid.data.FollowUpData
import com.preetTractor.galaxyAndroid.data.GlobalResponse
import com.preetTractor.galaxyAndroid.data.LeadDocumentResponse
import com.preetTractor.galaxyAndroid.data.LeadResponse
import com.preetTractor.galaxyAndroid.data.LeadSourceAllResponseModel
import com.preetTractor.galaxyAndroid.data.LeadTypeResponse
import com.preetTractor.galaxyAndroid.data.LoginSignUpModel
import com.preetTractor.galaxyAndroid.data.ModeOfTravelResponse
import com.preetTractor.galaxyAndroid.data.ResponseBackGroundLocation
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.ResponseInnerAttendance
import com.preetTractor.galaxyAndroid.data.ResponseJsonDataItem
import com.preetTractor.galaxyAndroid.data.ResponseOuterAttendanceListing
import com.preetTractor.galaxyAndroid.data.SaleEmployeeResponse
import com.preetTractor.galaxyAndroid.data.SalesEmployeeItem
import com.preetTractor.galaxyAndroid.data.beatplan.ResponseBeatPlan
import com.preetTractor.galaxyAndroid.data.beatplan.ResponseCityAll
import com.preetTractor.galaxyAndroid.data.beatplan.ResponseStateAll
import com.preetTractor.galaxyAndroid.data.model.CityResponse
import com.preetTractor.galaxyAndroid.data.model.TodayVisitDashboardResponse
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseCategoryDashboard
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseCustomerOne
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseItemListCustomerDashboard
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseJournalEntryBpWise
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseOutletPicsFromCustomer
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseSecondaryCustomerList
import com.preetTractor.galaxyAndroid.data.model.notes.ResponseAllNotes
import com.preetTractor.galaxyAndroid.data.model.notes.ResponseBpOverview
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ba.ModelBpListStatic
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.CountryResponse
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.CustomerBusinessRes
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.IndustryResponse
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.PayMentTermsDetail
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.PerformaInvoiceListRequestModel
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.ResponseBusinessType
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.ResponseZoneDropDown
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.StateData
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.StateRespose
import com.preetTractor.galaxyAndroid.ui.activity.test.ResponseHeirarchYList
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("photos")
    suspend fun getData(): Response<List<ResponseJsonDataItem>>
    @GET("photos")
    suspend fun getDataResponse(): Response<ResponseJsonDataItem>


    @GET("employee/all")
    fun loginSignUpApi(): Call<ResponseGlobal>


    @POST("activity/punch_daily_attendance")
    fun punchDailyAttendance(@Body requestBody: MultipartBody?): Call<ResponseGlobal>


    @POST("activity/galaxy_create_note")
    fun createNote(@Body requestBody: MultipartBody?): Call<ResponseGlobal>


    @POST("activity/galaxy_reschedule_beatplan")
    fun rescheduleBeatPlan(@Body jsonObject: JsonObject): Call<ResponseGlobal>



    @POST("activity/galaxy_create_outlet")
    fun createGalaxyOutlet(@Body requestBody: MultipartBody?): Call<ResponseGlobal>


    @POST("activity/galaxy_tracking_create_many")
    fun galaxyTrackingCreate(@Body jsonObject: JsonArray?): Call<ResponseGlobal>

    @POST("activity/galaxy_tracking_all_filter")
    fun galaxyTrackingFilter(@Body jsonObject: JsonObject?): Call<ResponseBackGroundLocation>

    @POST("activity/galaxy_beatplan_customer_all")
    fun callGalaxyBeatPlanCustomerAll(@Body jsonObject: JsonObject?): Call<ResponseBeatPlan>


    @POST("employee/galaxy_user_reportingto_schema")
    fun callHerirachyListing(@Body jsonObject: JsonObject?): Call<ResponseHeirarchYList>


    @POST("activity/galaxy_update_beatplan")
    fun updateBeatPlan(@Body jsonObject: MultipartBody): Call<ResponseGlobal>


    @POST("activity/galaxy_beatplan_one")
    fun getBeatPlanOne(@Body jsonObject: JsonObject?): Call<ResponseBeatPlan>



    @POST("activity/galaxy_all_attach_outlet")
    fun callOutletPicApi(@Body jsonObject: JsonObject?): Call<ResponseOutletPicsFromCustomer>



    @POST("journalentries/bp_wise")

    fun bp_general_entries(@Body obj: JsonObject): Call<ResponseJournalEntryBpWise>


    @POST("item/filter_bpitem_all")

    fun getItemListingForCustomersOverview(@Body obj: JsonObject): Call<ResponseItemListCustomerDashboard>


    @POST("item/filter_bpgroup_item")

    fun getCategoryListingForCustomersOverview(@Body obj: JsonObject): Call<ResponseCategoryDashboard>



    @POST("activity/checklist_all_filter")
    fun galaxyOuterattendanceList(@Body jsonObject: JsonObject?): Call<ResponseOuterAttendanceListing>


    @POST("activity/checklist_all")
    fun galaxyInnerattendanceList(@Body jsonObject: JsonObject?): Call<ResponseInnerAttendance>



    @POST("activity/galaxy_beatplan_all_filter")
    fun galaxyBeatPlanList(@Body jsonObject: JsonObject?): Call<ResponseBeatPlan>


    @POST("activity/galaxy_visit_beatplan_all_filter")
    fun galaxyVisitBeatPlanList(@Body jsonObject: JsonObject?): Call<ResponseBeatPlan>

    @POST("employee/galaxy_profile") //todo
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getNewAllAttachmentApi(@Body jsonObject: JsonObject?): Call<AttachmentModel>


    @POST("employee/galaxy_signout") //todo
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun logout(@Body jsonObject: JsonObject?): Call<ResponseGlobal>


    @POST("employee/galaxy_image_upload") //todo
    fun uploadProfilePic(@Body jsonObject: MultipartBody): Call<ResponseGlobal>

    @POST("activity/leave_all_filter")
    fun galaxyAllLeaveStatusList(@Body jsonObject: JsonObject?): Call<AllLeaveModel>


    @POST("activity/galaxy_all_attach_note")
    fun getAllNotes(@Body jsonObject: JsonObject?): Call<ResponseAllNotes>


    @POST("businesspartner/bp_overview")
    fun getBpOverView(@Body jsonObject: JsonObject?): Call<ResponseBpOverview>


    @POST("businesspartner/one")
    fun getBpOne(@Body jsonObject: JsonObject?): Call<ResponseCustomerOne>


    @POST("businesspartner/galaxy_one")
    fun getBpGalaxyOne(@Body jsonObject: JsonObject?): Call<ResponseCustomerOne>


    @POST("activity/leave_approval")
    fun galaxyApproveLeaveApi(@Body jsonObject: JsonObject): Call<ResponseGlobal>


    @POST("activity/galaxy_approval_beatplan")
    fun galaxyApproveBeatPlanApi(@Body jsonObject: JsonObject): Call<ResponseGlobal>

    @POST("expense/expense_approval")
    fun galaxyApproveExpenseApi(@Body jsonObject: JsonObject): Call<ResponseGlobal>




    @POST("activity/leave_request")
    fun galaxyNewLeaveRequest(@Body jsonObject: JsonObject?): Call<ResponseGlobal>

    @POST("activity/leave_request_update")
    fun galaxyUpdateLeaveRequest(@Body jsonObject: JsonObject?): Call<ResponseGlobal>

    @POST("activity/login_or_signup")
    fun loginSignUpApi(@Body data: HashMap<String, String>): Call<LoginSignUpModel>



    @POST("activity/verify_otp")
    fun verifyOTP(@Body data: HashMap<String, String>): Response<LoginSignUpModel>

    @POST("activity/beatplan_bp_dropdown")
    fun getCustomerListing(@Body data: JsonObject): Call<BeatPlanCustomerDropDownModel>
    @POST("states/all")
    fun getStateAll(@Body data: JsonObject): Call<ResponseStateAll>


    @POST("city/all_filter_page")
    fun getCityALL(@Body data: JsonObject): Call<ResponseCityAll>

    @GET("dropdown/all_transport_mode")
    fun getModeOfTravel(@Header("Authorization") token: String,): Call<ModeOfTravelResponse>


    @POST("businesspartner/galaxy_customer_all")
    fun getSecondaryCustomerListing(@Body data: JsonObject): Call<ResponseSecondaryCustomerList>

    @POST("activity/galaxy_create_beatplan")
    fun createBeatPlan(@Header("Authorization") token: String,@Body data: JsonArray): Call<BeatPlanCustomerDropDownModel>


    @POST("businesspartner/galaxy_customer_create")
    fun createSecondaryCustomer(@Body data: JsonObject): Call<ResponseGlobal>


    @POST("businesspartner/galaxy_customer_update")
    fun updateSecondaryCustomer(@Body data: JsonObject): Call<ResponseGlobal>


    @POST(AppConstants.SO_REQUEST_TODAY_VISIT_DASHBOARD)
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getTodayVisitDashboard(@Body jsonObject: JsonObject): Call<TodayVisitDashboardResponse>

    @GET("businesspartner/galaxy_bp_ba_list")
    fun getBpBaList(): Call<ModelBpListStatic>

    @GET("industries/all")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getIndustryList(@Header("Authorization") token: String): Call<IndustryResponse?>?

    @POST("employee/all_filter")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getSalesEmplyeeList(@Header("Authorization") token: String, @Body jsonObject: JsonObject): Call<JsonObject?>?

    @GET("businesspartner/alltype")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getBusinessType(@Header("Authorization") token: String): Call<ResponseBusinessType>

    @GET("paymenttermstypes/all")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getPaymentTerm(@Header("Authorization") token: String): Call<PayMentTermsDetail>

    @POST("dropdown/zone/all_filter_page")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getZoneDropDownApi(@Header("Authorization") token: String,
                           @Body performaInvoiceListRequestModel: PerformaInvoiceListRequestModel?): Call<ResponseZoneDropDown?>?

    @GET("countries/all")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getCountryList(): Call<CountryResponse?>?

    @POST("states/all")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getStateList(@Body stateData: StateData?): Call<StateRespose?>?

    @POST("city/all_filter_page")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getCityList(@Body stateData: FilterOverAll): Call<CityResponse>

    @POST("employee/galaxy_get_user_credential")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getGalaxyUserCredentials(@Body jsonObject: JsonObject): Call<JsonObject>

    @POST("api/user/login")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getLoginToken(@Body jsonObject: JsonObject): Call<JsonObject>


    @POST("businesspartner/all_filter_page")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getBPAllPageList(
        @Header("Authorization") token: String,
        @Body requestModel: JsonObject // Accepting JsonObject instead of BPAllFilterRequestModel
    ): Call<JsonObject?>? // Returning correct response type

    @POST("businesspartner/create")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun addNewCustomer(@Header("Authorization") token: String,
                       @Body jsonObject: JsonObject): Call<CustomerBusinessRes>


    @POST("activity/galaxy_edit_beatplan")
    fun updateBeatPlanCustomerTiming(@Body data: JsonObject): Call<ResponseGlobal>


    @GET("lead/source_all")
    fun getLeadSourceAll(@Header("Authorization") token: String): Call<LeadSourceAllResponseModel>

    @POST("lead/all_lead")
    fun getAllLead(@Header("Authorization") token: String, @Body jsonObject: JsonObject): Call<AllLeadResponse>

    @POST("businesspartner/target/fetch-targets")
    fun getBDRCData(@Header("Authorization") token: String, @Body jsonObject: JsonObject): Call<BdrcModel>

    @POST(AppConstants.DASHBOARD_TARGET)
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getDashBoardData(@Body jsonObject: JsonObject): Call<DashBoardCounterResponse>

    @GET("dynamic_field_list")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getDynamicFields(@Header("Authorization") token: String,@Query("module_name") moduleName: String?): Call<DynamicFieldResponse?>?


    @GET("lead/type_all")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getLeadType(@Header("Authorization") token: String): Call<LeadTypeResponse?>?

    @POST(AppConstants.SAVE_BDRC_DATA_API)
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun saveBDRCData(@Header("Authorization") token: String,@Body jsonObject: JsonObject): Call<BdrcModel>

    @POST("employee/all_filter")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getSalesEmplyeeDataList(@Header("Authorization") token: String,@Body employeeValue: SalesEmployeeItem?): Call<SaleEmployeeResponse?>?

    @POST("lead/create")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun createLead(@Header("Authorization") token: String,@Body jsonArray: JsonArray?): Call<LeadResponse?>?

    @GET("lead/source_all")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getsourceType(@Header("Authorization") token: String): Call<LeadTypeResponse?>?

    @POST("lead/one")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun particularlead(@Header("Authorization") token: String,@Body leadValue: JsonObject?): Call<LeadResponse?>?

    @POST("lead/one")
    fun particularLeadNew(@Header("Authorization") token: String,@Body params: MutableMap<String?, Any?>?): Call<ResponseBody?>?

    @GET("dynamic_field_list")
    fun getDynamicFieldList(@Header("Authorization") token: String,@Query("module_name") moduleName: String?): Call<DynamicFieldsListModelClass?>?

    @POST("lead/lead_attachments")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun particularleadattachment(@Header("Authorization") token: String,@Body leadValue: java.util.HashMap<String?, Int?>?): Call<LeadDocumentResponse?>?

    @POST("lead/update")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun updateLead(@Header("Authorization") token: String,@Body lv: JsonObject?): Call<GlobalResponse?>?

    @POST("lead/lead_attachment_delete")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun deleteLeadAttachment(@Body leadValue: JsonObject?): Call<LeadDocumentResponse?>?

    @POST("lead/lead_attachment_create")
    fun updateLeadattachment(@Header("Authorization") token: String,@Body requestBody: MultipartBody?): Call<LeadResponse?>?

    @POST("activity/chatter")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun createChat(@Body opportunityValue: ChatModel?): Call<ChatResponse?>?

    /*
    @POST("lead/chatter_all")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<ChatResponse> getAllLeadChat(@Body LeadChatModel opportunityValue);*/
    @POST("activity/chatter_all")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getAllLeadChat(@Header("Authorization") token: String,@Body opportunityValue: FollowUpData?): Call<ChatResponse?>?
}
