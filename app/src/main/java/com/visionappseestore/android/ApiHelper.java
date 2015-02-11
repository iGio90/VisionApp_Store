package com.visionappseestore.android;

import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class ApiHelper {
    public static final String APP_LIST = "https://www.visionapps.it/app/api_v2/apps_list";
    public static final String APP_DETAILS = "https://www.visionapps.it/app/api_v2/apps_update";
    public static final String APP_PURCHASE_LIST = "https://www.visionapps.it/app/api_v2/payments_list";
    public static final String APP_DOWNLOADED = "https://www.visionapps.it/app/api_v2/payments_verify/free";
    public static final String APP_DOWNLOADED_PAYPAL =
            "https://www.visionapps.it/app/api_v2/payments_verify/paypal?paypal_sandbox=1";

    public static void purchaseAppFree(String appId) {
        parseApi(APP_DOWNLOADED + "?app_id=" + appId, new ApiHandler() {
            @Override
            public void onSuccess(JSONObject object) {
            }

            @Override
            public void onError(String e) {
            }
        });
    }

    public static void purchaseAppPaid(String paymentId) {
        parseApi(APP_DOWNLOADED + "&paypal_payment_id=" + paymentId, new ApiHandler() {
            @Override
            public void onSuccess(JSONObject object) {
            }

            @Override
            public void onError(String e) {
            }
        });
    }

    public static void parseApi(String api, ApiHandler handler) {
        ApiThread thread = new ApiThread(api, handler);
        thread.start();
    }

    private static class ApiThread extends Thread {
        private String mApiUrl;
        private ApiHandler mHandler;

        public ApiThread(String api, ApiHandler handler) {
            mApiUrl = api;
            mHandler = handler;
        }

        @Override
        public void run() {
            final SettingsProvider mProvider = App.getInstance().getProvider();
            String email = mProvider.getPref(SettingsProvider.USER_LOGGED_IN_EMAIL, "");
            String pass = mProvider.getPref(SettingsProvider.USER_LOGGED_IN_PASSWORD, "");

            AsyncHttpClient mClient = new AsyncHttpClient();
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocket sf = new SSLSocket(trustStore);
                sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                mClient.setSSLSocketFactory(sf);
            } catch (IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyStoreException | KeyManagementException e) {
                mHandler.onError(e.getMessage());
            }

            final String basicAuth = "Basic " + Base64.encodeToString((email + ":" + pass).getBytes(), Base64.NO_WRAP);
            mClient.addHeader("Authorization", basicAuth);

            mClient.post(mApiUrl + "?u=" + email + "&p=" + pass, new AsyncHttpResponseHandler() {
                @Override
                public void onFailure(Throwable error, String content) {
                    mHandler.onError(error.getMessage());
                }

                @Override
                public void onSuccess(String response) {
                    try {
                        mHandler.onSuccess(new JSONObject(response));
                    } catch (JSONException e) {
                        mHandler.onError(e.getMessage());
                    }
                }
            });
        }
    }
}