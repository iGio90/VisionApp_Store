package it.visionapps.storevisionapps;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(getNoFallbackOptions())
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static App getInstance() {
        return mApp;
    }

    public SettingsProvider getProvider() {
        return mProvider;
    }

    public static DisplayImageOptions getNoFallbackOptions() {
        return new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
    }
}
