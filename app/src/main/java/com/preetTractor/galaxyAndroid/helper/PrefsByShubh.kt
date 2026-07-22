package com.preetTractor.galaxyAndroid.helper

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import com.google.gson.Gson
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ResponseBpOne
import com.pixplicity.easyprefs.library.Prefs

@Suppress("unused")
object PrefsByShubh {
    private const val DEFAULT_SUFFIX = "_preferences"
    private const val LENGTH = "#LENGTH"
    lateinit var mPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    /**
     * Initialize the Prefs helper class to keep a reference to the SharedPreference for this
     * application the SharedPreference will use the package name of the application as the Key.
     * This method is deprecated please us the new builder.
     *
     * @param context the Application context.
     */
    @Deprecated("")
    fun initPrefs(context: Context?) {
        Prefs.Builder().setContext(context).build()
    }

    private fun initPrefs(context: Context, prefsName: String?, mode: Int) {
        mPrefs = context.getSharedPreferences(prefsName, mode)
        editor= mPrefs.edit()
    }

    val preferences: SharedPreferences?

        /**
         * Returns the underlying SharedPreference instance
         *
         * @return an instance of the SharedPreference
         * @throws RuntimeException if SharedPreference instance has not been instantiated yet.
         */
        get() {
            if (mPrefs != null) {
                return mPrefs
            }
            throw RuntimeException(
                "Prefs class not correctly instantiated. Please call Builder.setContext().build() in the Application class onCreate."
            )
        }

    val all: Map<String, *>
        /**
         * @return Returns a map containing a list of pairs key/value representing
         * the preferences.
         * @see android.content.SharedPreferences.getAll
         */
        get() = preferences!!.all

    /**
     * Retrieves a stored int value.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not
     * an int.
     * @see android.content.SharedPreferences.getInt
     */
    fun getInt(key: String?, defValue: Int): Int {
        return preferences!!.getInt(key, defValue)
    }

