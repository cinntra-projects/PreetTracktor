package com.preetTractor.galaxyAndroid.apiHelper

import android.content.Context
import android.util.Log
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForBACart
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForCart
import com.preetTractor.galaxyAndroid.searchUi.model.DataSearchItemDmsSuggestion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pixplicity.easyprefs.library.Prefs

object AppConstants {

    val REQUEST_ID_MULTIPLE_PERMISSIONS = 7





    /****Api's****/

    const val LOGIN_SIGNUP = "employee/galaxy_login_or_signup"
    const val TYPE_OF_EXPENSE = "expense/type_of_expense"

    const val LOGIN_WITH_SAME_MOBILE = "employee/galaxy_status"
    const val CREATE_FEEDBACK = "feedback/dms_create_feedback"

    const val VERIFY_OTP = "employee/galaxy_verify_otp"

    //const val DISTRIBUTOR_PROFILE = "employee/distributor_profile"
    const val DISTRIBUTOR_PROFILE = "employee/galaxy_profile"

    const val DISTRIBUTOR_IMAGE_UPLOAD = "employee/distributor_image_upload"

    const val LOGOUT = "employee/galaxy_signout"

    const val CREATE_ENQUIRY = "enquiry/create"

    const val ENQUIRY_ALL_FILTER_LIST = "enquiry/all_filter"

    const val ENQUIRY_ALL_LIST = "enquiry/all"

    const val DOCUMENT_ALL = "document/all"
    const val DOCUMENT_SCHEME_ALL = "document/scheme_all"

    const val DOCUMENT_ALL_ITEM = "document/all_doc_item"
    const val SCHEME_DOCUMENT_ALL_ITEM = "document/scheme_all_doc_item"
    const val EXPENSE_CREATE = "expense/galaxy_create_expense"
    const val EXPENSE_UPDATE = "expense/update"
    const val EXPENSE_ONE = "expense/one"
    const val ALL_EXPENSE_LIST = "expense/galaxy_all_filter_page"
    const val EMPLOYEE_USER_REPORTING_TO = "employee/galaxy_user_reportingto"

    const val EMPLOYEE_USER_REPORTING_TO_EXPENDABLE = "employee/galaxy_user_reportingto_schema"

    const val ALL_TAGS_LIST = "document/all_tags"

    const val INVOICE_DASHBOARD_COUNT = "invoice/dms_dashboard"

    const val CATEGORY_LIST = "item/bp_invoice_category"

    const val ALL_MEDIA_FILES = "document/all_media_file"

    const val ALL_MEDIA_TAGS = "document/all_media_tags"

    const val PENDING_ORDERWISE = "deliverynote/pending_orderwise"

    const val PENDING_BYORDER = "deliverynote/pending_byorder"

    const val ORDER_ONE = "order/one"

    const val CATEGORY_ITEMS = "item/bp_invoice_category"

    const val CATEGORY_SUB_CATEGORY = "item/dms_subcategory_by_category_dashboard"

    const val CATEGORY_SUB_CATEGORY_ITEMS = "item/dms_items_by_subcategory_dashboard"

    const val PARTICULAR_ITEM_INVOICES = "item/bp_item_invoices"

    const val SHOW_LEDGER_INFO = "businesspartner/bp_ledger"

    const val LEDGER_REPORT = "journalentries/dms_bp_wise"
    const val DMS_BP_WISE_DOC_DETAIL = "journalentries/dms_bp_wise_docdetail"

    const val INVOICE_ONE = "invoice/one"

    const val RECEIPT_INVOICE_ONE = "invoice/one_receipt"

    const val VENDOR_RECEIPT_INVOICE_ONE = "purchaseinvoices/one_receipt"

    const val CREDIT_INVOICE_ONE = "invoice/credit_notes_one"

    const val AP_CREDIT_INVOICE_ONE = "purchaseinvoices/ap_credit_notes_one"


    /*** Payment Api's ***/

    const val AGING_REPORT_API = "invoice/bp_invoice_pending_dues_days"

    const val RECENT_PAYMENT_LIST_API = "payment/all_filter"

    const val API_PAYMENT_DASHBOARD = "journalentries/dms_bp_wise"

    const val API_PAYMENT_DUE_OVER_DUE = "invoice/bp_invoice_dues_payment"

