package com.andreibacalu.android.moderncopypaste.services;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.andreibacalu.android.moderncopypaste.R;

public class ChangeNotificationTextService extends Service {
	
	private static final String INTENT_COMMAND_TYPE = "type";
	private static final int INTENT_COMMAND_TYPE_PREVIOUS = -1;	
	private static final int INTENT_COMMAND_TYPE_NEXT = 1;	
	private static final int INTENT_COMMAND_TYPE_USE = 0;
	
	private static final String DEFAULT_STRING = "You have no copied data yet";
	
	private ClipboardManager clipBoard;
	private List<String> clipboardStrings = new ArrayList<String>();
	private String currentSelectedString = "";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		currentSelectedString = DEFAULT_STRING;
		createNotif(currentSelectedString);
		clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		clipBoard.addPrimaryClipChangedListener(new ClipboardListener());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void handleCommand(Intent intent) {
		int command = intent.getIntExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_USE);
		switch (command) {
		case INTENT_COMMAND_TYPE_PREVIOUS:
			createNotif(currentSelectedString = getStringInDirection(-1));
			break;
		case INTENT_COMMAND_TYPE_NEXT:
			createNotif(currentSelectedString = getStringInDirection(1));
			break;
			
		default:
			break;
		}
	}

	private String getStringInDirection(int direction) {
		String computedString = DEFAULT_STRING;
		if (!currentSelectedString.equals(DEFAULT_STRING) && clipboardStrings.contains(currentSelectedString)) {
			int indexOfCurrentSelectedString = clipboardStrings.indexOf(currentSelectedString);
			Log.e("MYAPP", String.valueOf(direction));
			Log.e("MYAPP", String.valueOf(indexOfCurrentSelectedString));
			Log.e("MYAPP", String.valueOf(clipboardStrings.size()));
			if (direction > 0) {
				if (indexOfCurrentSelectedString == clipboardStrings.size() - 1) {
					computedString = clipboardStrings.get(0);
				} else {
					computedString = clipboardStrings.get(indexOfCurrentSelectedString + 1);
				}
			} else if (direction < 0) {
				if (indexOfCurrentSelectedString == 0) {
					computedString = clipboardStrings.get(clipboardStrings.size() - 1);
				} else {
					computedString = clipboardStrings.get(indexOfCurrentSelectedString - 1);
				}
			}
		}
		Log.e("MYAPP", computedString);
		return computedString;
	}

	private void createNotif(String textString) {		
		RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification_layout);
		rv.setTextViewText(R.id.text_body, textString);
		
		Intent intent = new Intent(this, ChangeNotificationTextService.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_PREVIOUS);
		PendingIntent pIntent = PendingIntent.getService(getBaseContext(), INTENT_COMMAND_TYPE_PREVIOUS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.text_previous, pIntent);
		
		intent = new Intent(this, ChangeNotificationTextService.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_NEXT);
		pIntent = PendingIntent.getService(getBaseContext(), INTENT_COMMAND_TYPE_NEXT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.text_next, pIntent);
		
		if (clipboardStrings.size() < 2) {
			rv.setViewVisibility(R.id.text_next, View.INVISIBLE);
			rv.setViewVisibility(R.id.text_previous, View.INVISIBLE);
		} else {
			rv.setViewVisibility(R.id.text_next, View.VISIBLE);
			rv.setViewVisibility(R.id.text_previous, View.VISIBLE);
		}
		
		// Build notification
		// Actions are just fake
		Notification noti = new NotificationCompat.Builder(this)
			.setContent(rv)
			.setSmallIcon(R.drawable.ic_launcher)	
			.build();
		  
		NotificationManager notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, noti); 
	}
	
	private class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener {
		
		@Override
		public void onPrimaryClipChanged() {
			ClipData clip = clipBoard.getPrimaryClip();
			String clipData = "";
			if (clip != null) {
				clipData = (String) clip.getItemAt(0).getText();
				if (clipData != null) {
					clipboardStrings.remove(DEFAULT_STRING);
					clipboardStrings.add(currentSelectedString = clipData);
					createNotif(clipData);					
				}
			}
		}		
	}

}
