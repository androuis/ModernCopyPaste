package com.andreibacalu.android.copied.services;

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
import android.widget.Toast;

import com.andreibacalu.android.copied.R;

public class ChangeNotificationTextService extends Service {
	private final String TAG_LOG = getClass().getName();

	private final String INTENT_COMMAND_TYPE = "type";
	private final int INTENT_COMMAND_TYPE_PREVIOUS = -1;
	private final int INTENT_COMMAND_TYPE_NEXT = 1;
	private final int INTENT_COMMAND_TYPE_COPY = 2;
	private final int INTENT_COMMAND_TYPE_CUT = -2;
	private final int INTENT_COMMAND_TYPE_UNKNOWN = 0;

	private final int NOTIFICATION_ID = 0;

	private final String DEFAULT_STRING = "You have no copied data yet";

	private ClipboardManager clipBoard;
	private List<String> clipboardStrings = new ArrayList<String>();
	private String currentSelectedString = "";
	private ClipboardListener clipBoardListener;
	private NotificationManager notificationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipBoardListener = new ClipboardListener();
		clipBoard.addPrimaryClipChangedListener(clipBoardListener);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		currentSelectedString = DEFAULT_STRING;
		createNotif(currentSelectedString);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private void handleCommand(Intent intent) {
		int command = INTENT_COMMAND_TYPE_UNKNOWN;
		if (intent != null) {
			command = intent.getIntExtra(INTENT_COMMAND_TYPE,
					INTENT_COMMAND_TYPE_UNKNOWN);
		}
		Log.i(TAG_LOG, "handleCommand: " + command);
		switch (command) {
		case INTENT_COMMAND_TYPE_PREVIOUS:
			createNotif(currentSelectedString = getStringInDirection(-1));
			break;
		case INTENT_COMMAND_TYPE_NEXT:
			createNotif(currentSelectedString = getStringInDirection(1));
			break;
		case INTENT_COMMAND_TYPE_COPY:
			clipBoard.removePrimaryClipChangedListener(clipBoardListener);
			clipBoard.setPrimaryClip(ClipData.newPlainText(
					getString(R.string.clipdata_user_visible_label),
					currentSelectedString));
			notificationManager.cancelAll();
			createNotif(currentSelectedString);
			clipBoard.addPrimaryClipChangedListener(clipBoardListener);
			break;
		case INTENT_COMMAND_TYPE_CUT:
			clipBoard.removePrimaryClipChangedListener(clipBoardListener);
			clipBoard.setPrimaryClip(ClipData.newPlainText(
					getString(R.string.clipdata_user_visible_label),
					currentSelectedString));
			int indexOfCurrentSelectedString = clipboardStrings
					.indexOf(currentSelectedString);
			clipboardStrings.remove(currentSelectedString);
			notificationManager.cancelAll();
			if (clipboardStrings.size() > 0) {
				currentSelectedString = clipboardStrings.size() > indexOfCurrentSelectedString ? clipboardStrings
						.get(indexOfCurrentSelectedString) : clipboardStrings
						.get(indexOfCurrentSelectedString - 1);
				createNotif(currentSelectedString);
			} else {
				createNotif(DEFAULT_STRING);
			}
			clipBoard.addPrimaryClipChangedListener(clipBoardListener);
			break;
		default:
			createNotif(clipboardStrings.size() > 0 ? clipboardStrings.get(0) : DEFAULT_STRING);
			break;
		}
	}

	private String getStringInDirection(int direction) {
		String computedString = DEFAULT_STRING;
		if (!currentSelectedString.equals(DEFAULT_STRING)
				&& clipboardStrings.contains(currentSelectedString)) {
			int indexOfCurrentSelectedString = clipboardStrings
					.indexOf(currentSelectedString);
			Log.d(TAG_LOG, String.valueOf(direction));
			Log.d(TAG_LOG, String.valueOf(indexOfCurrentSelectedString));
			Log.d(TAG_LOG, String.valueOf(clipboardStrings.size()));
			if (direction > 0) {
				if (indexOfCurrentSelectedString == clipboardStrings.size() - 1) {
					computedString = clipboardStrings.get(0);
				} else {
					computedString = clipboardStrings
							.get(indexOfCurrentSelectedString + 1);
				}
			} else if (direction < 0) {
				if (indexOfCurrentSelectedString == 0) {
					computedString = clipboardStrings.get(clipboardStrings
							.size() - 1);
				} else {
					computedString = clipboardStrings
							.get(indexOfCurrentSelectedString - 1);
				}
			}
		}
		Log.d(TAG_LOG, computedString);
		return computedString;
	}

