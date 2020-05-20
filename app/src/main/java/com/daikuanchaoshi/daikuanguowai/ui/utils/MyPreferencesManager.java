package com.daikuanchaoshi.daikuanguowai.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class MyPreferencesManager {
    private static SharedPreferences mSharedPreferences;
    private static Context mContext;

    public static void initPreferencesManager(Context context) {
        mContext = context;
        if (mSharedPreferences == null) {
            int mode = Context.MODE_PRIVATE;//不再使用Build.VERSION.SDK_INT >= 11 ? Context.MODE_MULTI_PROCESS : Context.MODE_WORLD_READABLE;
            mSharedPreferences = context.getSharedPreferences("daikuanguowai_Kreditterbang", mode);
        }
    }

    public static SharedPreferences newShareprefenceByName(Context context, String name) {
        mContext = context;
        int mode = Context.MODE_PRIVATE;//不再使用Build.VERSION.SDK_INT >= 11 ? Context.MODE_MULTI_PROCESS : Context.MODE_WORLD_READABLE;
        return context.getSharedPreferences(name, mode);
    }

    public static SharedPreferences getMySharedPreferences() {
        if (mSharedPreferences == null) {
            initPreferencesManager(mContext);
        }
        return mSharedPreferences;
    }

    public static SharedPreferences.Editor getMyEditor(SharedPreferences preferences) {
        return preferences.edit();
    }

    @NonNull
    public static String getString(String key, String defValue) {
        mSharedPreferences = getMySharedPreferences();
        return mSharedPreferences.getString(key, defValue);
    }

    public static boolean putString(String key, String value) {
        SharedPreferences.Editor editor = getMyEditor(getMySharedPreferences());
        editor.putString(key, value);
        return editor.commit();
    }
    public static boolean putLong(String key, long value) {
        SharedPreferences.Editor editor = getMyEditor(getMySharedPreferences());
        editor.putLong(key, value);
        return editor.commit();
    }
    public static Long getLong(String key, long defValue) {
        mSharedPreferences = getMySharedPreferences();
        return mSharedPreferences.getLong(key, defValue);
    }
    public static boolean putLogin(boolean isLogin){
        SharedPreferences.Editor editor = getMyEditor(getMySharedPreferences());
        editor.putBoolean("isLogin", isLogin);
        return editor.commit();
    }
    public static boolean getLogin(){
        mSharedPreferences = getMySharedPreferences();
        return mSharedPreferences.getBoolean("isLogin", false);
    }
    public static boolean putRegiste(boolean isLogin){
        SharedPreferences.Editor editor = getMyEditor(getMySharedPreferences());
        editor.putBoolean("Registe", isLogin);
        return editor.commit();
    }
    public static boolean getRegiste(){
        mSharedPreferences = getMySharedPreferences();
        return mSharedPreferences.getBoolean("Registe", false);
    }
}
