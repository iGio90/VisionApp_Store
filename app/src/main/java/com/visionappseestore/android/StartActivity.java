package com.visionappseestore.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;


public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsProvider mProvider = App.getInstance().getProvider();
        Intent intent = new Intent(StartActivity.this, mProvider.getPref(SettingsProvider.USER_LOGGED_IN_STATE, false) ?
                MainStoreActivity.class : LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
