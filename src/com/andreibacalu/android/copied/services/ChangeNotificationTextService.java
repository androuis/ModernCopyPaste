package com.andreibacalu.android.copied.services;

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
import com.andreibacalu.android.copied.activities.MainActivity;
import com.andreibacalu.android.copied.activities.TransparentActivity;
import com.andreibacalu.android.copied.application.CopiedApplication;
import com.andreibacalu.android.copied.utils.SharedPreferencesUtil;

public class ChangeNotificationTextService extends Service {
	private final String TAG_LOG = getClass().getName();

	public final static String INTENT_COMMAND_TYPE = "type";
	public final static String INTENT_REMOVE_POSITION = "position";

	private final int INTENT_COMMAND_TYPE_PREVIOUS = -1;
	private final int INTENT_COMMAND_TYPE_NEXT = 1;
	public final static int INTENT_COMMAND_TYPE_COPY = 2;
	private final int INTENT_COMMAND_TYPE_CUT = -2;
	public final static int INTENT_COMMAND_TYPE_UNKNOWN = 0;
	private final int INTENT_COMMAND_TYPE_API_18_ERROR_CASE = Integer.MAX_VALUE;
	public final static int INTENT_COMMAND_TYPE_CHANGE_NOTIF = 10;
	public final static int INTENT_COMMAND_TYPE_OPEN_ACTIVITY = 11;
	public final static int INTENT_COMMAND_TYPE_REMOVE = 12;

	public final static int NOTIFICATION_ID = 0;
	private final int MAX_TOAST_TEXT_LENGHT = 50;

	private ClipboardManager clipBoard;
	private ClipboardListener clipBoardListener;
	private NotificationManager notificationManager;
	
