package com.preetTractor.galaxyAndroid.apiHelper

import com.preetTractor.galaxyAndroid.data.LoginSignUpModel
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.expense.newexpense.ResponseExpenseNew
import com.preetTractor.galaxyAndroid.data.expense.type.ExpenseTypeModel
import com.preetTractor.galaxyAndroid.data.team.ResponseTeamList
import com.google.gson.JsonObject
import com.product.connect.models.loginProcessModels.DistributorProfileModel
import com.product.connect.models.loginProcessModels.ProfileImageUploadModel
import com.preetTractor.galaxyAndroid.activity.signInScreen.model.ResponseTermsSignIn
import com.preetTractor.galaxyAndroid.data.FilterOverAll
import com.preetTractor.galaxyAndroid.data.FollowUpData
import com.preetTractor.galaxyAndroid.data.FollowUpResponse
import com.preetTractor.galaxyAndroid.data.LeadResponse
import com.preetTractor.galaxyAndroid.data.ResponseCategoryAllList
import com.preetTractor.galaxyAndroid.data.ResponseCategoryItemAllList
import com.preetTractor.galaxyAndroid.data.ba.ModelAllLogs
import com.preetTractor.galaxyAndroid.data.ba.ModelBaTargetSales
import com.preetTractor.galaxyAndroid.data.ba.ModelDashboardIncentive
import com.preetTractor.galaxyAndroid.data.ba.ModelTargetVsAchievedSales
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentItemListModel
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentListModel
import com.preetTractor.galaxyAndroid.moreUi.model.enquiryModel.EnquiryListModel
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseRecentSearchAndOrder
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSchemeList
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSubCategoryItem
import com.preetTractor.galaxyAndroid.data.beatplan.ResponseBeatPlan
import com.preetTractor.galaxyAndroid.data.model.order.model.apibody.ModelSoCreateRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.request.ModelBACreateOrderRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.request.ModelCreateOrderRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ModelOrderListing
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ResponseBpOne
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ba.ModelBusinessPartnerAll
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ba.ModelItemAllByCategory