    /**
     * Retrieves a stored boolean value.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not a boolean.
     * @see android.content.SharedPreferences.getBoolean
     */
    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return preferences!!.getBoolean(key, defValue)
    }

    /**
     * Retrieves a stored long value.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not a long.
     * @see android.content.SharedPreferences.getLong
     */
    fun getLong(key: String?, defValue: Long): Long {
        return preferences!!.getLong(key, defValue)
    }

    /**
     * Returns the double that has been saved as a long raw bits value in the long preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue the double Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not a long.
     * @see android.content.SharedPreferences.getLong
     */
    fun getDouble(key: String?, defValue: Double): Double {
        return java.lang.Double.longBitsToDouble(
            preferences!!.getLong(
                key,
                java.lang.Double.doubleToLongBits(defValue)
            )
        )
    }

    var businessPartnerDetails: ResponseBpOne?
        get() {
            val gson = Gson()
            val json = preferences?.getString("business_partner", "")
            return gson.fromJson(json, ResponseBpOne::class.java)
        }
        set(user) {
            val gson = Gson()
            val json = gson.toJson(user)
            editor.putString("business_partner", json)
            editor.apply()
        }

    fun setMPINValue(mpinValue: String?) {
        if (mpinValue != null) {
            putString("mpinValue", mpinValue)
        }
    }

    fun getMPINValue(): String? {
        return preferences?.getString("mpinValue","")
    }

    fun setFromWhere(fromWhere : String) {
        if (fromWhere != null) {
            putString("fromWhere", fromWhere)
        }
    }
    fun setMobileNo(mobile: String?) {
        if (mobile != null) {
            putString("mobile", mobile)
        }
    }
    fun ClearSession() {
        editor = preferences!!.edit().apply {
         clear()
         commit()
        }
    }
    fun setCardCode(cardCode: String?) {
        if (cardCode != null) {
            putString("_cardCode", cardCode)
        }
    }

    fun getCardCode(): String? {
        return preferences?.getString("_cardCode","")
    }

    fun setSalesEmployeeCode(salesEmployeeCode: String?) {
        if (salesEmployeeCode != null) {
            putString("_salesEmployeeCode", salesEmployeeCode)
        }
    }

    fun getSalesEmployeeCode(): String? {
        return preferences?.getString("_salesEmployeeCode","")
    }

    fun setCardName(cardName: String?) {
        if (cardName != null) {
            putString("card_name", cardName)
        }
    }

    fun getCardName(): String? {
        return preferences?.getString("card_name","")
    }

    fun setDistributorID(distributor_id: String?) {
        if (distributor_id != null) {
            putString("_distributor_id", distributor_id)
        }
    }

    fun getDistributorID(): String? {
        return preferences?.getString("_distributor_id","")
    }

    fun setToken(token : String) {
        if (token != null) {
            putString("token", token)
        }
    }

    fun getToken(): String? {
        return preferences?.getString("token","")
    }

    fun getMobileNO(): String? {
        return preferences?.getString("mobile","")
    }

    fun getFromWhere(): String? {
        return preferences?.getString("fromWhere","")
    }

    /**
     * Retrieves a stored float value.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not a float.
     * @see android.content.SharedPreferences.getFloat
     */
    fun getFloat(key: String?, defValue: Float): Float {
        return preferences!!.getFloat(key, defValue)
    }

    /**
     * Retrieves a stored String value.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not a String.
     * @see android.content.SharedPreferences.getString
     */
    fun getString(key: String?, defValue: String?): String? {
        return preferences!!.getString(key, defValue)
    }

    /**
     * Retrieves a Set of Strings as stored by [.putStringSet]. On Honeycomb and
     * later this will call the native implementation in SharedPreferences, on older SDKs this will
     * call [.getOrderedStringSet].
     * **Note that the native implementation of [SharedPreferences.getStringSet] does not reliably preserve the order of the Strings in the Set.**
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference values if they exist, or defValues otherwise.
     * @throws ClassCastException if there is a preference with this name that is not a Set.
     * @see android.content.SharedPreferences.getStringSet
     * @see .getOrderedStringSet
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun getStringSet(key: String, defValue: Set<String?>?): Set<String?>? {
        val prefs = preferences
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            prefs!!.getStringSet(key, defValue)
        } else {
            // Workaround for pre-HC's missing getStringSet
            getOrderedStringSet(key, defValue)
        }
    }

    /**
     * Retrieves a Set of Strings as stored by [.putOrderedStringSet],
     * preserving the original order. Note that this implementation is heavier than the native
     * [.getStringSet] method (which does not guarantee to preserve order).
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValues otherwise.
     * @throws ClassCastException if there is a preference with this name that is not a Set of
     * Strings.
     * @see .getStringSet
     */
    fun getOrderedStringSet(key: String, defValue: Set<String?>?): Set<String?>? {
        val prefs = preferences
        if (prefs!!.contains(key + LENGTH)) {
            val set = LinkedHashSet<String?>()
            val stringSetLength = prefs.getInt(key + LENGTH, -1)
            if (stringSetLength >= 0) {
                for (i in 0 until stringSetLength) {
                    set.add(prefs.getString("$key[$i]", null))
                }
            }
            return set
        }
        return defValue
    }

    /**
     * Stores a long value.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see android.content.SharedPreferences.Editor.putLong
     */
    fun putLong(key: String?, value: Long) {
        val editor = preferences!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    /**
     * Stores an integer value.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see android.content.SharedPreferences.Editor.putInt
     */
    fun putInt(key: String?, value: Int) {
        val editor = preferences!!.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * Stores a double value as a long raw bits value.
     *
     * @param key   The name of the preference to modify.
     * @param value The double value to be save in the preferences.
     * @see android.content.SharedPreferences.Editor.putLong
     */
    fun putDouble(key: String?, value: Double) {
        val editor = preferences!!.edit()
        editor.putLong(key, java.lang.Double.doubleToRawLongBits(value))
        editor.apply()
    }

    /**
     * Stores a float value.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see android.content.SharedPreferences.Editor.putFloat
     */
    fun putFloat(key: String?, value: Float) {
        val editor = preferences!!.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    /**
     * Stores a boolean value.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see android.content.SharedPreferences.Editor.putBoolean
     */
    fun putBoolean(key: String?, value: Boolean) {
        val editor = preferences!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * Stores a String value.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see android.content.SharedPreferences.Editor.putString
     */
    fun putString(key: String?, value: String?) {
        val editor = preferences!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * Stores a Set of Strings. On Honeycomb and later this will call the native implementation in
     * SharedPreferences.Editor, on older SDKs this will call [.putOrderedStringSet].
     * **Note that the native implementation of [SharedPreferences.Editor.putStringSet] does not reliably preserve the order of the Strings in the Set.**
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see android.content.SharedPreferences.Editor.putStringSet
     * @see .putOrderedStringSet
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun putStringSet(key: String, value: Set<String?>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val editor = preferences!!.edit()
            editor.putStringSet(key, value)
            editor.apply()
        } else {
            // Workaround for pre-HC's lack of StringSets
            putOrderedStringSet(key, value)
        }
    }

    /**
     * Stores a Set of Strings, preserving the order.
     * Note that this method is heavier that the native implementation [.putStringSet] (which does not reliably preserve the order of the Set). To preserve the order of the
     * items in the Set, the Set implementation must be one that as an iterator with predictable
     * order, such as [LinkedHashSet].
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see .putStringSet
     * @see .getOrderedStringSet
     */
    fun putOrderedStringSet(key: String, value: Set<String?>) {
        val editor = preferences!!.edit()
        var stringSetLength = 0
        if (mPrefs!!.contains(key + LENGTH)) {
            // First read what the value was
            stringSetLength = mPrefs!!.getInt(key + LENGTH, -1)
        }
        editor.putInt(key + LENGTH, value.size)
        var i = 0
        for (aValue in value) {
            editor.putString("$key[$i]", aValue)
            i++
        }
        while (i < stringSetLength) {
            // Remove any remaining values
            editor.remove("$key[$i]")
            i++
        }
        editor.apply()
    }

    /**
     * Removes a preference value.
     *
     * @param key The name of the preference to remove.
     * @see android.content.SharedPreferences.Editor.remove
     */
    fun remove(key: String) {
        val prefs = preferences
        val editor = prefs!!.edit()
        if (prefs.contains(key + LENGTH)) {
            // Workaround for pre-HC's lack of StringSets
            val stringSetLength = prefs.getInt(key + LENGTH, -1)
            if (stringSetLength >= 0) {
                editor.remove(key + LENGTH)
                for (i in 0 until stringSetLength) {
                    editor.remove("$key[$i]")
                }
            }
        }
        editor.remove(key)

        editor.apply()
    }

    /**
     * Checks if a value is stored for the given key.
     *
     * @param key The name of the preference to check.
     * @return `true` if the storage contains this key value, `false` otherwise.
     * @see android.content.SharedPreferences.contains
     */
    fun contains(key: String?): Boolean {
        return preferences!!.contains(key)
    }

    /**
     * Removed all the stored keys and values.
     *
     * @return the [SharedPreferences.Editor] for chaining. The changes have already been committed/applied
     * through the execution of this method.
     * @see android.content.SharedPreferences.Editor.clear
     */
    fun clear(): SharedPreferences.Editor {
        val editor = preferences!!.edit().clear()
        editor.apply()
        return editor
    }

    /**
     * Returns the Editor of the underlying SharedPreferences instance.
     *
     * @return An Editor
     */
    fun edit(): SharedPreferences.Editor {
        return preferences!!.edit()
    }

    /**
     * Builder class for the EasyPrefs instance. You only have to call this once in the Application
     * onCreate. And in the rest of the code base you can call Prefs.method name.
     */
    class Builder {
        private var mKey: String? = null
        private var mContext: Context? = null
        private var mMode = -1
        private var mUseDefault = false

        /**
         * Set the filename of the SharedPreference instance. Usually this is the application's
         * packagename.xml but it can be modified for migration purposes or customization.
         *
         * @param prefsName the filename used for the SharedPreference
         * @return the [com.pixplicity.easyprefs.library.Prefs.Builder] object.
         */
        fun setPrefsName(prefsName: String?): Builder {
            mKey = prefsName
            return this
        }

        /**
         * Set the Context used to instantiate the SharedPreferences
         *
         * @param context the application context
         * @return the [com.pixplicity.easyprefs.library.Prefs.Builder] object.
         */
        fun setContext(context: Context?): Builder {
            mContext = context
            return this
        }

        /**
         * Set the mode of the SharedPreference instance.
         *
         * @param mode Operating mode.  Use 0 or [Context.MODE_PRIVATE] for the
         * default operation, [Context.MODE_WORLD_READABLE]
         * @return the [com.pixplicity.easyprefs.library.Prefs.Builder] object.
         * @see Context.getSharedPreferences
         */
        @SuppressLint("WorldReadableFiles", "WorldWriteableFiles")
        fun setMode(mode: Int): Builder {
            if (mode == ContextWrapper.MODE_PRIVATE || mode == ContextWrapper.MODE_WORLD_READABLE || mode == ContextWrapper.MODE_WORLD_WRITEABLE || mode == ContextWrapper.MODE_MULTI_PROCESS) {
                mMode = mode
            } else {
                throw RuntimeException("The mode in the SharedPreference can only be set too ContextWrapper.MODE_PRIVATE, ContextWrapper.MODE_WORLD_READABLE, ContextWrapper.MODE_WORLD_WRITEABLE or ContextWrapper.MODE_MULTI_PROCESS")
            }

            return this
        }

        /**
         * Set the default SharedPreference file name. Often the package name of the application is
         * used, but if the [android.preference.PreferenceActivity] or [ ] is used the system will append that with
         * _preference.
         *
         * @param defaultSharedPreference true if default SharedPreference name should used.
         * @return the [com.pixplicity.easyprefs.library.Prefs.Builder] object.
         */
        fun setUseDefaultSharedPreference(defaultSharedPreference: Boolean): Builder {
            mUseDefault = defaultSharedPreference
            return this
        }

        /**
         * Initialize the SharedPreference instance to used in the application.
         *
         * @throws RuntimeException if Context has not been set.
         */
        fun build() {
            if (mContext == null) {
                throw RuntimeException("Context not set, please set context before building the Prefs instance.")
            }

            if (TextUtils.isEmpty(mKey)) {
                mKey = mContext!!.packageName
            }

            if (mUseDefault) {
                mKey += DEFAULT_SUFFIX
            }

            if (mMode == -1) {
                mMode = ContextWrapper.MODE_PRIVATE
            }

            initPrefs(mContext!!, mKey, mMode)
        }
    }



    // add new functions


    fun setFirebaseFCMToken(firebaseFCM: String?) {
        if (firebaseFCM != null) {
            putString("firebaseFCM", firebaseFCM)
        }

    }

    fun getFirebaseFCMToken(): String? {
        return preferences?.getString("firebaseFCM", "")
    }


    fun setUserEmail(userEmail: String?) {
        if (userEmail != null) {
            putString("userEmail", userEmail)
        }
    }

    fun getUserEmail(): String? {
        return preferences?.getString("userEmail","")
    }

    fun setUserPassowrd(userPassword: String?) {
        if (userPassword != null) {
            putString("userPassword", userPassword)
        }
    }

    fun getUserPassword(): String? {
        return preferences?.getString("userPassword","")
    }

    fun setUserFCM(userFCM: String?) {
        if (userFCM != null) {
            putString("userFCM", userFCM)
        }
    }

    fun getUserFCM(): String? {
        return preferences?.getString("userFCM","")
    }

    fun setUserAppId(userAppid: String?) {
        if (userAppid != null) {
            putString("userAppid", userAppid)
        }
    }

    fun getUserAppId(): String? {
        return preferences?.getString("userAppid","")
    }


    fun setEmpCode(code: String?) {
        if (code != null) {
            putString("emp_code", code)
        }
    }

    fun getEmpCode(): String? {
        return preferences?.getString("emp_code","")
    }
    fun setEmpName(code: String?) {
        if (code != null) {
            putString(Globals.EMP_NAME, code)
        }
    }

    fun getEmpName(): String? {
        return preferences?.getString(Globals.EMP_NAME,"")
    }
}