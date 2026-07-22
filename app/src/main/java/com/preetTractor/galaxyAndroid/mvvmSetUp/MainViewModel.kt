package com.preetTractor.galaxyAndroid.mvvmSetUp

import Event
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.preetTractor.galaxyAndroid.data.expense.newexpense.ResponseExpenseNew
import com.preetTractor.galaxyAndroid.data.expense.type.ExpenseTypeModel
import com.preetTractor.galaxyAndroid.data.team.ResponseTeamList
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.google.gson.JsonObject
import com.product.connect.models.loginProcessModels.DistributorProfileModel
import com.product.connect.models.loginProcessModels.ProfileImageUploadModel
import com.preetTractor.galaxyAndroid.activity.signInScreen.model.ResponseTermsSignIn
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.data.*
import com.preetTractor.galaxyAndroid.data.ba.ModelAllLogs
import com.preetTractor.galaxyAndroid.data.ba.ModelBaTargetSales
import com.preetTractor.galaxyAndroid.data.ba.ModelDashboardIncentive
import com.preetTractor.galaxyAndroid.data.ba.ModelTargetVsAchievedSales
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentItemListModel
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentListModel
import com.preetTractor.galaxyAndroid.moreUi.model.enquiryModel.EnquiryListModel
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseRecentSearchAndOrder
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSchemeList
import com.preetTractor.galaxyAndroid.searchUi.model.ResponseSearchItemDmsSuggestion
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
import com.preetTractor.galaxyAndroid.orderUi.model.response.DataSoRequestAllFilter
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseDispatchList
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseItemListFromSubCategory
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingDeliveryNote
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingOrderInner
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSubCategoryItem
import com.preetTractor.galaxyAndroid.searchUi.model.ResponseItemOne

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MainViewModel(
    val app: Application,
    private val repos: MainRepos,
    private val dispatchers: CoroutineDispatcher = Dispatchers.Main,
    val fanxApi: ApisInterface
) : AndroidViewModel(app) {

    //todo dateselector with viewhost
    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate
    val apiData: Flow<com.preetTractor.galaxyAndroid.retrofit.Resource<List<ResponseJsonDataItem>>> = repos.fetchData()

    val apiresponse: Flow<com.preetTractor.galaxyAndroid.retrofit.Resource<ResponseJsonDataItem>> =repos.sec()
    fun setDate(date: String) {
        _selectedDate.value = date
    }
    fun fetchData(): Flow<com.preetTractor.galaxyAndroid.retrofit.Resource<List<ResponseJsonDataItem>>> = flow {
        emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Loading())
        try {
            val response = RetrofitClient.apiService.getData()
            if (response.isSuccessful) {
                emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Success(response.body() ?: emptyList()))
            } else {
                emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Error("Failed to fetch data"))
            }
        } catch (e: Exception) {
            emit(com.preetTractor.galaxyAndroid.retrofit.Resource.Error("An error occurred"))
        }
    }


    fun sec():Flow<com.preetTractor.galaxyAndroid.retrofit.Resource<ResponseJsonDataItem>> = flow {
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
    val _termsSignIn = MutableLiveData<Event<Resource<ResponseTermsSignIn>>>()
    val termsSignIn: LiveData<Event<Resource<ResponseTermsSignIn>>> = _termsSignIn
    //todo dataStatus with viewhost
    private val _selectedStatus = MutableLiveData<String>()
    val selectedStatus: LiveData<String> get() = _selectedStatus

    fun setStatus(date: String) {
        _selectedStatus.value = date
    }


    /****Live Data get****/
    private val _loginSignUpData = MutableLiveData<Event<Resource<LoginSignUpModel>>>()
    val loginSignUpData: LiveData<Event<Resource<LoginSignUpModel>>> = _loginSignUpData

    private val _expenseTypeData = MutableLiveData<Event<Resource<ExpenseTypeModel>>>()
    val expenseTypeData: LiveData<Event<Resource<ExpenseTypeModel>>> = _expenseTypeData
    private val _allExpenseListData = MutableLiveData<Event<Resource<ResponseExpenseNew>>>()
    val allExpenseListData: LiveData<Event<Resource<ResponseExpenseNew>>> = _allExpenseListData


    private val _listingGetTeamUser = MutableLiveData<Event<Resource<ResponseTeamList>>>()
    val listingGetTeamUser: LiveData<Event<Resource<ResponseTeamList>>> = _listingGetTeamUser

    private val _createExpenseData = MutableLiveData<Event<Resource<LoginSignUpModel>>>()
    val createExpenseTypeData: LiveData<Event<Resource<LoginSignUpModel>>> = _createExpenseData

    private val _enquiryAllListData = MutableLiveData<Event<Resource<EnquiryListModel>>>()
    val enquiryAllListData: LiveData<Event<Resource<EnquiryListModel>>> = _enquiryAllListData

    private val _documentALlList = MutableLiveData<Event<Resource<DocumentListModel>>>()
    val documentALlList: LiveData<Event<Resource<DocumentListModel>>> = _documentALlList

    private val _documentItemAllList = MutableLiveData<Event<Resource<DocumentItemListModel>>>()
    val documentItemAllList: LiveData<Event<Resource<DocumentItemListModel>>> = _documentItemAllList


    private val _documentSchemeItemAllList = MutableLiveData<Event<Resource<DocumentItemListModel>>>()
    val documentSchemeItemAllList: LiveData<Event<Resource<DocumentItemListModel>>> = _documentSchemeItemAllList

    private val _beatPlanItemAllList = MutableLiveData<Event<Resource<ResponseBeatPlan>>>()
    val beatPlanItemAllList: LiveData<Event<Resource<ResponseBeatPlan>>> = _beatPlanItemAllList

    private val _beatPlanItemCustomerAllList = MutableLiveData<Event<Resource<ResponseBeatPlan>>>()
    val beatPlanItemCustomerAllList: LiveData<Event<Resource<ResponseBeatPlan>>> = _beatPlanItemCustomerAllList

    private val _beatPlanBPList = MutableLiveData<Event<Resource<ResponseBeatPlan>>>()
    val beatPlanBPList: LiveData<Event<Resource<ResponseBeatPlan>>> = _beatPlanBPList

    val _itemCategoryListAllFilter = MutableLiveData<Event<Resource<ResponseCategoryAllList>>>()
    val itemCategoryListAllFilter: LiveData<Event<Resource<ResponseCategoryAllList>>> = _itemCategoryListAllFilter

    val _bpListAllFilter = MutableLiveData<Event<Resource<ModelBusinessPartnerAll>>>()
    val bpListAllFilter: LiveData<Event<Resource<ModelBusinessPartnerAll>>> = _bpListAllFilter

    val _allItemByCategory = MutableLiveData<Event<Resource<ModelItemAllByCategory>>>()
    val allItemByCategory: LiveData<Event<Resource<ModelItemAllByCategory>>> = _allItemByCategory

    val _targetVsAchievedSales = MutableLiveData<Event<Resource<ModelTargetVsAchievedSales>>>()
    val targetVsAchievedSales: LiveData<Event<Resource<ModelTargetVsAchievedSales>>> = _targetVsAchievedSales

    val _totalTargetSales = MutableLiveData<Event<Resource<ModelBaTargetSales>>>()
    val totalTargetSales: LiveData<Event<Resource<ModelBaTargetSales>>> = _totalTargetSales

    val _totalIncentives = MutableLiveData<Event<Resource<ModelDashboardIncentive>>>()
    val totalIncentives: LiveData<Event<Resource<ModelDashboardIncentive>>> = _totalIncentives

    val _schemeListAllFilter = MutableLiveData<Event<Resource<ResponseSchemeList>>>()
    val schemeListAllFilter: LiveData<Event<Resource<ResponseSchemeList>>> = _schemeListAllFilter

    val _recentSearchAndOrder = MutableLiveData<Event<Resource<ResponseRecentSearchAndOrder>>>()
    val recentSearchAndOrder: LiveData<Event<Resource<ResponseRecentSearchAndOrder>>> = _recentSearchAndOrder

    val _itemSearcSugggestion = MutableLiveData<Event<Resource<ResponseSearchItemDmsSuggestion>>>()
    val itemSearcSugggestion: LiveData<Event<Resource<ResponseSearchItemDmsSuggestion>>> = _itemSearcSugggestion

    val _itemInScheme = MutableLiveData<Event<Resource<ResponseItemListFromSubCategory>>>()
    val itemInScheme: LiveData<Event<Resource<ResponseItemListFromSubCategory>>> = _itemInScheme

    val _itemSubCategoryListAllFilter = MutableLiveData<Event<Resource<ResponseSubCategoryItem>>>()
    val itemSubCategoryListAllFilter: LiveData<Event<Resource<ResponseSubCategoryItem>>> = _itemSubCategoryListAllFilter

    val _itemOne = MutableLiveData<Event<Resource<ResponseItemOne>>>()
    val itemOne: LiveData<Event<Resource<ResponseItemOne>>> = _itemOne

    //todo set response sucess, loading, error in view model..

    private val _itemListFromSubcategoryOrderRequesteWithPaging = MutableLiveData<List<ResponseCategoryItemAllList.Data>>()
    val itemListFromSubcategoryOrderRequesteWithPaging: LiveData<List<ResponseCategoryItemAllList.Data>> = _itemListFromSubcategoryOrderRequesteWithPaging

    private val _loadingItemListFromSubcategoryOrderRequesteWithPaging = MutableLiveData<Boolean>()
    val loadingItemListFromSubcategoryOrderRequesteWithPaging: LiveData<Boolean> = _loadingItemListFromSubcategoryOrderRequesteWithPaging

    private val _errorItemListFromSubcategoryOrderRequesteWithPaging= MutableLiveData<String?>()
    val errorItemListFromSubcategoryOrderRequesteWithPaging: LiveData<String?> = _errorItemListFromSubcategoryOrderRequesteWithPaging

    private val _distributorProfileData = MutableLiveData<Event<Resource<DistributorProfileModel>>>()
    val distributorProfileData: LiveData<Event<Resource<DistributorProfileModel>>> = _distributorProfileData

    private val _profileImageUpload = MutableLiveData<Event<Resource<ProfileImageUploadModel>>>()
    val profileImageUpload: LiveData<Event<Resource<ProfileImageUploadModel>>> = _profileImageUpload

    val _createSoRequest = MutableLiveData<Event<Resource<ResponseGlobal>>>()
    val createSoRequest: LiveData<Event<Resource<ResponseGlobal>>> = _createSoRequest

    val _createBAOrderRequest = MutableLiveData<Event<Resource<ResponseGlobal>>>()
    val createBAOrderRequest: LiveData<Event<Resource<ResponseGlobal>>> = _createBAOrderRequest

    private val _requestOrderOneDetailData = MutableLiveData<Event<Resource<OrderOneDetailModel>>>()
    val requestOrderOneDetailData: LiveData<Event<Resource<OrderOneDetailModel>>> = _requestOrderOneDetailData

    val _updateSoRequest = MutableLiveData<Event<Resource<ResponseGlobal>>>()
    val updateSoRequest: LiveData<Event<Resource<ResponseGlobal>>> = _updateSoRequest

    //todo set response sucess, loading, error in view model..

    private val _dispatchListWithPaging = MutableLiveData<List<ResponseDispatchList.Data>>()
    val dispatchListWithPaging: LiveData<List<ResponseDispatchList.Data>> = _dispatchListWithPaging

    private val _loadingdispatchListWithPaging = MutableLiveData<Boolean>()
    val loadingdispatchListWithPaging: LiveData<Boolean> = _loadingdispatchListWithPaging

    private val _errordispatchListWithPaging = MutableLiveData<String?>()
    val errordispatchListWithPaging: LiveData<String?> = _errordispatchListWithPaging

    //todo set response sucess, loading, error in view model..

    private val _deliveryNotePendingListWithPaging = MutableLiveData<List<ResponsePendingDeliveryNote.Data>>()
    val deliveryNotePendingListWithPaging: LiveData<List<ResponsePendingDeliveryNote.Data>> = _deliveryNotePendingListWithPaging

    private val _loadingdeliveryNotePendingListWithPaging = MutableLiveData<Boolean>()
    val loadingdeliveryNotePendingListWithPaging: LiveData<Boolean> = _loadingdeliveryNotePendingListWithPaging

    private val _errordeliveryNotePendingListWithPaging= MutableLiveData<String?>()
    val errordeliveryNotePendingListWithPaging: LiveData<String?> = _errordeliveryNotePendingListWithPaging

    private val _pendingByOrderListData = MutableLiveData<Event<Resource<PendingByOrderModel>>>()
    val pendingByOrderListData: LiveData<Event<Resource<PendingByOrderModel>>> = _pendingByOrderListData

    //todo set response sucess, loading, error in view model..

    private val _deliveryNotePendingWiseWithPaging = MutableLiveData<List<ResponsePendingOrderInner.Data>>()
    val deliveryNotePendingWiseWithPaging: LiveData<List<ResponsePendingOrderInner.Data>> = _deliveryNotePendingWiseWithPaging

    private val _loadingdeliveryNotePendingWiseWithPaging = MutableLiveData<Boolean>()
    val loadingdeliveryNotePendingWiseWithPaging: LiveData<Boolean> = _loadingdeliveryNotePendingWiseWithPaging

    private val _errordeliveryNotePendingWiseWithPagingWithPaging= MutableLiveData<String?>()
    val errordeliveryNotePendingWiseWithPagingWithPaging: LiveData<String?> = _errordeliveryNotePendingWiseWithPagingWithPaging

    private val _orderOneDetailData = MutableLiveData<Event<Resource<OrderOneDetailModel>>>()
    val orderOneDetailData: LiveData<Event<Resource<OrderOneDetailModel>>> = _orderOneDetailData

    private val _bPOneDetailData = MutableLiveData<Event<Resource<ResponseBpOne>>>()
    val bPOneDetailData: LiveData<Event<Resource<ResponseBpOne>>> = _bPOneDetailData

    //start BA work (added by Vinod Pal)
    private val _baLogData = MutableLiveData<Event<Resource<ModelAllLogs>>>()
    val baLogData: LiveData<Event<Resource<ModelAllLogs>>> = _baLogData

    private val _orderListResponse = MutableLiveData<Event<Resource<ModelOrderListing>>>()
    val orderListResponse: LiveData<Event<Resource<ModelOrderListing>>> = _orderListResponse
    //end BA work (added by Vinod Pal)

    private val _requestOrderDeleteData = MutableLiveData<Event<Resource<ResponseGlobal>>>()
    val requestOrderDeleteData: LiveData<Event<Resource<ResponseGlobal>>> = _requestOrderDeleteData

    //todo set response sucess, loading, error in view model..

    private val _soRequestFIlterWithPaging = MutableLiveData<List<DataSoRequestAllFilter>>()
    val soRequestFIlterWithPaging: LiveData<List<DataSoRequestAllFilter>> = _soRequestFIlterWithPaging

    private val _loadingsoRequestFIlterWithPaging = MutableLiveData<Boolean>()
    val loadingsoRequestFIlterWithPaging: LiveData<Boolean> = _loadingsoRequestFIlterWithPaging

    private val _errorsoRequestFIlterWithPaging= MutableLiveData<String?>()
    val errorsoRequestFIlterPaymentsWithPaging: LiveData<String?> = _errorsoRequestFIlterWithPaging


    private val _orderFilterWithPaging = MutableLiveData<List<ModelOrderListing.Data>>()
    val orderFilterWithPaging: LiveData<List<ModelOrderListing.Data>> = _orderFilterWithPaging

    private val _loadingOrderFilterWithPaging = MutableLiveData<Boolean>()
    val loadingOrderFilterWithPaging: LiveData<Boolean> = _loadingOrderFilterWithPaging

    private val _errorOrderFilterWithPaging= MutableLiveData<String?>()
    val errorOrderFilterWithPaging: LiveData<String?> = _errorOrderFilterWithPaging

    private val _refreshList = MutableLiveData<Boolean>()
    val refreshList: LiveData<Boolean> = _refreshList

    fun editAdapterData(isEditable: Boolean) {
        _refreshList.value = isEditable
    }

    fun verifyOTP(data: HashMap<String, String>, context: Context) {

        _loginSignUpData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.verifyOTP(data, context)
            _loginSignUpData.postValue(Event(result))
        }
    }
    fun getTermsConditionDetails( context : Context) {
        _termsSignIn.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTermsSignIn(context)
            _termsSignIn.postValue(Event(result))

        }
    }

    fun logoutApi(data: JsonObject, context: Context) {
        _loginSignUpData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.logoutApi(data, context)
            _loginSignUpData.postValue(Event(result))
        }
    }

    fun createEnquiryApi(data: JsonObject, context: Context) {
        _loginSignUpData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createEnquiryApi(data, context)
            _loginSignUpData.postValue(Event(result))
        }
    }
    fun getExpenseTypeApi(context: Context) {
        _expenseTypeData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getExpenseListApi(context)
            _expenseTypeData.postValue(Event(result))
        }
    }
    fun getAllExpenseListApi(data: JsonObject,context: Context) {
        _allExpenseListData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getItemAllExpenseListALlFilter(data,context)
            _allExpenseListData.postValue(Event(result))
        }
    }



    fun getListingOfTeamUser(data: JsonObject,context: Context) {
        _listingGetTeamUser.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getListingOfTeamUser(data,context)
            _listingGetTeamUser.postValue(Event(result))
        }
    }

    fun getListingOfTeamUserExpandable(data: JsonObject,context: Context) {
        _listingGetTeamUser.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getListingOfTeamUserExpandable(data,context)
            _listingGetTeamUser.postValue(Event(result))
        }
    }
    fun createExpenseApi(jsonObject: JsonObject, context: Context) {
        _createExpenseData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createExpenseListApi(jsonObject,context)
            _createExpenseData.postValue(Event(result))
        }
    }
    fun getEnquiryListApi(data: JsonObject, context : Context) {
        _enquiryAllListData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getEnquiryListApi(data, context)
            _enquiryAllListData.postValue(Event(result))
        }
    }

    fun getDocumentAllApi( context : Context) {
        _documentALlList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getDocumentAllApi(context)
            _documentALlList.postValue(Event(result))
        }
    }


    fun getDocumentAllItemListApi(data: JsonObject, context : Context) {
        _documentItemAllList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getDocumentAllItemListApi(data, context)
            _documentItemAllList.postValue(Event(result))
        }
    }



    fun getSchemeDocumentAllItemListApi(data: JsonObject, context : Context) {
        _documentSchemeItemAllList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getSchemeDocumentAllItemListApi(data, context)
            _documentSchemeItemAllList.postValue(Event(result))
        }
    }

    fun getBusinessPartnerApi(jsonObject: JsonObject, beatPlanFragment: Context) {
        _beatPlanBPList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getBPListApi(jsonObject, beatPlanFragment)
            _beatPlanBPList.postValue(Event(result))
        }
    }


    fun getBeatPlanListing(jsonObject: JsonObject, beatPlanFragment: Context) {
        _beatPlanItemAllList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getBeatPlanAllItemListApi(jsonObject, beatPlanFragment)
            _beatPlanItemAllList.postValue(Event(result))
        }
    }

    fun getBeatPlanCustomerAllListing(jsonObject: JsonObject, beatPlanFragment: Context) {
        _beatPlanItemCustomerAllList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getBeatPlanCustomerAllList(jsonObject, beatPlanFragment)
            _beatPlanItemCustomerAllList.postValue(Event(result))
        }
    }

    fun getItemAllCategoryListALlFilter(context : Context) {
        _itemCategoryListAllFilter.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {

            val result = repos.getItemAllCategoryListALlFilter(context)
            _itemCategoryListAllFilter.postValue(Event(result))

        }
    }

    fun getBpListALlFilter(data: JsonObject, context: Context) {
        _bpListAllFilter.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {

            val result = repos.getBpListALlFilter(data,context)
            _bpListAllFilter.postValue(Event(result))

        }
    }

