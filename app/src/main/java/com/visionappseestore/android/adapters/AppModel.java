package com.visionappseestore.android.adapters;

/**
 * Created by iGio90 on 03/01/15.
 */
public class AppModel {
    private String mTitle;
    private String mIconUrl;

    public AppModel(String title, String icon) {
        mTitle = title;
        mIconUrl = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getIconUrl() {
        return mIconUrl;
    }
}
