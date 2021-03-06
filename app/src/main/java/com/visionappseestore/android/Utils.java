package com.visionappseestore.android;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by iGio90 on 03/01/15.
 */
public class Utils {
    public static int dpToPx(float dp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }

    public static void showDialog(Context context, String title, String message, String okButton, View.OnClickListener listener) {
        final MaterialDialog mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(okButton, listener)
                .setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }
}
