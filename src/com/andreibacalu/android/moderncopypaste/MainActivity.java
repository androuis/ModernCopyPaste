package com.andreibacalu.android.moderncopypaste;

import com.andreibacalu.android.moderncopypaste.R;
import com.andreibacalu.android.moderncopypaste.services.ChangeNotificationTextService;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.hello_world);
		textView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//addNotification();
				getBaseContext().startService(new Intent(getBaseContext(), ChangeNotificationTextService.class));
			}
		});
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
		
		RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification_layout);
		rv.setTextViewText(R.id.text_body, "test");

		// Build notification
		// Actions are just fake
		Notification noti = new NotificationCompat.Builder(this)
			.setContent(rv)
			.setSmallIcon(R.drawable.ic_launcher)	
			.build();
		
		rv.setOnClickPendingIntent(R.id.text_previous, pIntent);
		  
		NotificationManager notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, noti); 
	}

	
	
}
