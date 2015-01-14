package it.visionapps.storevisionapps.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.visionapps.storevisionapps.ApiHandler;
import it.visionapps.storevisionapps.ApiHelper;
import it.visionapps.storevisionapps.MainStoreActivity;
import it.visionapps.storevisionapps.R;
import it.visionapps.storevisionapps.Utils;
import it.visionapps.storevisionapps.adapters.AppModel;

/**
 * Created by iGio90 on 14/01/15.
 */
public class UpdateService extends Service {
    private Handler mProcessHandler;

    private static final int NOTIFICATION_ID = 9947;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread ht = new HandlerThread("updater_handler");
        ht.start();
        mProcessHandler = new Handler(ht.getLooper());

        mProcessHandler.post(mCheckForUpdates);
    }

    private final Thread mCheckForUpdates = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    ApiHelper.parseApi(ApiHelper.APP_LIST, new ApiHandler() {
                        @Override
                        public void onSuccess(JSONObject object) {
                            final ArrayList<AppModel> models = new ArrayList<>();
                            try {
                                JSONArray data = object.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject model = data.getJSONObject(i);
                                    if (Utils.isAppInstalled(UpdateService.this, model.getString("app_packagename"))) {
                                        PackageInfo pInfo = getPackageManager().getPackageInfo(model.getString("app_packagename"), 0);
                                        if (pInfo.versionName.equals(model.getString("version_name"))) {
                                            models.add(new AppModel(
                                                    model.getString("app_name"),
                                                    model.getString("icon_url")
                                            ));
                                        }
                                    }
                                }


                                if (!models.isEmpty()) {
                                    if (models.size() == 1) {
                                        showSingleNotification(models.get(0));
                                    } else {
                                        showMultipleNotification(models.size());
                                    }
                                }
                            } catch (JSONException | PackageManager.NameNotFoundException ignored) {
                            }
                        }

                        @Override
                        public void onError(String e) {
                        }
                    });
                }
            }
    );

    private void showSingleNotification(AppModel model) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_seestore);
        mBuilder.setContentTitle(model.getTitle());
        mBuilder.setContentText("Nuovo aggiornamento disponibile!");
        Intent resultIntent = new Intent(this, MainStoreActivity.class);
        resultIntent.putExtra("appName", model.getTitle());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainStoreActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void showMultipleNotification(int size) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_seestore);
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText("Ci sono " + size + " aggiornamenti disponibili!");
        Intent resultIntent = new Intent(this, MainStoreActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainStoreActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
