package com.gatisnau.gati;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

public class GatiPreferences {

    private static final String SHARED_PREFERENCES_NAME = "gati_preferences";

    private static final String KEY_SWITCH_SCHEDULE_STATE = "switch_schedule_button_state";

    public static void setSwitchScheduleButtonState(Context context, boolean isChecked){
        setBoolean(KEY_SWITCH_SCHEDULE_STATE, isChecked, context);
    }

    public static boolean getSwitchScheduleButtonState(Context context){
        return getBoolean(context, KEY_SWITCH_SCHEDULE_STATE, false);
    }





    private static boolean getBoolean(Context context, String key, boolean defValue){
        if (context == null) return defValue;
        return getPreferences(context).getBoolean(key, defValue);
    }

    private static void setBoolean(String key, boolean value, Context context) {
        if (context == null) return;
        getEditor(context).putBoolean(key, value).apply();
    }


    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    private static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}