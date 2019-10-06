package com.izyver.gati.data.android

import android.content.Context
import android.content.SharedPreferences

private const val GATI_SHARED_PREFERENCES_NAME = "schedule_type_preferences"

class GatiPref(private val context: Context?) {

    private var preferences: SharedPreferences? = null
        get() {
            return if (field != null) field
            else context?.getSharedPreferences(GATI_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        }

    fun saveScheduleType(typeName: String) {
        if (preferences == null) return
        preferences!!.edit().putString(KEY_SCHEDULE_TYPE_NAME, typeName).apply()
    }

    fun getScheduleTypeName(): String?{
        if (preferences == null) return null
        return preferences!!.getString(KEY_SCHEDULE_TYPE_NAME, null)
    }
}

private const val KEY_SCHEDULE_TYPE_NAME = "schedule_type_name"