fun getAllItemListByCategory(data: JsonObject, context: Context) {
        _allItemByCategory.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {

            val result = repos.getAllItemListByCategory(data,context)
            _allItemByCategory.postValue(Event(result))

        }
    }

    fun getDashboardTargetVsAchieved(data: JsonObject, context: Context) {
        _targetVsAchievedSales.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {

            val result = repos.getDashboardTargetVsAchieved(data,context)
            _targetVsAchievedSales.postValue(Event(result))

        }
    }

    fun getDashboardTotalSales(data: JsonObject, context: Context) {
        _totalTargetSales.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {

            val result = repos.getDashboardTotalSales(data,context)
            _totalTargetSales.postValue(Event(result))

        }
    }

    fun getDashboardIncentive(data: JsonObject, context: Context) {
        _totalIncentives.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {

            val result = repos.getDashboardIncentive(data,context)
            _totalIncentives.postValue(Event(result))

        }
    }

    fun getSchemeListALlFilter(context : Context) {
        _schemeListAllFilter.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {

            val result = repos.getSchemeListALlFilter(JsonObject(), context)
            _schemeListAllFilter.postValue(Event(result))

        }
    }

    fun recentSearchesAndOrders(data: JsonObject, context: Context){
        _recentSearchAndOrder.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.recentSearchesAndOrders(data, context)
            _recentSearchAndOrder.postValue(Event(result))
        }
    }

    fun searchItemInDMS(data: JsonObject, context: Context){
        _itemSearcSugggestion.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.searchItemInDMS(data, context)
            _itemSearcSugggestion.postValue(Event(result))
        }
    }

    fun schemeItemInDMS(data: JsonObject, context: Context){
        _itemInScheme.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.schemeItemInDMS(data, context)
            _itemInScheme.postValue(Event(result))
        }
    }

    fun getItemAllSubCategoryListALlFilter(jsonObject: JsonObject,context : Context) {
        _itemSubCategoryListAllFilter.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {

            val result = repos.getItemAllSubCategoryListALlFilter(jsonObject,context)
            _itemSubCategoryListAllFilter.postValue(Event(result))

        }
    }

    fun getAllItemListFromSubCategoryOrderRequest(jsonObject: JsonObject, context : Context) {

        viewModelScope.launch {
            _loadingItemListFromSubcategoryOrderRequesteWithPaging.value = true
            try {
                val loadedItems = repos.getAllItemListFromSubCategoryOrderRequest(jsonObject, context)
                _itemListFromSubcategoryOrderRequesteWithPaging.value = loadedItems
                _errorItemListFromSubcategoryOrderRequesteWithPaging.value = null
            } catch (e: Exception) {
                _errorItemListFromSubcategoryOrderRequesteWithPaging.value = e.message
            }

            _loadingItemListFromSubcategoryOrderRequesteWithPaging.value = false
        }
    }

    fun getItemOne(data: JsonObject, context: Context){
        _itemOne.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getItemOne(data, context)
            _itemOne.postValue(Event(result))
        }
    }

    fun distributorProfile(data: java.util.HashMap<String, String>, context : Context) {
        _distributorProfileData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.distributorProfile(data, context)
            _distributorProfileData.postValue(Event(result))
        }
    }

    fun distributorImageProfile(data: MultipartBody, context : Context) {
        _profileImageUpload.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.distributorImageProfile(data, context)
            _profileImageUpload.postValue(Event(result))
        }
    }

    /*fun createSoRequest(data: RequestBodyForSoRequestCreate, context: Context){
        _createSoRequest.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createSoRequest(data, context)
            _createSoRequest.postValue(Event(result))
        }
    }*/

    fun createSoRequest(data: ModelCreateOrderRequest, context: Context){
        _createSoRequest.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createSoRequest(data, context)
            _createSoRequest.postValue(Event(result))
        }
    }

