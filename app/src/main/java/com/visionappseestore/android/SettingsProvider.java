package com.visionappseestore.android;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by iGio90 on 20/09/2014.
 */
public class SettingsProvider {
    public static final String PREFERENCES = "VisionAppPreferences";

    public static final String USER_LOGGED_IN_STATE = "user_logged_in_state";
    public static final String USER_LOGGED_IN_EMAIL = "user_logged_in_email";
    public static final String USER_LOGGED_IN_PASSWORD = "user_logged_in_password";

    private Context mContext;

    public SettingsProvider(Context context) {
        mContext = context;
    }

    public SharedPreferences get() {
        return mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public String getPref(String key, String def) {
        return get().getString(key, def);
    }

    public int getPref(String key, int def) {
        return get().getInt(key, def);
    }

    public boolean getPref(String key, boolean def) {
        return get().getBoolean(key, def);
    }

    public void putPref(String key, String value) {
        get().edit().putString(key, value).commit();
    }

    public void putPref(String key, int value) {
        get().edit().putInt(key, value).commit();
    }

    public void putPref(String key, boolean value) {
        get().edit().putBoolean(key, value).commit();
    }
}
