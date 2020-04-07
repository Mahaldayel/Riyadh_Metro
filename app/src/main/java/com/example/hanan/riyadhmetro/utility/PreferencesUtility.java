package com.example.hanan.riyadhmetro.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtility {

    public static final String AUTHORITY = "AUTHORITY";
    public static final int MONITOR_AUTHORITY = 3;
    public static final int ADMIN_AUTHORITY = 2;
    public static final int USER_AUTHORITY = 1;


    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*

     */
    public static void setAuthority(Context context, int AUTHORITY_TYPE) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(AUTHORITY, AUTHORITY_TYPE);
        editor.apply();
    }

    public static int getAuthority(Context context) {
        return getPreferences(context).getInt(AUTHORITY, 0);
    }


    public static void removePrefrences(Context context, String value) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove(value);
        editor.apply();
    }


}



