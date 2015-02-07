package com.visionappseestore.android.controllers;

import com.visionappseestore.android.adapters.AppModel;

import java.util.ArrayList;

/**
 * Created by iGio90 on 06/02/15.
 */
public class UserPackageController {
    private final ArrayList<AppModel> mModels = new ArrayList<>();

    public UserPackageController() {

    }

    public void addModel(AppModel model) {
        synchronized (mModels) {
            mModels.add(model);
        }
    }

    public AppModel getModel(int id) {
        synchronized (mModels) {
            for (AppModel model : mModels) {
                if (model.getId() == id) {
                    return model;
                }
            }

            return null;
        }
    }
}
