package it.visionapps.storevisionapps;

import android.app.Application;

/**
 * Created by iGio90 on 03/01/15.
 */
public class App extends Application {
    private static App mApp;
    private SettingsProvider mProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        mProvider = new SettingsProvider(this);
    }

    public static App getInstance() {
        return mApp;
    }

    public SettingsProvider getProvider() {
        return mProvider;
    }
}