	private Intent cutPerformedIntent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		cutPerformedIntent = new Intent(getString(R.string.action_cut_performed));
		clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		if (Integer.valueOf(android.os.Build.VERSION.SDK) != 18) {
			clipBoardListener = new ClipboardListener();
			clipBoard.addPrimaryClipChangedListener(clipBoardListener);
		}
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		createNotif(CopiedApplication.getCurrentSelectedString(), false);
	}

	@Override
	public void onDestroy() {
		notificationManager.cancelAll();
		notificationManager = null;
		clipBoard.removePrimaryClipChangedListener(clipBoardListener);
		clipBoard = null;
		clipBoardListener = null;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private void handleCommand(Intent intent) {
		int indexOfCurrentSelectedString, numberOfTextsInClipboard;
		int command = INTENT_COMMAND_TYPE_UNKNOWN;
		if (intent != null) {
			command = intent.getIntExtra(INTENT_COMMAND_TYPE,
					INTENT_COMMAND_TYPE_UNKNOWN);
		}
		Log.i(TAG_LOG, "handleCommand: " + command);
		switch (command) {
		case INTENT_COMMAND_TYPE_PREVIOUS:
			CopiedApplication
					.setCurrentSelectedString(getStringInDirection(-1));
			createNotif(CopiedApplication.getCurrentSelectedString(), false);
			break;
		case INTENT_COMMAND_TYPE_NEXT:
			CopiedApplication.setCurrentSelectedString(getStringInDirection(1));
			createNotif(CopiedApplication.getCurrentSelectedString(), false);
			break;
		case INTENT_COMMAND_TYPE_COPY:
			clipBoard.removePrimaryClipChangedListener(clipBoardListener);
			try {
				clipBoard.setPrimaryClip(ClipData.newPlainText(
						getString(R.string.clipdata_user_visible_label),
						CopiedApplication.getCurrentSelectedString()));
			} catch (Exception e) {
				Log.e(TAG_LOG, e.getMessage());
			}
			createNotif(CopiedApplication.getCurrentSelectedString(), true);
			clipBoard.addPrimaryClipChangedListener(clipBoardListener);
			displayAddedToClipboardToast();
			break;
		case INTENT_COMMAND_TYPE_CUT:
			clipBoard.removePrimaryClipChangedListener(clipBoardListener);
			try {
				clipBoard.setPrimaryClip(ClipData.newPlainText(
						getString(R.string.clipdata_user_visible_label),
						CopiedApplication.getCurrentSelectedString()));
			} catch (Exception e) {
				Log.e(TAG_LOG, e.getMessage());
			}
			indexOfCurrentSelectedString = CopiedApplication
					.removeStringFromClipboard(CopiedApplication
							.getCurrentSelectedString());
			numberOfTextsInClipboard = CopiedApplication
					.getNumberOfTextsInClipboard();
			if (numberOfTextsInClipboard > 0) {
				CopiedApplication
						.setCurrentSelectedString(numberOfTextsInClipboard > indexOfCurrentSelectedString ? CopiedApplication
								.getClipboarString(indexOfCurrentSelectedString)
								: CopiedApplication
										.getClipboarString(indexOfCurrentSelectedString - 1));
				createNotif(CopiedApplication.getCurrentSelectedString(), true);
			} else {
				createNotif(CopiedApplication.getCurrentSelectedString(), true);
			}
			sendBroadcast(cutPerformedIntent);
			clipBoard.addPrimaryClipChangedListener(clipBoardListener);
			displayAddedToClipboardToast();
			break;
		case INTENT_COMMAND_TYPE_API_18_ERROR_CASE:
			if (clipBoardListener == null) {
				clipBoardListener = new ClipboardListener();
				clipBoard.addPrimaryClipChangedListener(clipBoardListener);
			}
			createNotif(CopiedApplication.getCurrentSelectedString(), false);
			break;
		case INTENT_COMMAND_TYPE_CHANGE_NOTIF:
			createNotif(CopiedApplication.getCurrentSelectedString(), true);
			break;
		case INTENT_COMMAND_TYPE_OPEN_ACTIVITY:
			createNotif(CopiedApplication.getCurrentSelectedString(), false);
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			break;
		case INTENT_COMMAND_TYPE_REMOVE:
			int positionToBeRemoved = intent.getIntExtra(
					INTENT_REMOVE_POSITION, -1);
			if (positionToBeRemoved != -1) {
				String stringToBeRemoved = CopiedApplication
						.getClipboarString(positionToBeRemoved);
				CopiedApplication.removeStringFromClipboard(stringToBeRemoved);
				if (CopiedApplication.getCurrentSelectedString().equals(
						stringToBeRemoved)) {
					numberOfTextsInClipboard = CopiedApplication
							.getNumberOfTextsInClipboard();
					if (numberOfTextsInClipboard > 0) {
						CopiedApplication
								.setCurrentSelectedString(numberOfTextsInClipboard > positionToBeRemoved ? CopiedApplication
										.getClipboarString(positionToBeRemoved)
										: CopiedApplication
												.getClipboarString(positionToBeRemoved - 1));
					}
				}
				createNotif(CopiedApplication.getCurrentSelectedString(), true);
			}
			break;
		}
	}

	private void displayAddedToClipboardToast() {
		Toast.makeText(getApplicationContext(),
				getString(R.string.added_to_clipboard), Toast.LENGTH_SHORT)
				.show();
	}

	private String getStringInDirection(int direction) {
		String computedString = CopiedApplication.DEFAULT_STRING;
		int indexOfCurrentSelectedString = -1;
		if ((indexOfCurrentSelectedString = CopiedApplication
				.getPositionOfCurrentSelectedString()) != -1) {
			Log.d(TAG_LOG, String.valueOf(direction));
			Log.d(TAG_LOG, String.valueOf(indexOfCurrentSelectedString));
			Log.d(TAG_LOG, String.valueOf(CopiedApplication
					.getNumberOfTextsInClipboard()));
			if (direction > 0) {
				if (indexOfCurrentSelectedString == CopiedApplication
						.getNumberOfTextsInClipboard() - 1) {
					computedString = CopiedApplication.getClipboarString(0);
				} else {
					computedString = CopiedApplication
							.getClipboarString(indexOfCurrentSelectedString + 1);
				}
			} else if (direction < 0) {
				if (indexOfCurrentSelectedString == 0) {
					computedString = CopiedApplication
							.getClipboarString(CopiedApplication
									.getNumberOfTextsInClipboard() - 1);
				} else {
					computedString = CopiedApplication
							.getClipboarString(indexOfCurrentSelectedString - 1);
				}
			}
		}
		Log.d(TAG_LOG, computedString);
		return computedString;
	}

	private void createNotif(String textString, boolean dismissCurrent) {
		if (dismissCurrent) {
			notificationManager.cancelAll();
		}
		if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 16
				&& SharedPreferencesUtil
						.getInstance(getApplicationContext())
						.getSetting(
								SharedPreferencesUtil.SETTING_NOTIFICATION_BIG_VIEW)) {
			createBigStyleNotification(textString);
		} else {
			createNormalNotification(textString);
		}
	}

	private void createBigStyleNotification(String textString) {
		String textForNotification = textString.substring(0, textString
				.length() > MAX_TOAST_TEXT_LENGHT ? MAX_TOAST_TEXT_LENGHT
				: textString.length());
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				getApplicationContext())
				.setContentTitle(getString(R.string.app_name))
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentText(
						textForNotification.length() == textString.length() ? textForNotification
								: textForNotification + "...")
				.setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(textString));
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_OPEN_ACTIVITY);
		PendingIntent pIntentOpenActivity = PendingIntent.getActivity(getBaseContext(),
				INTENT_COMMAND_TYPE_OPEN_ACTIVITY, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		intent = new Intent(this, ChangeNotificationTextService.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_PREVIOUS);
		PendingIntent pIntentPrevious = PendingIntent.getService(getBaseContext(),
				INTENT_COMMAND_TYPE_PREVIOUS, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		intent = new Intent(this, ChangeNotificationTextService.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_NEXT);
		PendingIntent pIntentNext = PendingIntent.getService(getBaseContext(),
				INTENT_COMMAND_TYPE_NEXT, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		if (clipBoardListener == null) {
			intent = new Intent(this, ChangeNotificationTextService.class);
			intent.putExtra(INTENT_COMMAND_TYPE,
					INTENT_COMMAND_TYPE_API_18_ERROR_CASE);
			PendingIntent pIntent = PendingIntent.getService(getBaseContext(),
					INTENT_COMMAND_TYPE_API_18_ERROR_CASE, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			Notification notification = notificationBuilder.setContentIntent(pIntent)
					.build();
			completeAndShowNotification(notification);
			return;
		}

		PendingIntent pIntentCopy = null;
		if (!textString.contains(CopiedApplication.DEFAULT_STRING)) {
			intent = new Intent(this, TransparentActivity.class);
			intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_COPY);
			pIntentCopy = PendingIntent.getActivity(getBaseContext(),
					INTENT_COMMAND_TYPE_COPY, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		}

		if (CopiedApplication.getNumberOfTextsInClipboard() >= 2) {
			notificationBuilder
				.addAction(R.drawable.icon_previous, getString(R.string.previous_text), pIntentPrevious)
				.addAction(0, getString(R.string.copy), pIntentCopy)
				.addAction(R.drawable.icon_next, getString(R.string.next_text), pIntentNext);
		} else if (CopiedApplication.getNumberOfTextsInClipboard() == 1
				&& !CopiedApplication.getClipboarString(0).contains(
						CopiedApplication.DEFAULT_STRING)) {
			notificationBuilder
				.addAction(0, getString(R.string.copy), pIntentCopy);
		}

		Notification notification = notificationBuilder.setContentIntent(pIntentOpenActivity)
				.build();
		completeAndShowNotification(notification);
	}

	private void createNormalNotification(String textString) {
		Log.d(TAG_LOG, ">>createNotif() " + textString + " all texts: "
				+ CopiedApplication.getList());
		RemoteViews rv = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		rv.setTextViewText(
				R.id.text_body,
				clipBoardListener != null ? textString
						: textString
								+ " "
								+ getString(R.string.clipdata_not_listening_for_changes));

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_OPEN_ACTIVITY);
		PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(),
				INTENT_COMMAND_TYPE_OPEN_ACTIVITY, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.notification_layout_parent, pIntent);

		intent = new Intent(this, ChangeNotificationTextService.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_PREVIOUS);
		pIntent = PendingIntent.getService(getBaseContext(),
				INTENT_COMMAND_TYPE_PREVIOUS, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.text_previous, pIntent);

		intent = new Intent(this, ChangeNotificationTextService.class);
		intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_NEXT);
		pIntent = PendingIntent.getService(getBaseContext(),
				INTENT_COMMAND_TYPE_NEXT, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.text_next, pIntent);

		if (clipBoardListener == null) {
			intent = new Intent(this, ChangeNotificationTextService.class);
			intent.putExtra(INTENT_COMMAND_TYPE,
					INTENT_COMMAND_TYPE_API_18_ERROR_CASE);
			pIntent = PendingIntent.getService(getBaseContext(),
					INTENT_COMMAND_TYPE_API_18_ERROR_CASE, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.text_body, pIntent);
		}

		if (!textString.contains(CopiedApplication.DEFAULT_STRING)) {
			intent = new Intent(this, TransparentActivity.class);
			intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_COPY);
			pIntent = PendingIntent.getActivity(getBaseContext(),
					INTENT_COMMAND_TYPE_COPY, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.text_copy, pIntent);

			intent = new Intent(this, TransparentActivity.class);
			intent.putExtra(INTENT_COMMAND_TYPE, INTENT_COMMAND_TYPE_CUT);
			pIntent = PendingIntent.getActivity(getBaseContext(),
					INTENT_COMMAND_TYPE_CUT, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.text_cut, pIntent);
		}

		if (CopiedApplication.getNumberOfTextsInClipboard() < 2) {
			rv.setViewVisibility(R.id.text_next, View.GONE);
			rv.setViewVisibility(R.id.text_previous, View.GONE);
			if (CopiedApplication.getNumberOfTextsInClipboard() == 1
					&& !CopiedApplication.getClipboarString(0).contains(
							CopiedApplication.DEFAULT_STRING)) {
				rv.setViewVisibility(R.id.text_copy, View.VISIBLE);
				rv.setViewVisibility(R.id.text_cut, View.VISIBLE);
			} else {
				rv.setViewVisibility(R.id.text_copy, View.GONE);
				rv.setViewVisibility(R.id.text_cut, View.GONE);
			}
		} else {
			rv.setViewVisibility(R.id.text_next, View.VISIBLE);
			rv.setViewVisibility(R.id.text_previous, View.VISIBLE);
			rv.setViewVisibility(R.id.text_copy, View.VISIBLE);
			rv.setViewVisibility(R.id.text_cut, View.VISIBLE);
		}

		// Build notification
		Notification notification = new NotificationCompat.Builder(this)
				.setContent(rv).setSmallIcon(R.drawable.ic_launcher).build();
		completeAndShowNotification(notification);
	}

	private void completeAndShowNotification(Notification notification) {
		if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 16) {
			notification.priority = SharedPreferencesUtil.getInstance(
					getApplicationContext()).getSetting(
					SharedPreferencesUtil.SETTING_DISPLAY_NOTIFICATION) ? Notification.PRIORITY_DEFAULT
					: Notification.PRIORITY_MIN;
		}
		if (SharedPreferencesUtil.getInstance(getApplicationContext()).getSetting(SharedPreferencesUtil.SETTING_NOTIFICATION_NO_CLEAR)) {
			notification.flags |= Notification.FLAG_NO_CLEAR;
		}
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	private class ClipboardListener implements
			ClipboardManager.OnPrimaryClipChangedListener {

		@Override
		public void onPrimaryClipChanged() {
			ClipData clipData = clipBoard.getPrimaryClip();
			String clipText = "";
			if (clipData != null && clipData.getItemCount() > 0
					&& clipData.getItemAt(0).getText() != null) {
				clipText = clipData.getItemAt(0).getText().toString();
				if (clipText != null) {
					if (!CopiedApplication.clipboarStringsContain(clipText)) {
						CopiedApplication.addStringToClipboard(clipText);
						createNotif(clipText, false);
						String textToBeDisplayed = clipText
								.substring(
										0,
										clipText.length() > MAX_TOAST_TEXT_LENGHT ? MAX_TOAST_TEXT_LENGHT
												: clipText.length());
						if (SharedPreferencesUtil.getInstance(
								getApplicationContext()).getSetting(
								SharedPreferencesUtil.SETTING_DISPLAY_TOAST)) {
							Toast.makeText(
									getApplicationContext(),
									getString(
											R.string.text_copied,
											textToBeDisplayed.length() == clipText
													.length() ? textToBeDisplayed
													: textToBeDisplayed + "..."),
									Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			Log.i(TAG_LOG, "<<onPrimaryClipChanged() - clipData: " + clipData
					+ " clipText: " + clipText);
		}
	}

}
