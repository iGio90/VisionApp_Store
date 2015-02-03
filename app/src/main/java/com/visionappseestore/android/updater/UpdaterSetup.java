package com.visionappseestore.android.updater;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class UpdaterSetup extends BroadcastReceiver {
	@Override
	public void onReceive(final Context ctx, final Intent intent) {
		AlarmManager alarmManager = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(ctx, UpdaterReceiver.class);

		PendingIntent intentExecuted = PendingIntent.getBroadcast(ctx, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, 20);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				now.getTimeInMillis(), Config.EXEC_INTERVAL, intentExecuted);
	}

}
