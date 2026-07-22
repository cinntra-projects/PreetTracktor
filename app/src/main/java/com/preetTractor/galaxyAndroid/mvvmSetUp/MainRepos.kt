package com.preetTractor.galaxyAndroid.mvvmSetUp

import android.content.Context
import com.google.gson.JsonObject
import com.product.connect.models.loginProcessModels.DistributorProfileModel
import com.product.connect.models.loginProcessModels.ProfileImageUploadModel

import com.preetTractor.galaxyAndroid.data.beatplan.ResponseBeatPlan
import com.preetTractor.galaxyAndroid.data.expense.newexpense.ResponseExpenseNew
import com.preetTractor.galaxyAndroid.data.expense.type.ExpenseTypeModel

import com.preetTractor.galaxyAndroid.data.team.ResponseTeamList

import com.preetTractor.galaxyAndroid.orderUi.model.OrderOneDetailModel
import com.preetTractor.galaxyAndroid.orderUi.model.PendingByOrderModel
import com.preetTractor.galaxyAndroid.orderUi.model.response.DataSoRequestAllFilter
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseDispatchList
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseItemListFromSubCategory
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingDeliveryNote
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingOrderInner
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseRecentSearchAndOrder
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSchemeList
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSubCategoryItem
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient


import com.preetTractor.galaxyAndroid.activity.signInScreen.model.ResponseTermsSignIn
import com.preetTractor.galaxyAndroid.data.*
import com.preetTractor.galaxyAndroid.data.ba.ModelAllLogs
import com.preetTractor.galaxyAndroid.data.ba.ModelBaTargetSales
import com.preetTractor.galaxyAndroid.data.ba.ModelDashboardIncentive
import com.preetTractor.galaxyAndroid.data.ba.ModelTargetVsAchievedSales
import com.preetTractor.galaxyAndroid.data.model.order.model.apibody.ModelSoCreateRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.request.ModelBACreateOrderRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.request.ModelCreateOrderRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ModelOrderListing
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ResponseBpOne
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ba.ModelBusinessPartnerAll
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ba.ModelItemAllByCategory
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentItemListModel
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentListModel
import com.preetTractor.galaxyAndroid.moreUi.model.enquiryModel.EnquiryListModel

import com.preetTractor.galaxyAndroid.searchUi.model.ResponseItemOne
import com.preetTractor.galaxyAndroid.searchUi.model.ResponseSearchItemDmsSuggestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody

interface MainRepos {

    suspend fun verifyOTP(data: HashMap<String, String>, context: Context): Resource<LoginSignUpModel>
    suspend fun distributorProfile(data: HashMap<String, String>, context : Context): Resource<DistributorProfileModel>

    suspend fun distributorImageProfile(data: MultipartBody, context : Context): Resource<ProfileImageUploadModel>

    suspend fun logoutApi(data: JsonObject, context: Context): Resource<LoginSignUpModel>


    suspend fun getTermsSignIn(context: Context): Resource<ResponseTermsSignIn>

    suspend fun getSchemeListALlFilter(
        jsonObject: JsonObject,
        context: Context
    ): Resource<ResponseSchemeList>