    const val PAYMENT_INVOICE_PENDING_DUES_LIST_API = "invoice/bp_invoice_pending_dues_list"
    const val DELIVERYNOTE_DMS_PENDING_ORDER_ITEM_ALL_API = "deliverynote/dms_pendingorder_item_all"
    const val DELIVERYNOTE_DMS_PENDING_ORDER_WISE_API = "deliverynote/dms_pending_orderwise"


    const val DMS_SETTING_DETAILS_API = "appsetting/dms_setting_detail"
    const val DMS_SETTING_TERMS_SIGN_IN_API = "appsetting/dms_all_termcondition"
    const val BEAT_PLAN_BP_LISTING = "activity/beatplan_bp_dropdown"
    const val BEAT_PLAN_LISTING = "activity/galaxy_beatplan_all_filter"
    const val BEAT_PLAN_LISTING_CUSTOMER_ALL = "activity/galaxy_beatplan_customer_all"
    const val DISPATCH_LIST = "invoice/bp_invoice"


    //const val SCHEME_LIST_ALL_FILTER = "scheme/all_filter"
    const val SCHEME_LIST_ALL_FILTER = "scheme/galaxy_all_filter"
    //const val ITEM_ALL_CATEGORY_LIST = "item/all_category_list"
//    const val ITEM_ALL_CATEGORY_LIST = "item/galaxy_all_category_list"
    const val ITEM_ALL_CATEGORY_LIST = "item/galaxy_category_all" // todo api for category listing in order module
    //const val ITEM_ALL_SUB_CATEGORY_LIST = "item/all_subcategory_list"
    const val ITEM_ALL_SUB_CATEGORY_LIST = "item/galaxy_all_subcategory_list"
    //const val ITEM_ALL_ITEM_LIST = "item/all_item_list"
//    const val ITEM_ALL_ITEM_LIST = "item/galaxy_all_item_list"
    const val ITEM_ALL_ITEM_LIST = "item/galaxy_category_item_all_filter" // todo api for category item listing in order module

    const val BUSINESS_PARTNER_GALAXY_ONE = "businesspartner/galaxy_one"
    const val INVOICE_DMS_INCOMING_PAYMENTS = "invoice/dms_incoming_payments"

    //        const val SO_REQUEST_CREATE = "sorequest/galaxy_create"
    const val SO_REQUEST_CREATE = "order/galaxy_create"
    const val SO_REQUEST_ALL_FILTER = "sorequest/galaxy_all_filter"
    const val SO_REQUEST_LIST_ALL_FILTER_PAGE = "order/galaxy_all_filter_page"
    const val SO_REQUEST_ONE = "sorequest/galaxy_one"
    const val SO_REQUEST_SO_DELETE = "sorequest/galaxy_so_delete"
    const val SO_REQUEST_SO_UPDATE = "sorequest/galaxy_update"
    const val ITEM_DMS_SEARCH_SUGGESTION = "item/dms_search_suggestion"
    const val ITEM_DMS_SCHEME_ITEM_LIST = "item/dms_scheme_item_list"
    const val ITEM_DMS_LAST_ORDER_ITEM = "item/dms_last_order_item"

    const val SO_REQUEST_LIST_DISPATCH_ORDER = "invoice/galaxy_all_filter_page" // todo dispatch order api

    const val SO_REQUEST_TODAY_VISIT_DASHBOARD = "activity/galaxy_dashboard"

    const val ITEM_ONE = "item/dms_item_one"

    //Start Beauty Advisor Api End Url's (added by Vinod Pal)

    const val BA_PREVIOUS_ORDER_LISTING = ""
    const val BA_PREVIOUS_ORDER_DETAILS = ""
    const val BA_BP_ALL_FILTER = "businesspartner/galaxy_bp_all_filter"
    const val BA_BP_GALAXY_ONE = ""
    const val BA_ORDER_LIST_ALL_FILTER_PAGE = "order/galaxy_ba_all_filter_page"
    const val BA_DASHBOARD_TARGET_ASSIGNED_VS_ACHIEVED = "employee/employee_target_ba"
    const val BA_DASHBOARD_TOTAL_SALES = "employee/employee_target_ba_sales"
    const val BA_DASHBOARD_INCENTIVE = "incentive/galaxy_calculation_incentive_amount"
    const val BA_DASHBOARD_LOGS = "employee/top_bp_by_order_filter"

   const val LEAD_ALl_API = "lead/all_filter_page"
   const val FOLLOW_UP_API = "activity/followup"
   const val SAVE_BDRC_DATA_API = "businesspartner/achievement/save-achievements"

    const val DASHBOARD_TARGET = "employee/dashboard"

