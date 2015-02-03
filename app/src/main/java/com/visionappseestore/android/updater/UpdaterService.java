package com.visionappseestore.android.updater;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.visionappseestore.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdaterService extends Service {
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    private SharedPreferences prefs;

    private int CURRENT_VERSION;
    private int update;

    private File updateFile;

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

    @Override
    public void onCreate() {
        super.onCreate();

        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Aggiornamento See Store in download")
                .setSmallIcon(R.drawable.ic_menu_store);

        prefs = getSharedPreferences("prefs", MODE_MULTI_PROCESS);

        File baseDir = new File(
                Environment.getExternalStorageDirectory(),
                        "/SeeStore");
        if (!baseDir.exists())
            baseDir.mkdir();

        new checkForUpdate().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class checkForUpdate extends AsyncTask<String, Void, Void> {
        JSONObject json;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            PackageInfo pInfo;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                CURRENT_VERSION = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                CURRENT_VERSION = 0;
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            try {
                url = new URL(Config.CHECK_FILE);

                URLConnection connection = url.openConnection();

                String line;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                json = new JSONObject(builder.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {
                JSONObject responseObject = json.getJSONObject("responseData");
                JSONArray resultArray = responseObject.getJSONArray("results");

                JSONObject obj = resultArray.getJSONObject(0);
                update = obj.getInt("ultima_versione");

                Log.e("bam", "updated " + update);
                Log.e("bam", "current " + CURRENT_VERSION);

                updateFile = new File(Environment.getExternalStorageDirectory()
                        + "/SeeStore/SeeStore_" + update + ".apk");

                if (update > CURRENT_VERSION) {
                    if (updateFile.exists()) {
                        Log.e("bam", "installed update");
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(updateFile),
                                "application/vnd.android.package-archive");

                        PendingIntent pendingIntent = PendingIntent.getActivity(UpdaterService.this, 0, intent, 0);

                        mBuilder.setContentTitle("See Store update")
                                .setContentText("Tocca qui per installare")
                                .setProgress(0, 0, false)
                                .setContentIntent(pendingIntent);

                        prefs.edit().putInt("last", update).commit();

                        mNotifyManager.notify(1616, mBuilder.build());
                    } else {
                        Log.e("bam", "download");
                        final DownloadTask downloadTask = new DownloadTask(UpdaterService.this);
                        downloadTask.execute(Config.APK_FILE);
                    }
                }
            } catch (JSONException ignored) {
                Log.e("bam", ignored.toString());
            } catch (NullPointerException ignored) {
                Log.e("bam", ignored.toString());
            }
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        int id = 1616;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            mBuilder.setProgress(100, progress[0], false);
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();

            if (updateFile.exists()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(updateFile),
                        "application/vnd.android.package-archive");

                PendingIntent pendingIntent = PendingIntent.getActivity(UpdaterService.this, 0, intent, 0);

                mBuilder.setContentTitle("See Store update")
                        .setContentText("Tocca qui per installare")
                        .setProgress(0, 0, false)
                        .setContentIntent(pendingIntent);

                prefs.edit().putInt("last", update).commit();

                mNotifyManager.notify(id, mBuilder.build());
            } else {
                mBuilder.setContentTitle("See Store update")
                        .setContentText("Download non riuscito")
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
            }

            stopSelf();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("bam", "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory()
                        + "/SeeStore/SeeStore_" + update + ".apk");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}
