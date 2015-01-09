package it.visionapps.storevisionapps;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class ApiHelper {
    public static final String APP_LIST = "https://www.visionapps.it/app/api_v2/apps_list";
    public static final String APP_DETAILS = "https://www.visionapps.it/app/api_v2/apps_update";

    public static void parseApi(String api, ApiHandler handler) {
        ApiThread thread = new ApiThread(api, handler);
        thread.start();
        Log.e("bam", api);
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