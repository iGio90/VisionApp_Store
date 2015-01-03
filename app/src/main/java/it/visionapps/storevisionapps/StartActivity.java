package it.visionapps.storevisionapps;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsProvider mProvider = App.getInstance().getProvider();

        Intent intent = new Intent(this, mProvider.getPref(SettingsProvider.USER_LOGGED_IN_STATE, false) ?
                MainStoreActivity.class : LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