    var cartListForOrderRequest = mutableListOf<LocalDataForCart>()

    fun saveCartListToPreferences(context: Context, cartList: List<LocalDataForCart>) {

        val gson = Gson()
        val json = gson.toJson(cartList)
        Prefs.putString("cart_list", json)

    }

    fun saveBaCartListToPreferences(cartList: List<LocalDataForBACart>) {
        val gson = Gson()
        val json = gson.toJson(cartList)
        Prefs.putString("cart_list_ba", json)

    }

    fun getBaCartListFromPreferences(): MutableList<LocalDataForBACart> {
        val gson = Gson()
        val json = Prefs.getString("cart_list_ba", null)
        val type = object : TypeToken<MutableList<LocalDataForBACart>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }
    /*

        fun saveCartListToPreferences(context: Context, cartList: List<LocalDataForCart>) {

            val gson = Gson()
            val json = gson.toJson(cartList)
            Prefs.putString("cart_list", json)

        }

        fun getCartListFromPreferences(context: Context): MutableList<LocalDataForCart> {
            val gson = Gson()
            val json = Prefs.getString("cart_list", null)
            val type = object : TypeToken<MutableList<LocalDataForCart>>() {}.type
            return gson.fromJson(json, type) ?: mutableListOf()
        }


        fun saveDataSearchItemListToPreferences(
            context: Context,
            list: List<DataSearchItemDmsSuggestion>
        ) {

            val gson = Gson()
            val json = gson.toJson(list)
            Prefs.putString("data_search_list", json)

            Log.e(
                "APPCONSTANT SEARCH>>>>>",
                "saveDataSearchItemListToPreferences: ${Prefs.getString("data_search_list")}"
            )

        }

        fun getDataSearchItemListFromPreferences(context: Context): MutableList<DataSearchItemDmsSuggestion> {

            val gson = Gson()
            val json = Prefs.getString("data_search_list", null)
            val type = object : TypeToken<MutableList<DataSearchItemDmsSuggestion>>() {}.type
            return gson.fromJson(json, type) ?: mutableListOf()

            Log.e(
                "APPCONSTANT SEARCH>>>>>",
                "saveDataSearchItemListToPreferences: ${Prefs.getString("data_search_list")}"
            )
        }

        fun addItemToDataSearchList(context: Context, item: DataSearchItemDmsSuggestion) {
            val list = getDataSearchItemListFromPreferences(context)
            if (list.none { it.Name == item.Name }) {
                list.add(item)
                saveDataSearchItemListToPreferences(context, list)
            }
        }

    */

    //shubham


    fun getVideoIdFromYouTubeUrl(youtubeUrl: String?): String? {
        var videoId: String? = null
        if (youtubeUrl != null && youtubeUrl.contains("youtube.com/watch?v=")) {
            val startIndex = youtubeUrl.indexOf("v=") + 2 // Move to the character after "v="
            var endIndex = youtubeUrl.indexOf("&", startIndex) // Find the end of the video ID
            if (endIndex == -1) {
                endIndex = youtubeUrl.length // If "&" is not found, use the end of the string
            }
            videoId = youtubeUrl.substring(startIndex, endIndex)
        }
        return videoId
    }

    fun getDataSearchItemListFromPreferences(context: Context): MutableList<DataSearchItemDmsSuggestion> {

        val gson = Gson()
        val json = Prefs.getString("data_search_list", null)
        val type = object : TypeToken<MutableList<DataSearchItemDmsSuggestion>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()

    }

    fun addItemToDataSearchList(context: Context, item: DataSearchItemDmsSuggestion) {
        val list = getDataSearchItemListFromPreferences(context)
        if (list.none { it.Name == item.Name }) {
            list.add(item)
            saveDataSearchItemListToPreferences(context, list)
        }
    }

    private fun saveDataSearchItemListToPreferences(
        context: Context,
        list: List<DataSearchItemDmsSuggestion>
    ) {

        val gson = Gson()
        val json = gson.toJson(list)
        Prefs.putString("data_search_list", json)

        Log.e(
            "APPCONSTANT SEARCH>>>>>",
            "saveDataSearchItemListToPreferences: ${Prefs.getString("data_search_list")}"
        )

    }

    fun getCartListFromPreferences(context: Context): MutableList<LocalDataForCart> {
        val gson = Gson()
        val json = Prefs.getString("cart_list", null)
        val type = object : TypeToken<MutableList<LocalDataForCart>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }


}