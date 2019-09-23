package com.izyver.gati.old.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import static com.izyver.gati.old.model.ApplicationData.FULL_SCHEDULE;

public class GatiPreferences {

    private static final String SHARED_PREFERENCES_NAME = "gati_preferences";

    private static final String KEY_SWITCH_SCHEDULE_STATE = "switch_schedule_button_state";
    private static final String KEY_TYPE_SCHEDULE = "schedule_type";

    public static void setSwitchScheduleButtonState(Context context, boolean isChecked){
        setBoolean(KEY_SWITCH_SCHEDULE_STATE, isChecked, context);
    }

    public static boolean getSwitchScheduleButtonState(Context context){
        return getBoolean(context, KEY_SWITCH_SCHEDULE_STATE, false);
    }


    public static void setTypeSchedule(Context context, int type){
        setInt(context, KEY_TYPE_SCHEDULE, type);
    }

    public static int getTypeSchedule(Context context){
        return getInt(context, KEY_TYPE_SCHEDULE, FULL_SCHEDULE);
    }


    //-------------------private getter/setter-------------------//

    private static int getInt(Context context, String key, int defValue){
        if (context == null) return defValue;
        return getPreferences(context).getInt(key, defValue);
    }

    private static void setInt(Context context, String key, int value){
        if (context == null) return;
        getEditor(context).putInt(key, value).apply();
    }


    private static boolean getBoolean(Context context, String key, boolean defValue){
        if (context == null) return defValue;
        return getPreferences(context).getBoolean(key, defValue);
    }

    private static void setBoolean(String key, boolean value, Context context) {
        if (context == null) return;
        getEditor(context).putBoolean(key, value).apply();
    }


    //-------------------internal logic-------------------//

    private static SharedPreferences.Editor getEditor(@NonNull Context context) {
        return getPreferences(context).edit();
    }

    private static SharedPreferences getPreferences(@NonNull Context context){
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}