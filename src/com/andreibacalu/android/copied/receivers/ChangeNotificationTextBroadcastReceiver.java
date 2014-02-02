package com.andreibacalu.android.copied.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.andreibacalu.android.copied.services.ChangeNotificationTextService;
import com.andreibacalu.android.copied.utils.SharedPreferencesUtil;

public class ChangeNotificationTextBroadcastReceiver extends BroadcastReceiver {

	private final String LOG_TAG = ChangeNotificationTextBroadcastReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "<<onReceive()");
		if (SharedPreferencesUtil.getInstance(context.getApplicationContext()).getSetting(SharedPreferencesUtil.SETTING_SERVICE_BOOT)) {
			Intent i = new Intent(context, ChangeNotificationTextService.class);
			context.startService(i);
		}
	}

}
