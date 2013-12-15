package com.andreibacalu.android.copied.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.andreibacalu.android.copied.R;
import com.andreibacalu.android.copied.R.drawable;
import com.andreibacalu.android.copied.R.id;
import com.andreibacalu.android.copied.R.layout;
import com.andreibacalu.android.copied.R.menu;
import com.andreibacalu.android.copied.services.ChangeNotificationTextService;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getBaseContext().startService(
				new Intent(getBaseContext(),
						ChangeNotificationTextService.class));
		finish();
		/*
		 * setContentView(R.layout.activity_main); textView = (TextView)
		 * findViewById(R.id.hello_world); textView.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { //addNotification();
		 * 
		 * ClipboardManager clipboardManager = (ClipboardManager)
		 * getSystemService(CLIPBOARD_SERVICE);
		 * clipboardManager.setPrimaryClip(ClipData
		 * .newPlainText("label vizibil", "copiat")); } });
		 */
	}

	public void nextClicked(View view) {
		Toast.makeText(getBaseContext(), "dsadad", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	protected void addNotification() {
		// Prepare intent which is triggered if the
		// notification is selected

		Intent intent = new Intent(this, NotificationHandler.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		RemoteViews rv = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		rv.setTextViewText(R.id.text_body, "test");

		// Build notification
		// Actions are just fake
		Notification noti = new NotificationCompat.Builder(this).setContent(rv)
				.setSmallIcon(R.drawable.ic_launcher).build();

		rv.setOnClickPendingIntent(R.id.text_previous, pIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, noti);
	}

}
