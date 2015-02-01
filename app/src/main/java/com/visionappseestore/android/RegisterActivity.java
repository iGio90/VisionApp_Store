package com.visionappseestore.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_register);

        final RegisterActivity _activity = this;

        // popup privacy
        final TextView privacyText = (TextView)findViewById(R.id.register_linkprivacy);
        privacyText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ScrollView sv = new ScrollView(_activity);
                TextView tv = new TextView(_activity);
                tv.setText(Html.fromHtml(
                        getResources().getString(R.string.html_privacy)));
                sv.addView(tv);
                FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)tv.getLayoutParams();
                p.setMargins(20,0,20,0);
                tv.setLayoutParams(p);
                new AlertDialog.Builder(_activity)
                        .setView(sv)
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }}).create().show();
            }
        });

        // popup privacy
        final TextView terminiText = (TextView)findViewById(R.id.register_linktermini);
        terminiText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ScrollView sv = new ScrollView(_activity);
                TextView tv = new TextView(_activity);
                tv.setText(Html.fromHtml(
                        getResources().getString(R.string.html_termini)));
                sv.addView(tv);
                FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)tv.getLayoutParams();
                p.setMargins(20,0,20,0);
                tv.setLayoutParams(p);
                new AlertDialog.Builder(_activity)
                        .setView(sv)
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }}).create().show();
            }
        });


        final ProgressDialog progress = new ProgressDialog(this);


        Button button = (Button)findViewById(R.id.register_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (!(netInfo != null && netInfo.isConnectedOrConnecting())) {
                    new AlertDialog.Builder(_activity)
                            .setMessage("È necessario essere connessi ad internet per eseguire l'operazione")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }}).create().show();
                    return;
                }

                // check password
                if (!((EditText)findViewById(R.id.register_password)).getText().toString().equals(
                        ((EditText)findViewById(R.id.register_password1)).getText().toString()
                )){
                    new AlertDialog.Builder(_activity)
                            .setMessage("Le password inserite devono coincidere")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }}).create().show();
                    return;
                }

                // check email
                if (!((EditText)findViewById(R.id.register_email)).getText().toString().equals(
                        ((EditText)findViewById(R.id.register_email1)).getText().toString()
                )){
                    new AlertDialog.Builder(_activity)
                            .setMessage("Gli indirizzi email inseriti devono coincidere")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }}).create().show();
                    return;
                }

                if (((EditText)findViewById(R.id.register_piva)).getText().toString().length()!=11){
                    new AlertDialog.Builder(_activity)
                            .setMessage("Lunghezza campo Partita Iva non valida")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }}).create().show();
                    return;
                }

                // check privacy
                if (!((CheckBox)findViewById(R.id.register_privacy)).isChecked()){
                    new AlertDialog.Builder(_activity)
                            .setMessage("È necessario accettare i termini di servizio e le norme sulla privacy")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }}).create().show();
                    return;
                }

                try {
                    JSONObject jsonReq = new JSONObject();

                    jsonReq.put("email",((EditText)findViewById(R.id.register_email)).getText().toString());
                    jsonReq.put("password",((EditText)findViewById(R.id.register_password)).getText().toString());
                    jsonReq.put("name",((EditText)findViewById(R.id.register_nome)).getText().toString());
                    jsonReq.put("surname",((EditText)findViewById(R.id.register_cognome)).getText().toString());
                    jsonReq.put("job",((Spinner)findViewById(R.id.register_job)).getSelectedItem().toString());
                    jsonReq.put("company",((EditText)findViewById(R.id.register_ragionesociale)).getText().toString());
                    jsonReq.put("country",((EditText)findViewById(R.id.register_stato)).getText().toString());
                    jsonReq.put("city",((EditText)findViewById(R.id.register_citta)).getText().toString());
                    jsonReq.put("address",((EditText)findViewById(R.id.register_via)).getText().toString());
                    jsonReq.put("post_code",((EditText)findViewById(R.id.register_cap)).getText().toString());
                    jsonReq.put("street_number",((EditText)findViewById(R.id.register_numerocivico)).getText().toString());
                    jsonReq.put("province",((EditText)findViewById(R.id.register_provincia)).getText().toString());
                    jsonReq.put("p_iva",((EditText)findViewById(R.id.register_piva)).getText().toString());

                    StringEntity jsonReqStrEnt;
                    jsonReqStrEnt = new StringEntity(jsonReq.toString());

                    progress.setMessage("Invio...");
                    progress.show();

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(
                            v.getContext(),
                            "http://www.visionapps.it/app/api_v1/user_register",
                            jsonReqStrEnt,
                            "application/json",
                            new AsyncHttpResponseHandler() {
                                @Override
                                public void onFailure(Throwable error, String content) {

                                    new AlertDialog.Builder(_activity)
                                            .setMessage("Errore")
                                            .setCancelable(true)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }}).create().show();

                                    progress.dismiss();
                                }
                                @Override
                                public void onSuccess(String response) {
                                    progress.dismiss();
                                    try {
                                        JSONObject jsonRes = new JSONObject(response);
                                        if (jsonRes.getInt("error_code") == 0){

                                            new AlertDialog.Builder(_activity)
                                                    .setMessage("Registrazione avvenuta con successo")
                                                    .setCancelable(true)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            _activity.finish();
                                                        }}).create().show();

                                        } else {

                                            new AlertDialog.Builder(_activity)
                                                    .setMessage(jsonRes.getString("message"))
                                                    .setCancelable(true)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                        }}).create().show();

                                        }
                                    } catch (JSONException e) {

                                        new AlertDialog.Builder(_activity)
                                                .setMessage("Errore")
                                                .setCancelable(true)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }}).create().show();

                                        e.printStackTrace();
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.getStackTrace();
                }

            }
        });
    }
}