package com.visionappseestore.android.updater;

/**
 * Created by giova_000 on 29/05/2014.
 */
public class Config {

    public static final String CHECK_FILE = "https://raw.githubusercontent.com/iGio90/misc/master/check";
    public static final String APK_FILE = "http://www.visionapps.it/see_store/See_store.apk";

    // il primo valore equivale alle ore. di default il check viene effettuato ogni 6 ore
    public static final int EXEC_INTERVAL = 3 * 60 * 60 * 1000;
}