    fun fetchData(): Flow<com.preetTractor.galaxyAndroid.retrofit.Resource<List<ResponseJsonDataItem>>> =
        flow {
            emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Loading())
            try {
                val response = RetrofitClient.apiService.getData()
                if (response.isSuccessful) {
                    emit(
                        com.preetTractor.galaxyAndroid.retrofit.Resource.Success(
                            response.body() ?: emptyList()
                        )
                    )
                } else {
                    emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Error("Failed to fetch data"))
                }
            } catch (e: Exception) {
                emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Error("An error occurred"))
            }
        }


    fun sec(): Flow<com.preetTractor.galaxyAndroid.retrofit.Resource<ResponseJsonDataItem>> = flow {
        emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Loading())
        try {
            val response = RetrofitClient.apiService.getDataResponse()
            if (response.isSuccessful) {
                emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Success(response.body()!!))
            } else {
                emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Error("Failed to fetch data"))
            }
        } catch (e: Exception) {
            emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Error("An error occurred"))
        }
    }

    suspend fun getItemAllCategoryListALlFilter(context: Context): Resource<ResponseCategoryAllList>

    suspend fun getBpListALlFilter(jsonObject: JsonObject,context: Context): Resource<ModelBusinessPartnerAll>

    suspend fun getAllItemListByCategory(jsonObject: JsonObject,context: Context): Resource<ModelItemAllByCategory>

    suspend fun getDashboardTargetVsAchieved(jsonObject: JsonObject,context: Context): Resource<ModelTargetVsAchievedSales>
    suspend fun getDashboardTotalSales(jsonObject: JsonObject,context: Context): Resource<ModelBaTargetSales>
    suspend fun getDashboardIncentive(jsonObject: JsonObject,context: Context): Resource<ModelDashboardIncentive>

    suspend fun getItemAllSubCategoryListALlFilter(jsonObject: JsonObject, context: Context): Resource<ResponseSubCategoryItem>

    suspend fun getAllItemListFromSubCategoryOrderRequest(jsonObject: JsonObject, context: Context): List<ResponseCategoryItemAllList.Data>


    suspend fun getItemAllExpenseListALlFilter(jsonObject: JsonObject, context: Context): Resource<ResponseExpenseNew>

    suspend fun getListingOfTeamUser(jsonObject: JsonObject, context: Context): Resource<ResponseTeamList>

    suspend fun getListingOfTeamUserExpandable(jsonObject: JsonObject, context: Context): Resource<ResponseTeamList>

    suspend fun createEnquiryApi(data: JsonObject, context: Context): Resource<LoginSignUpModel>

    suspend fun getEnquiryListApi(data: JsonObject, context: Context): Resource<EnquiryListModel>

    suspend fun getExpenseListApi(context: Context): Resource<ExpenseTypeModel>
    suspend fun createExpenseListApi(data: JsonObject, context: Context): Resource<LoginSignUpModel>

    suspend fun getDocumentAllApi(context: Context): Resource<DocumentListModel>

    suspend fun getDocumentAllItemListApi(data: JsonObject, context: Context): Resource<DocumentItemListModel>

    suspend fun getSchemeDocumentAllItemListApi(data: JsonObject, context: Context): Resource<DocumentItemListModel>

    suspend fun getBPListApi(data: JsonObject, context: Context): Resource<ResponseBeatPlan>

    suspend fun getBeatPlanAllItemListApi(data: JsonObject, context: Context): Resource<ResponseBeatPlan>
    suspend fun getBeatPlanCustomerAllList(data: JsonObject, context: Context): Resource<ResponseBeatPlan>
    suspend fun recentSearchesAndOrders(jsonObject: JsonObject, context: Context): Resource<ResponseRecentSearchAndOrder>

    suspend fun searchItemInDMS(jsonObject: JsonObject, context: Context): Resource<ResponseSearchItemDmsSuggestion>
    suspend fun schemeItemInDMS(jsonObject: JsonObject, context: Context): Resource<ResponseItemListFromSubCategory>
    suspend fun getItemOne(jsonObject: JsonObject, context: Context): Resource<ResponseItemOne>
    //suspend fun createSoRequest(jsonObject: RequestBodyForSoRequestCreate, context : Context): Resource<ResponseGlobal>
//    suspend fun createSoRequest(jsonObject: ModelSoCreateRequest, context : Context): Resource<ResponseGlobal>
    suspend fun createSoRequest(jsonObject: ModelCreateOrderRequest, context : Context): Resource<ResponseGlobal>
    suspend fun createBAOrderRequest(jsonObject: ModelBACreateOrderRequest, context : Context): Resource<ResponseGlobal>
    suspend fun requestOrderOneApi(jsonObject: JsonObject, context : Context): Resource<OrderOneDetailModel>
    //suspend fun updateSoRequest(jsonObject: RequestBodyForSoRequestCreate, context : Context): Resource<ResponseGlobal>
    suspend fun updateSoRequest(jsonObject: ModelSoCreateRequest, context : Context): Resource<ResponseGlobal>
    suspend fun getDispatchList(jsonObject: JsonObject, context : Context): List<ResponseDispatchList.Data>
    suspend fun getDeliveryNotePendingOrderItemAll(jsonObject: JsonObject, context : Context): List<ResponsePendingDeliveryNote.Data>
    suspend fun pendingByOrder(jsonObject: JsonObject, context : Context): Resource<PendingByOrderModel>
    suspend fun getDeliveryNotePendingOrderWiseInner(jsonObject: JsonObject, context : Context): List<ResponsePendingOrderInner.Data>
    suspend fun orderOneApi(jsonObject: JsonObject, context : Context): Resource<OrderOneDetailModel>
    suspend fun bPOneApi(jsonObject: JsonObject, context : Context): Resource<ResponseBpOne>
    suspend fun requestOrderDeleteApi(jsonObject: JsonObject, context : Context): Resource<ResponseGlobal>
    suspend fun getSoRequestAllFilter(jsonObject: JsonObject, context : Context): List<DataSoRequestAllFilter>
    suspend fun getOrderListingAllFilter(jsonObject: JsonObject, context : Context): List<ModelOrderListing.Data>
    suspend fun getBAOrderListingAllFilter(jsonObject: JsonObject, context : Context): List<ModelOrderListing.Data>
    suspend fun getOrderDispatchListingAllFilter(jsonObject: JsonObject, context : Context): List<ModelOrderListing.Data>
    suspend fun getDashboardLogs(jsonObject: JsonObject, context : Context): Resource<ModelAllLogs>
    suspend fun getOrderListingAllFilterPage(jsonObject: JsonObject, context : Context): Resource<ModelOrderListing>
    suspend fun callFollowUpApirepos(context : Context,followUpData: FollowUpData): FollowUpResponse
    suspend fun getAllLead(
        jsonObject: FilterOverAll,
        context: Context
    ): Resource<List<LeadValue>>



}