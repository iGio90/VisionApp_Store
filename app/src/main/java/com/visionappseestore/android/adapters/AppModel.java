package com.visionappseestore.android.adapters;

/**
 * Created by iGio90 on 03/01/15.
 */
public class AppModel {
    private int mId;
    private String mTitle;
    private String mIconUrl;

    public AppModel(int id, String title, String icon) {
        mId = id;
        mTitle = title;
        mIconUrl = icon;
    }

    public int getId() { return mId; }
    public String getTitle() {
        return mTitle;
    }

    public String getIconUrl() {
        return mIconUrl;
    }
}