	private void createNotif(String textString) {
		Log.d(TAG_LOG, ">>createNotif() " + textString);
		RemoteViews rv = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		rv.setTextViewText(R.id.text_body, textString);

		Intent intent = new Intent(this, ChangeNotificationTextService.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_PREVIOUS);
		PendingIntent pIntent = PendingIntent.getService(getBaseContext(),
				INTENT_COMMAND_TYPE_PREVIOUS, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.text_previous, pIntent);

		intent = new Intent(this, ChangeNotificationTextService.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_NEXT);
		pIntent = PendingIntent.getService(getBaseContext(),
				INTENT_COMMAND_TYPE_NEXT, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.text_next, pIntent);

		if (!DEFAULT_STRING.equals(textString)) {
			intent = new Intent(this, ChangeNotificationTextService.class);
			intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_COPY);
			pIntent = PendingIntent.getService(getBaseContext(),
					INTENT_COMMAND_TYPE_COPY, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.text_copy, pIntent);

			intent = new Intent(this, ChangeNotificationTextService.class);
			intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_CUT);
			pIntent = PendingIntent.getService(getBaseContext(),
					INTENT_COMMAND_TYPE_CUT, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.text_cut, pIntent);
		}

		if (clipboardStrings.size() < 2) {
			rv.setViewVisibility(R.id.text_next, View.GONE);
			rv.setViewVisibility(R.id.text_previous, View.GONE);
			if (clipboardStrings.size() == 1
					&& !DEFAULT_STRING.equals(clipboardStrings.get(0))) {
				rv.setViewVisibility(R.id.text_copy, View.VISIBLE);
				rv.setViewVisibility(R.id.text_cut, View.VISIBLE);
			} else {
				rv.setViewVisibility(R.id.text_copy, View.GONE);
				rv.setViewVisibility(R.id.text_cut, View.GONE);
				rv.setViewVisibility(R.id.text_actions, View.GONE);
			}
		} else {
			rv.setViewVisibility(R.id.text_next, View.VISIBLE);
			rv.setViewVisibility(R.id.text_previous, View.VISIBLE);
			rv.setViewVisibility(R.id.text_copy, View.VISIBLE);
			rv.setViewVisibility(R.id.text_cut, View.VISIBLE);
			rv.setViewVisibility(R.id.text_actions, View.VISIBLE);
		}

		// Build notification
		Notification notification = new NotificationCompat.Builder(this)
				.setContent(rv).setSmallIcon(R.drawable.ic_launcher).build();
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	private class ClipboardListener implements
			ClipboardManager.OnPrimaryClipChangedListener {

		@Override
		public void onPrimaryClipChanged() {
			ClipData clipData = clipBoard.getPrimaryClip();
			String clipText = "";
			if (clipData != null) {
				clipText = (String) clipData.getItemAt(0).getText();
				if (clipText != null) {
					if (clipboardStrings.size() == 0) {
						clipboardStrings.remove(DEFAULT_STRING);
					}
					if (!clipboardStrings.contains(clipText)) {
						clipboardStrings.add(currentSelectedString = clipText);
						createNotif(clipText);
						Toast.makeText(getApplicationContext(),
								getString(R.string.text_copied, clipText),
								Toast.LENGTH_SHORT).show();
					}
				}
			}
			Log.i(TAG_LOG, "<<onPrimaryClipChanged() - clipData: " + clipData
					+ " clipText: " + clipText);
		}
	}

}
