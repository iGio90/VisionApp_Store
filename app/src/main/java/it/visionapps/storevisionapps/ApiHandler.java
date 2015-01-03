package it.visionapps.storevisionapps;

import org.json.JSONObject;

public interface ApiHandler {
	public void onSuccess(JSONObject object);
	public void onError(String e);
}
