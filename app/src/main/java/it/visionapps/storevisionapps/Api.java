package it.visionapps.storevisionapps;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

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

public class Api {

    public static final String APP_LIST = "https://www.visionapps.it/app/api_v2/apps_list";

    public static void parseApi(String api, ApiHandler handler) {
        final SettingsProvider mProvider = App.getInstance().getProvider();
        String email = mProvider.getPref(SettingsProvider.USER_LOGGED_IN_EMAIL, "");
        String pass = mProvider.getPref(SettingsProvider.USER_LOGGED_IN_PASSWORD, "");

        InputStream is = null;
        String json = null;
        api += "?u=" + email + "&p=" + pass;
        try {
            HttpClient httpClient = getSslClient();
            HttpGet httpGet = new HttpGet(api);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (IOException e) {
            handler.onError(e);
        }
        try {
            assert is != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            handler.onError(e);
        }
        try {
            handler.onSuccess(new JSONObject(json));
        } catch (JSONException e) {
            handler.onError(e);
        }
    }

    public static HttpClient getSslClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        return new DefaultHttpClient(conMgr, params);
    }
}