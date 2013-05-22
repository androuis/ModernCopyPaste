package com.andreibacalu.android.moderncopypaste.services;

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
import android.widget.RemoteViews;

import com.andreibacalu.android.moderncopypaste.R;

public class ChangeNotificationTextService extends Service {
	
	private static final String INTENT_COMMAND_TYPE = "type";
	private static final int INTENT_COMMAND_TYPE_PREVIOUS = -1;	
	private static final int INTENT_COMMAND_TYPE_NEXT = 1;	
	private static final int INTENT_COMMAND_TYPE_USE = 0;
	private ClipboardManager clipBoard;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		createNotif("test");
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
			createNotif("previous");
			break;
		case INTENT_COMMAND_TYPE_NEXT:
			createNotif("next");
			break;
			
		default:
			break;
		}
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
		
		/*createAction(rv, INTENT_COMMAND_TYPE_PREVIOUS);
		createAction(rv, INTENT_COMMAND_TYPE_NEXT);
		createAction(rv, INTENT_COMMAND_TYPE_USE);*/
		
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

	/*private void createAction(RemoteViews rv, int intentCommandType) {
		Intent intent = new Intent(this, ChangeNotificationTextService.class);
		intent.putExtra(INTENT_COMMAND_TYPE, intentCommandType);
		PendingIntent pIntent = PendingIntent.getService(getBaseContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		switch (intentCommandType) {
		case INTENT_COMMAND_TYPE_PREVIOUS:
			rv.setOnClickPendingIntent(R.id.text_previous, pIntent);
			break;
		case INTENT_COMMAND_TYPE_NEXT:
			rv.setOnClickPendingIntent(R.id.text_next, pIntent);
			break;
		case INTENT_COMMAND_TYPE_USE:
			//rv.setOnClickPendingIntent(R.id.text_next, pIntent);
			break;
		default:
			break;
		}
	}*/
	
	private class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener {
		
		@Override
		public void onPrimaryClipChanged() {
			ClipData clip = clipBoard.getPrimaryClip();
			Log.e("CLIP_DATA", clip.toString());
		}		
	}

}
