package it.visionapps.storevisionapps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import it.visionapps.storevisionapps.widgets.NexaTextView;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        NexaTextView button = (NexaTextView) findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (!(netInfo != null && netInfo.isConnectedOrConnecting())) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("È necessario essere connessi ad internet per eseguire l'operazione")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }}).create().show();
                    return;
                }

                final String login_email = ((EditText)findViewById(R.id.login_email)).getText().toString();
                final String login_password = ((EditText)findViewById(R.id.login_password)).getText().toString();

                final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
                progress.setMessage("Login in corso...");
                progress.show();

                StringEntity jsonReqStrEnt = null;
                try {
                    JSONObject jsonReq = new JSONObject();
                    jsonReq.put("device_number",Build.SERIAL);
                    jsonReqStrEnt = new StringEntity(jsonReq.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AsyncHttpClient client = new AsyncHttpClient();
                client.setBasicAuth(login_email,login_password);
                client.post(
                        v.getContext(),
                        "http://www.visionapps.it/app/api_v1/user_testcredentials?u="+login_email+"&p="+login_password,
                        jsonReqStrEnt,
                        "application/json",
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onFailure(Throwable error, String content) {
                                try {
                                    if (progress.isShowing())
                                        progress.dismiss();
                                } catch (IllegalArgumentException ignored) {}

                                new AlertDialog.Builder(LoginActivity.this)
                                        .setMessage("Login Fallito, verificare l'email e la password inserite")
                                        .setCancelable(true)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }}).create().show();

                            }
                            @Override
                            public void onSuccess(String response) {
                                try {
                                    if (progress.isShowing())
                                        progress.dismiss();
                                } catch (IllegalArgumentException ignored) {}

                                SettingsProvider mProvider = App.getInstance().getProvider();
                                mProvider.putPref(SettingsProvider.USER_LOGGED_IN_STATE, true);
                                mProvider.putPref(SettingsProvider.USER_LOGGED_IN_EMAIL, login_email);
                                mProvider.putPref(SettingsProvider.USER_LOGGED_IN_PASSWORD, login_password);

                                Intent i = new Intent(getApplicationContext(), StartActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        });
            }
        });

        TextView login_create = (TextView)findViewById(R.id.login_create);
        login_create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RegisterActivity.class));
            }
        });

        TextView login_changepassword = (TextView)findViewById(R.id.login_changepassword);
        login_changepassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ChangePasswordActivity.class));
            }
        });

    }
}