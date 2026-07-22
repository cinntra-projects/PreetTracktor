package com.preetTractor.galaxyAndroid.sessionManagement

import android.content.Context
import android.content.SharedPreferences

class SessionManagement(var _context: Context) {

      companion object {
            // todo Sharedpref file name
            private const val PREF_NAME = "AndroidHivePref"
      }

      // todo Shared Preferences
      var pref: SharedPreferences

      //todo  Editor for Shared preferences
      var editor: SharedPreferences.Editor

      // todo Shared pref mode
      var PRIVATE_MODE = 0


      //todo Constructor
      init {
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            editor = pref.edit()
      }


      fun setSharedPrefernce(key: String, value: String) {
            editor = pref.edit()
            editor.putString(key, value)
            editor.commit()
      }

      private fun getDataFromSharedPreferences(Key: String): String? {
            return try {
                  val returnString: String? = pref.getString(Key, "")
                  returnString
            } catch (e: java.lang.Exception) {
                  ""
            }
      }

      fun ClearSession() {
            editor = pref.edit()
            editor.clear()
            editor.commit()
      }

      fun setMPINValue(mpinValue: String?) {
            if (mpinValue != null) {
                  setSharedPrefernce("mpinValue", mpinValue)
            }
      }

      fun getMPINValue(): String? {
            return getDataFromSharedPreferences("mpinValue")
      }

      fun setMobileNo(mobile: String?) {
            if (mobile != null) {
                  setSharedPrefernce("mobile", mobile)
            }
      }

      fun getMobileNO(): String? {
            return getDataFromSharedPreferences("mobile")
      }


      fun setCardCode(cardCode: String?) {
            if (cardCode != null) {
                  setSharedPrefernce("_cardCode", cardCode)
            }
      }

      fun getCardCode(): String? {
            return getDataFromSharedPreferences("_cardCode")
      }

      fun setSalesEmployeeCode(salesEmployeeCode: String?) {
            if (salesEmployeeCode != null) {
                  setSharedPrefernce("_salesEmployeeCode", salesEmployeeCode)
            }
      }

      fun getSalesEmployeeCode(): String? {
            return getDataFromSharedPreferences("_salesEmployeeCode")
      }

      fun setCardName(cardName: String?) {
            if (cardName != null) {
                  setSharedPrefernce("card_name", cardName)
            }
      }

      fun getCardName(): String? {
            return getDataFromSharedPreferences("card_name")
      }

      fun setDistributorID(distributor_id: String?) {
            if (distributor_id != null) {
                  setSharedPrefernce("_distributor_id", distributor_id)
            }
      }

      fun getDistributorID(): String? {
            return getDataFromSharedPreferences("_distributor_id")
      }


      fun setFromWhere(fromWhere: String) {
            if (fromWhere != null) {
                  setSharedPrefernce("fromWhere", fromWhere)
            }
      }

      fun getFromWhere(): String? {
            return getDataFromSharedPreferences("fromWhere")
      }

      fun setToken(token: String) {
            if (token != null) {
                  setSharedPrefernce("token", token)
            }
      }

      fun getToken(): String? {
            return getDataFromSharedPreferences("token")
      }


}