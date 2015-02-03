package com.visionappseestore.android.updater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdaterReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context ctx, final Intent intent) {
		Intent eventService = new Intent(ctx, UpdaterService.class);
		ctx.startService(eventService);
	}
}
