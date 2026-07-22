package com.preetTractor.galaxyAndroid.mvvmSetUp

import android.content.Context
import com.google.gson.JsonObject

import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.data.FilterOverAll
import com.preetTractor.galaxyAndroid.data.FollowUpData
import com.preetTractor.galaxyAndroid.data.FollowUpResponse
import com.preetTractor.galaxyAndroid.data.LeadValue
import com.preetTractor.galaxyAndroid.data.ResponseCategoryItemAllList

import com.preetTractor.galaxyAndroid.data.beatplan.ResponseBeatPlan
import com.preetTractor.galaxyAndroid.data.model.order.model.apibody.ModelSoCreateRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.request.ModelBACreateOrderRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.request.ModelCreateOrderRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ModelOrderListing
import com.preetTractor.galaxyAndroid.data.team.ResponseTeamList
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.orderUi.model.response.DataSoRequestAllFilter
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseDispatchList
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingDeliveryNote
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingOrderInner
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseRecentSearchAndOrder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class DefaultMainRepositories : MainRepos {

    val token = "Token ${Globals.GalaxyVistaToken}"

    override suspend fun verifyOTP(data: java.util.HashMap<String, String>, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).verifyOTP(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun distributorProfile(
        data: java.util.HashMap<String, String>,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).distributorProfile(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun distributorImageProfile(data: MultipartBody, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).distributorImageProfile(data)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun logoutApi(data: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).logoutApi(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun createEnquiryApi(data: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).createEnquiryApi(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getEnquiryListApi(data: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getEnquiryListApi(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getExpenseListApi(context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getExpenseListApi()
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun createExpenseListApi(
        data: JsonObject,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).createExpenseApi(data)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getDocumentAllApi(context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getDocumentAllApi()
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getDocumentAllItemListApi(data: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getDocumentAllItemListApi(data)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getSchemeDocumentAllItemListApi(data: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getSchemeDocumentAllItemListApi(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getTermsSignIn(context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getTermsSignIN()
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getBPListApi(
        data: JsonObject,
        context: Context
    ): Resource<ResponseBeatPlan> =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getBPListApi(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getBeatPlanAllItemListApi(
        data: JsonObject,
        context: Context
    ): Resource<ResponseBeatPlan> =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getBeatPlanAllItemListApi(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getBeatPlanCustomerAllList(
        data: JsonObject,
        context: Context
    ): Resource<ResponseBeatPlan> =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getBeatPlanCustomerAllList(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun recentSearchesAndOrders(
        jsonObject: JsonObject,
        context: Context
    ): Resource<ResponseRecentSearchAndOrder> =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).recentSearchesAndOrders(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getSchemeListALlFilter(jsonObject: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getSchemeListALlFilter()
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getItemAllCategoryListALlFilter(context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getItemAllCategoryListALlFilter(token)
                Resource.Success(response.body()!!)
            }
        }

override suspend fun getBpListALlFilter(jsonObject: JsonObject,context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getBpListALlFilter(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

override suspend fun getAllItemListByCategory(jsonObject: JsonObject,context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getAllItemListByCategory(token, jsonObject)
                Resource.Success(response.body()!!)
            }
        }
override suspend fun getDashboardTargetVsAchieved(jsonObject: JsonObject,context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getDashboardTargetVsAchieved(jsonObject)
                Resource.Success(response.body()!!)
            }
        }
override suspend fun getDashboardTotalSales(jsonObject: JsonObject,context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getDashboardTotalSales(jsonObject)
                Resource.Success(response.body()!!)
            }
        }
override suspend fun getDashboardIncentive(jsonObject: JsonObject,context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getDashboardIncentive(jsonObject)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getItemAllSubCategoryListALlFilter(
        jsonObject: JsonObject,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response =
                    ApiClient().service(context).getItemAllSubCategoryListALlFilter(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getAllItemListFromSubCategoryOrderRequest(
        jsonObject: JsonObject,
        context: Context
    ): List<ResponseCategoryItemAllList.Data> {

        val response =
            ApiClient().service(context).getAllItemListFromSubCategoryOrderRequest(token,jsonObject)
        return response.data

    }

    override suspend fun getItemAllExpenseListALlFilter(
        jsonObject: JsonObject,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response =
                    ApiClient().service(context).getAllExpenseListApi(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getListingOfTeamUser(
        jsonObject: JsonObject,
        context: Context
    ): Resource<ResponseTeamList> =
        withContext(Dispatchers.IO) {
            safeCall {
                val response =
                    ApiClient().service(context).getListingOfTeamUser(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getListingOfTeamUserExpandable(
        jsonObject: JsonObject,
        context: Context
    ): Resource<ResponseTeamList> =
        withContext(Dispatchers.IO) {
            safeCall {
                val response =
                    ApiClient().service(context).getListingOfTeamUserExpandable(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun searchItemInDMS(
        jsonObject: JsonObject,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).searchItemInDMS(token, jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun schemeItemInDMS(
        jsonObject: JsonObject,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).schemeItemInDMS(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getItemOne(
        jsonObject: JsonObject,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getItemOne(jsonObject)
                Resource.Success(response.body()!!)
            }
        }



    override suspend fun createSoRequest(
        jsonObject: ModelCreateOrderRequest,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).createSoRequest(token, jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun createBAOrderRequest(
        jsonObject: ModelBACreateOrderRequest,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).createBAOrderRequest(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun requestOrderOneApi(jsonObject: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).requestOrderOneApi(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    /*override suspend fun updateSoRequest(
        jsonObject: RequestBodyForSoRequestCreate,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).updateSoRequest(jsonObject)
                Resource.Success(response.body()!!)
            }
        }*/

    override suspend fun updateSoRequest(
        jsonObject: ModelSoCreateRequest,
        context: Context
    ) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).updateSoRequest(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getDispatchList(
        jsonObject: JsonObject,
        context: Context
    ): List<ResponseDispatchList.Data> {

        val response = ApiClient().service(context).getDispatchList(jsonObject)
        return response.data

    }

    override suspend fun getDeliveryNotePendingOrderItemAll(
        jsonObject: JsonObject,
        context: Context
    ): List<ResponsePendingDeliveryNote.Data> {

        val response = ApiClient().service(context).getDeliveryNotePendingOrderItemAll(jsonObject)
        return response.data

    }

    override suspend fun pendingByOrder(jsonObject: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).pendingByOrder(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getDeliveryNotePendingOrderWiseInner(
        jsonObject: JsonObject,
        context: Context
    ): List<ResponsePendingOrderInner.Data> {

        val response = ApiClient().service(context).getDeliveryNotePendingOrderWiseInner(jsonObject)
        return response.data

    }

    override suspend fun orderOneApi(jsonObject: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).orderOneApi(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun bPOneApi(jsonObject: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).bpOne(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun requestOrderDeleteApi(jsonObject: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).requestOrderDeleteApi(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getSoRequestAllFilter(
        jsonObject: JsonObject,
        context: Context
    ): List<DataSoRequestAllFilter> {

        val response =
            ApiClient().service(context).getSoRequestAllFilter(jsonObject)
        return response.data

    }

    override suspend fun getDashboardLogs(jsonObject: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getDashboardLogs(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getOrderListingAllFilterPage(jsonObject: JsonObject, context: Context) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service(context).getOrderListingAllFilterPage(jsonObject)
                Resource.Success(response.body()!!)
            }
        }




    override suspend fun getOrderListingAllFilter(
        jsonObject: JsonObject,
        context: Context
    ): List<ModelOrderListing.Data> {

        val response =
            ApiClient().service(context).getOrderListingAllFilter(jsonObject)
        return response.data

    }

    override suspend fun getBAOrderListingAllFilter(
        jsonObject: JsonObject,
        context: Context
    ): List<ModelOrderListing.Data> {

        val response =
            ApiClient().service(context).getBAOrderListingAllFilter(jsonObject)
        return response.data

    }

    override suspend fun getOrderDispatchListingAllFilter(
        jsonObject: JsonObject,
        context: Context
    ): List<ModelOrderListing.Data> {

        val response =
            ApiClient().service(context).getOrderDispatchListingAllFilterPage(jsonObject)
        return response.body()!!.data

    }

    override suspend fun callFollowUpApirepos(context: Context, followUpData: FollowUpData): FollowUpResponse {
        val response =
            ApiClient().service(context).callFollowUpAPi(followUpData)
        return response.body()!!
    }



    override suspend fun getAllLead(
        jsonObject: FilterOverAll,
        context: Context
    ): Resource<List<LeadValue>> {

        return try {

            val response = ApiClient()
                .service(context)
                .getAllLead(jsonObject)

            if (response.isSuccessful) {

                val body = response.body()

                if (body?.data != null) {

                    if (body.data.isNotEmpty()) {
                        Resource.Success(body.data)
                    } else {
                        Resource.Error("No leads found")
                    }

                } else {
                    Resource.Error("Empty response")
                }

            } else {

                Resource.Error(
                    response.message().ifEmpty {
                        "Something went wrong"
                    }
                )
            }

        } catch (e: Exception) {

            Resource.Error(
                e.localizedMessage ?: "Network Error"
            )
        }
    }

}