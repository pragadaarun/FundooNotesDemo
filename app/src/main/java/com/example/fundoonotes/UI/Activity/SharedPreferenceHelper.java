package com.example.fundoonotes.UI.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferenceHelper {
    private Context context;
    public static final String SHARED_PREFERENCES = "SharedPreferenceHelper";
    public static final String IS_LOGGED_IN = "isLoggedIn";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPreferenceHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setIsLoggedIn(boolean value) {
        editor  = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN,value);
        editor.apply();
    }

    public boolean getLoggedIN(){
        Log.d("Shared Preference","IS_LOGGED_IN value" + IS_LOGGED_IN);
        return sharedPreferences.getBoolean(IS_LOGGED_IN,false);
    }

}
