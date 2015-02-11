package com.visionappseestore.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import de.madcyph3r.materialnavigationdrawer.MaterialNavigationDrawer;
import de.madcyph3r.materialnavigationdrawer.MaterialNavigationDrawerListener;
import de.madcyph3r.materialnavigationdrawer.item.MaterialHeadItem;
import de.madcyph3r.materialnavigationdrawer.menu.MaterialDevisor;
import de.madcyph3r.materialnavigationdrawer.menu.MaterialMenu;
import de.madcyph3r.materialnavigationdrawer.menu.MaterialSection;
import de.madcyph3r.materialnavigationdrawer.tools.RoundedCornersDrawable;

import com.visionappseestore.android.adapters.AppModel;
import com.visionappseestore.android.controllers.UserPackageController;
import com.visionappseestore.android.service.UpdateService;
import com.visionappseestore.android.updater.UpdaterSetup;

/**
 * Created by iGio90 on 03/01/15.
 */
public class MainStoreActivity extends MaterialNavigationDrawer implements MaterialNavigationDrawerListener {
    private MaterialNavigationDrawer mDrawer;

    private Handler mProcessHandler;
    private Handler mDownloadHandler;

    public StoreFragment mStoreFragment;

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "AfhUslDRAv-FRGx6hArFCTxFzpkgaLfAjeFe4CafxX8uS_H-Y9yO8SRQlJdP5_s54iaNkSRAGVmfZrrs";
    private static final int REQUEST_CODE_PAYMENT = 1;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);

    private ProgressDialog mProgress;

    private String mCurrentAppId = "";
    private String mCurrentUrl = "";
    private String mCurrentAppName = "";
    private String mCurrentAppVersion = "";

    private UserPackageController mPackageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HandlerThread ht = new HandlerThread("store_handler");
        ht.start();
        mProcessHandler = new Handler(ht.getLooper());

        HandlerThread dl = new HandlerThread("download_handler");
        dl.start();
        mDownloadHandler = new Handler(dl.getLooper());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mProgress = new ProgressDialog(this);
        mPackageController = new UserPackageController();
        checkPurchasedApps();

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
        startService(new Intent(this, UpdateService.class));
        sendBroadcast(new Intent(this, UpdaterSetup.class));
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent data = getIntent();
        if (data.getStringExtra("appName") != null) {
            mStoreFragment.setCurrentApp(data.getStringExtra("appName"));
        }
    }

    public void setCurrentApp(String appName) {
        mStoreFragment.setCurrentApp(appName);
    }

    public Handler getProcessHandler() {
        return mProcessHandler;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mDrawer = this;
        setDrawerDPWidth(300);

        mStoreFragment = new StoreFragment();

        MaterialMenu mMenu = new MaterialMenu();
        MaterialSection mStore = newSection(getString(R.string.store), this.getResources().getDrawable(R.drawable.ic_homepage_store), mStoreFragment, false);
        MaterialSection mSettings = newSection(getString(R.string.settings), new SettingsFragment(), true);

        mMenu.getSections().add(mStore);
        mMenu.getSections().add(new MaterialDevisor());
        mMenu.getSections().add(mSettings);

        final Bitmap mHeadIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_store);
        final RoundedCornersDrawable drawableAppIcon = new RoundedCornersDrawable(getResources(), mHeadIcon);

        MaterialHeadItem mHeadItem = new MaterialHeadItem("", "", drawableAppIcon, getResources().getDrawable(R.drawable.sfondo_menu_store), mMenu, 0);
        mHeadItem.setCloseDrawerOnChanged(true);
        addHeadItem(mHeadItem);

        this.setOnChangedListener(this);
    }

    @Override
    public void onBeforeChangedHeadItem(MaterialHeadItem newHeadItem) {

    }

    @Override
    public void onAfterChangedHeadItem(MaterialHeadItem newHeadItem) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        JSONObject client = confirm.toJSONObject();
                        JSONObject response = client.getJSONObject("response");
                        String state = response.getString("state");
                        final String id = response.getString("id");

                        Log.e("bam", response.toString());

                        if (state.equals("approved")) {
                            ApiHelper.purchaseAppPaid(id);
                        }
                    } catch (JSONException ignored) {
                    }
                }
            }
        }
    }

    public void downloadApp(final String appId,
                            final String url, final String name, final String version) {
        Utils.showDialog(this, "Condizioni", "bla bla bla", "Accetto", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setTitle(name);
                mProgress.setMessage("Downloading...");
                mProgress.setCancelable(false);
                mProgress.show();

                ApiHelper.purchaseAppFree(appId);

                mDownloadHandler.post(new DownloadThread(url, name, version));
            }
        });
    }

    public void buyItem(String id, String price, String url, String name, String version) {
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE, price, id);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        mCurrentAppId = id;
        mCurrentUrl = url;
        mCurrentAppName = name;
        mCurrentAppVersion = version;
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent, String price, String appId) {
        return new PayPalPayment(new BigDecimal(price), "EUR", appId,
                paymentIntent);
    }

    @Override
    public void onBackPressed() {
        if (mStoreFragment != null &&
                mStoreFragment.isInDetails()) {
            mStoreFragment.leaveDetails();
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count <= 1) {
                finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    class DownloadThread extends Thread {
        String mUrl;
        String mAppName;
        String mVersion;

        public DownloadThread(String url, String name, String version) {
            mUrl = url;
            mAppName = name;
            mVersion = version;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(mUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                File file = new File(App.getInstance().getFilesDir(), mAppName + "_" + mVersion + ".apk");
                file.setReadable(true, false);

                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();

                int totalSize = urlConnection.getContentLength();
                int downloadedSize = 0;

                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0 ) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    updateProgress(file, downloadedSize, totalSize);
                }
                fileOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateProgress(final File file, final int currentSize, final int totalFileSize) {
        MainStoreActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentSize == totalFileSize) {
                    mProgress.cancel();
                } else {
                    if (!mProgress.isShowing())
                        mProgress.show();
                }
            }
        });

        if (currentSize == totalFileSize) {
            file.setReadable(true, false);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void checkPurchasedApps() {
        ApiHelper.parseApi(ApiHelper.APP_PURCHASE_LIST, new ApiHandler() {
            @Override
            public void onSuccess(JSONObject object) {
                try {
                    JSONArray data = object.getJSONArray("data");
                    for (int i=0;i<=data.length();i++) {
                        JSONObject appData = (JSONObject) data.get(i);
                        mPackageController.addModel(new AppModel(
                                Integer.parseInt(appData.getString("app_id")),
                                appData.getString("app_name"),
                                appData.getString("icon_url")
                        ));
                    }
                } catch (JSONException e) {
                    onError("");
                }
            }

            @Override
            public void onError(String e) {
                new AlertDialog.Builder(MainStoreActivity.this)
                        .setMessage("Errore nella verifica dei pacchetti acquistati. Si prega di riprovare in seguito!")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create().show();
            }
        });
    }

    public UserPackageController getPackageController() {
        return mPackageController;
    }
}