import com.preetTractor.galaxyAndroid.orderUi.model.OrderOneDetailModel
import com.preetTractor.galaxyAndroid.orderUi.model.PendingByOrderModel
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseDispatchList
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseItemListFromSubCategory
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingDeliveryNote
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingOrderInner
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSoRequestAllFilter
import com.preetTractor.galaxyAndroid.searchUi.model.ResponseItemOne
import com.preetTractor.galaxyAndroid.searchUi.model.ResponseSearchItemDmsSuggestion
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApisInterface {

    @POST(AppConstants.LOGIN_SIGNUP)
    fun loginSignUpApi(@Body data: HashMap<String, String>): Call<LoginSignUpModel>

    @POST(AppConstants.LOGIN_WITH_SAME_MOBILE)
    fun loginWithSameNumber(@Body data: HashMap<String, String>): Call<LoginSignUpModel>

    @POST(AppConstants.VERIFY_OTP)
    suspend fun verifyOTP(@Body data: HashMap<String, String>): Response<LoginSignUpModel>

    @POST(AppConstants.LOGOUT)
    suspend fun logoutApi(@Body data: JsonObject): Response<LoginSignUpModel>

    @POST(AppConstants.LOGOUT)
    fun callLogoutApi(@Body data: JsonObject): Call<LoginSignUpModel>


    @GET(AppConstants.DMS_SETTING_TERMS_SIGN_IN_API)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getTermsSignIN(): Response<ResponseTermsSignIn>


    @POST(AppConstants.BEAT_PLAN_BP_LISTING)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getBPListApi(@Body jsonObject: JsonObject): Response<ResponseBeatPlan>

    @POST(AppConstants.BEAT_PLAN_LISTING)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getBeatPlanAllItemListApi(@Body jsonObject: JsonObject): Response<ResponseBeatPlan>

    @POST(AppConstants.BEAT_PLAN_LISTING_CUSTOMER_ALL)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getBeatPlanCustomerAllList(@Body jsonObject: JsonObject): Response<ResponseBeatPlan>

    @GET(AppConstants.SCHEME_LIST_ALL_FILTER)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getSchemeListALlFilter(): Response<ResponseSchemeList>


//    @GET(AppConstants.ITEM_ALL_CATEGORY_LIST)
//    @Headers("Content-Type: application/json;charset=UTF-8")
//    suspend fun getItemAllCategoryListALlFilter(): Response<ResponseItemAllCategoryList>

    @GET(AppConstants.ITEM_ALL_CATEGORY_LIST)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getItemAllCategoryListALlFilter(@Header("Authorization") token: String): Response<ResponseCategoryAllList>

    @POST(AppConstants.ITEM_ALL_SUB_CATEGORY_LIST)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getItemAllSubCategoryListALlFilter(@Body jsonObject: JsonObject): Response<ResponseSubCategoryItem>

    @POST(AppConstants.CREATE_ENQUIRY)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun createEnquiryApi(@Body data: JsonObject): Response<LoginSignUpModel>

    @POST(AppConstants.ENQUIRY_ALL_FILTER_LIST)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getEnquiryListApi(@Body jsonObject: JsonObject): Response<EnquiryListModel>

    @GET(AppConstants.DOCUMENT_ALL)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getDocumentAllApi(): Response<DocumentListModel>

    @POST(AppConstants.DOCUMENT_ALL_ITEM)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getDocumentAllItemListApi(@Body jsonObject: JsonObject): Response<DocumentItemListModel>

    @POST(AppConstants.SCHEME_DOCUMENT_ALL_ITEM)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getSchemeDocumentAllItemListApi(@Body jsonObject: JsonObject): Response<DocumentItemListModel>

    @POST(AppConstants.EXPENSE_CREATE)
    suspend fun createExpenseApi(@Body jsonObject: JsonObject): Response<LoginSignUpModel>


    @POST(AppConstants.EXPENSE_CREATE)
    fun createExpenseApiMultipart(@Body jsonObject: MultipartBody): Call<ResponseGlobal>

    @POST(AppConstants.EXPENSE_UPDATE)
    fun updateExpenseApiMultipart(@Body jsonObject: MultipartBody): Call<ResponseGlobal>


    @POST(AppConstants.EXPENSE_ONE)
    fun getExpenseOne(@Body jsonObject: JsonObject): Call<ResponseExpenseNew>

    @POST(AppConstants.ALL_EXPENSE_LIST)
    suspend fun getAllExpenseListApi(@Body jsonObject: JsonObject): Response<ResponseExpenseNew>


    @POST(AppConstants.EMPLOYEE_USER_REPORTING_TO)
    suspend fun getListingOfTeamUser(@Body jsonObject: JsonObject): Response<ResponseTeamList>

    @POST(AppConstants.EMPLOYEE_USER_REPORTING_TO_EXPENDABLE)
    suspend fun getListingOfTeamUserExpandable(@Body jsonObject: JsonObject): Response<ResponseTeamList>

    //todo searchItem
    @POST(AppConstants.ITEM_DMS_SEARCH_SUGGESTION)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun searchItemInDMS(@Header("Authorization") token: String, @Body jsonObject: JsonObject): Response<ResponseSearchItemDmsSuggestion>

    //todo itemByScheme
    @POST(AppConstants.ITEM_DMS_SCHEME_ITEM_LIST)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun schemeItemInDMS( @Body jsonObject: JsonObject): Response<ResponseItemListFromSubCategory>

    @GET(AppConstants.TYPE_OF_EXPENSE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getExpenseListApi(): Response<ExpenseTypeModel>

    //todo recentOrderAndSearches
    @POST(AppConstants.ITEM_DMS_LAST_ORDER_ITEM)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun recentSearchesAndOrders(@Body jsonObject: JsonObject): Response<ResponseRecentSearchAndOrder>

    @POST(AppConstants.DISTRIBUTOR_PROFILE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun distributorProfile(@Body data: HashMap<String, String>): Response<DistributorProfileModel>

    @POST(AppConstants.DISTRIBUTOR_IMAGE_UPLOAD)
    suspend fun distributorImageProfile(@Body data: MultipartBody): Response<ProfileImageUploadModel>

    //todo create so request
    @POST(AppConstants.SO_REQUEST_CREATE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun createSoRequest(@Header("Authorization") token: String, @Body jsonObject: ModelCreateOrderRequest): Response<ResponseGlobal>

    @POST(AppConstants.SO_REQUEST_CREATE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun createBAOrderRequest(@Body jsonObject: ModelBACreateOrderRequest): Response<ResponseGlobal>

    //todo update so request
    @POST(AppConstants.SO_REQUEST_SO_UPDATE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun updateSoRequest(@Body jsonObject: ModelSoCreateRequest): Response<ResponseGlobal>

    @POST(AppConstants.SO_REQUEST_ONE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun requestOrderOneApi(@Body jsonObject: JsonObject): Response<OrderOneDetailModel>

    /* todo searchItem */
    @POST(AppConstants.ITEM_ONE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getItemOne(@Body jsonObject: JsonObject): Response<ResponseItemOne>

    @POST(AppConstants.ITEM_ALL_ITEM_LIST)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getAllItemListFromSubCategoryOrderRequest(@Header("Authorization") token: String,@Body jsonObject: JsonObject): ResponseCategoryItemAllList

    @POST(AppConstants.DISPATCH_LIST)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getDispatchList(@Body jsonObject: JsonObject): ResponseDispatchList

    @POST(AppConstants.DELIVERYNOTE_DMS_PENDING_ORDER_ITEM_ALL_API)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getDeliveryNotePendingOrderItemAll(@Body jsonObject: JsonObject): ResponsePendingDeliveryNote

    @POST(AppConstants.PENDING_BYORDER)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun pendingByOrder(@Body jsonObject: JsonObject): Response<PendingByOrderModel>

    @POST(AppConstants.DELIVERYNOTE_DMS_PENDING_ORDER_WISE_API)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getDeliveryNotePendingOrderWiseInner(@Body jsonObject: JsonObject): ResponsePendingOrderInner

    @POST(AppConstants.ORDER_ONE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun orderOneApi(@Body jsonObject: JsonObject): Response<OrderOneDetailModel>

    @POST(AppConstants.BUSINESS_PARTNER_GALAXY_ONE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun bpOne(@Body jsonObject: JsonObject): Response<ResponseBpOne>

    @POST(AppConstants.SO_REQUEST_SO_DELETE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun requestOrderDeleteApi(@Body jsonObject: JsonObject): Response<ResponseGlobal>

    @POST(AppConstants.SO_REQUEST_ALL_FILTER)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getSoRequestAllFilter(@Body jsonObject: JsonObject): ResponseSoRequestAllFilter

    @POST(AppConstants.BA_DASHBOARD_LOGS)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getDashboardLogs(@Body jsonObject: JsonObject): Response<ModelAllLogs>

    @POST(AppConstants.SO_REQUEST_LIST_ALL_FILTER_PAGE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getOrderListingAllFilter(@Body jsonObject: JsonObject): ModelOrderListing

    @POST(AppConstants.BA_ORDER_LIST_ALL_FILTER_PAGE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getBAOrderListingAllFilter(@Body jsonObject: JsonObject): ModelOrderListing

    @POST(AppConstants.SO_REQUEST_LIST_ALL_FILTER_PAGE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getOrderListingAllFilterPage(@Body jsonObject: JsonObject): Response<ModelOrderListing>

    @POST(AppConstants.SO_REQUEST_LIST_DISPATCH_ORDER)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getOrderDispatchListingAllFilterPage(@Body jsonObject: JsonObject): Response<ModelOrderListing>

    @POST(AppConstants.BA_BP_ALL_FILTER)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getBpListALlFilter(@Body jsonObject: JsonObject): Response<ModelBusinessPartnerAll>

    @POST(AppConstants.ITEM_ALL_ITEM_LIST)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getAllItemListByCategory(@Header("Authorization") token: String, @Body jsonObject: JsonObject): Response<ModelItemAllByCategory>

    @POST(AppConstants.BA_DASHBOARD_TARGET_ASSIGNED_VS_ACHIEVED)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getDashboardTargetVsAchieved(@Body jsonObject: JsonObject): Response<ModelTargetVsAchievedSales>

    @POST(AppConstants.BA_DASHBOARD_TOTAL_SALES)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getDashboardTotalSales(@Body jsonObject: JsonObject): Response<ModelBaTargetSales>

    @POST(AppConstants.BA_DASHBOARD_INCENTIVE)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getDashboardIncentive(@Body jsonObject: JsonObject): Response<ModelDashboardIncentive>

    @POST(AppConstants.FOLLOW_UP_API)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun callFollowUpAPi(@Body jsonObject: FollowUpData): Response<FollowUpResponse>

    @POST(AppConstants.LEAD_ALl_API)
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getAllLead(@Body jsonObject: FilterOverAll): Response<LeadResponse>



}