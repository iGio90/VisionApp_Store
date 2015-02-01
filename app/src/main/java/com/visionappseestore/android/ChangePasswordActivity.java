package com.visionappseestore.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.visionappseestore.android.widgets.NexaTextView;

public class ChangePasswordActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_change_password);

		final ProgressDialog progress = new ProgressDialog(this);
		final ChangePasswordActivity activity = this;
		
		NexaTextView button = (NexaTextView)findViewById(R.id.changepassword_button);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnectedOrConnecting())) {
	      	new AlertDialog.Builder(activity)
	      	.setMessage("È necessario essere connessi ad internet per eseguire l'operazione")
	      	.setCancelable(true)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int id) {
					  	dialog.dismiss();
					  }}).create().show();
	      	return;
        }

      	try {
        	JSONObject jsonReq = new JSONObject();

	        jsonReq.put("email",((EditText)findViewById(R.id.changepassword_email)).getText().toString());
	      	
	      	StringEntity jsonReqStrEnt;
		        jsonReqStrEnt = new StringEntity(jsonReq.toString());
		        
		        progress.setMessage("Invio...");
		        progress.show();
		        
			      AsyncHttpClient client = new AsyncHttpClient();
			      client.post(
			      		v.getContext(),
			      		"http://www.visionapps.it/app/api_v1/user_changepassword",
			      		jsonReqStrEnt,
			      		"application/json",
			      		new AsyncHttpResponseHandler() {
						      @Override
						      public void onFailure(Throwable error, String content) {
						      	progress.dismiss();
						      	
						      	new AlertDialog.Builder(activity)
						      	.setMessage("Errore")
						      	.setCancelable(true)
										.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
										  public void onClick(DialogInterface dialog, int id) {
										  	dialog.dismiss();
										  }}).create().show();

						      }
						      @Override
						      public void onSuccess(String response) {
						      	progress.dismiss();
						      	try {
	                    JSONObject jsonRes = new JSONObject(response);
	                    
	                    if (jsonRes.getInt("error_code") == 0){
	                    	
	        			      	new AlertDialog.Builder(activity)
	        			      	.setMessage("Email per il reset della password inviata")
	        			      	.setCancelable(true)
	        							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        							  public void onClick(DialogInterface dialog, int id) {
	        							  	dialog.dismiss();
	        							  }}).create().show();
	        			      	
	                    } else {
	                    	
	        			      	new AlertDialog.Builder(activity)
	        			      	.setMessage(jsonRes.getString("message"))
	        			      	.setCancelable(true)
	        							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        							  public void onClick(DialogInterface dialog, int id) {
	        							  	dialog.dismiss();
	        							  }}).create().show();
	        			      	
	                    }
	                    
                    } catch (JSONException e) {
                    	
                    	new AlertDialog.Builder(activity)
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
        	Log.d("robb", e.getMessage());
        }
				
			}
		});
	}
}