fun createBAOrderRequest(data: ModelBACreateOrderRequest, context: Context){
        _createBAOrderRequest.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createBAOrderRequest(data, context)
            _createBAOrderRequest.postValue(Event(result))
        }
    }


    fun requestOrderOneApi(jsonObject: JsonObject, context : Context) {
        _requestOrderOneDetailData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.requestOrderOneApi(jsonObject, context)
            _requestOrderOneDetailData.postValue(Event(result))
        }
    }

    /*fun updateSoRequest(data: RequestBodyForSoRequestCreate, context: Context){
        _updateSoRequest.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.updateSoRequest(data, context)
            _updateSoRequest.postValue(Event(result))
        }
    }*/
fun updateSoRequest(data: ModelSoCreateRequest, context: Context){
        _updateSoRequest.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.updateSoRequest(data, context)
            _updateSoRequest.postValue(Event(result))
        }
    }

    fun getDispatchListPagingApi(jsonObject: JsonObject, context : Context) {

        viewModelScope.launch {
            _loadingdispatchListWithPaging.value = true
            try {
                val loadedItems = repos.getDispatchList(jsonObject, context)
                _dispatchListWithPaging.value = loadedItems
                _errordispatchListWithPaging.value = null
            } catch (e: Exception) {
                _errordispatchListWithPaging.value = e.message
            }

            _loadingdispatchListWithPaging.value = false
        }
    }

    fun getDeliveryNotePendingOrderItemAll(jsonObject: JsonObject, context : Context) {

        viewModelScope.launch {
            _loadingdeliveryNotePendingListWithPaging.value = true
            try {
                val loadedItems = repos.getDeliveryNotePendingOrderItemAll(jsonObject, context)
                _deliveryNotePendingListWithPaging.value = loadedItems
                _errordeliveryNotePendingListWithPaging.value = null
            } catch (e: Exception) {
                _errordeliveryNotePendingListWithPaging.value = e.message
            }

            _loadingdeliveryNotePendingListWithPaging.value = false
        }
    }

    fun pendingByOrder(jsonObject: JsonObject, context : Context) {
        _pendingByOrderListData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.pendingByOrder(jsonObject, context)
            _pendingByOrderListData.postValue(Event(result))
        }
    }

    fun getDeliveryNotePendingOrderWiseInner(jsonObject: JsonObject, context : Context) {

        viewModelScope.launch {
            _loadingdeliveryNotePendingWiseWithPaging.value = true
            try {
                val loadedItems = repos.getDeliveryNotePendingOrderWiseInner(jsonObject, context)
                _deliveryNotePendingWiseWithPaging.value = loadedItems
                _errordeliveryNotePendingWiseWithPagingWithPaging.value = null
            } catch (e: Exception) {
                _errordeliveryNotePendingWiseWithPagingWithPaging.value = e.message
            }

            _loadingdeliveryNotePendingWiseWithPaging.value = false
        }
    }

    fun orderOneApi(jsonObject: JsonObject, context : Context) {
        _orderOneDetailData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.orderOneApi(jsonObject, context)
            _orderOneDetailData.postValue(Event(result))
        }
    }

    fun bPOneApi(jsonObject: JsonObject, context : Context) {
        _bPOneDetailData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.bPOneApi(jsonObject, context)
            _bPOneDetailData.postValue(Event(result))
        }
    }

    fun requestOrderDeleteApi(jsonObject: JsonObject, context : Context) {
        _requestOrderDeleteData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.requestOrderDeleteApi(jsonObject, context)
            _requestOrderDeleteData.postValue(Event(result))
        }
    }

    fun getSoRequestAllFilter(jsonObject: JsonObject, context : Context) {

        viewModelScope.launch {
            _loadingsoRequestFIlterWithPaging.value = true
            try {
                val loadedItems = repos.getSoRequestAllFilter(jsonObject, context)
                _soRequestFIlterWithPaging.value = loadedItems
                _errorsoRequestFIlterWithPaging.value = null
            } catch (e: Exception) {
                _errorsoRequestFIlterWithPaging.value = e.message
            }

            _loadingsoRequestFIlterWithPaging.value = false
        }
    }

    // start ba work (added by Vinod Pal)
    fun getDashboardLogs(jsonObject: JsonObject, context : Context) {
        _baLogData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getDashboardLogs(jsonObject, context)
            _baLogData.postValue(Event(result))
        }
    }

    fun getOrderListingAllFilterPage(jsonObject: JsonObject, context : Context) {
        _orderListResponse.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getOrderListingAllFilterPage(jsonObject, context)
            _orderListResponse.postValue(Event(result))
        }
    }

    //end ba work (added by Vinod Pal)


    fun getOrderListingAllFilter(jsonObject: JsonObject, context : Context) {

        Log.d("Request", "getOrderListingAllFilter: $jsonObject")
        viewModelScope.launch {
            _loadingOrderFilterWithPaging.value = true
            try {
                val loadedItems = repos.getOrderListingAllFilter(jsonObject, context)
                _orderFilterWithPaging.value = loadedItems
                _errorsoRequestFIlterWithPaging.value = null
            } catch (e: Exception) {
                _errorsoRequestFIlterWithPaging.value = e.message
            }

            _loadingOrderFilterWithPaging.value = false
        }
    }
    fun getBAOrderListingAllFilter(jsonObject: JsonObject, context: Context) {

        Log.d("Request", "getOrderListingAllFilter: $jsonObject")
        viewModelScope.launch {
            _loadingOrderFilterWithPaging.value = true
            try {
                val loadedItems = repos.getBAOrderListingAllFilter(jsonObject, context)
                _orderFilterWithPaging.value = loadedItems
                _errorsoRequestFIlterWithPaging.value = null
            } catch (e: Exception) {
                _errorsoRequestFIlterWithPaging.value = e.message
            }

            _loadingOrderFilterWithPaging.value = false
        }
    }

    fun getOrderDispatchListingAllFilter(jsonObject: JsonObject, context : Context) {

        Log.d("Request", "getOrderListingAllFilter: $jsonObject")
        viewModelScope.launch {
            _loadingOrderFilterWithPaging.value = true
            try {
                val loadedItems = repos.getOrderDispatchListingAllFilter(jsonObject, context)
                _orderFilterWithPaging.value = loadedItems
                _errorsoRequestFIlterWithPaging.value = null
            } catch (e: Exception) {
                _errorsoRequestFIlterWithPaging.value = e.message
            }

            _loadingOrderFilterWithPaging.value = false
        }
    }

}